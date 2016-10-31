/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Thomas Colborne (ERAU) 
 ******************************************************************************/
package org.eclipse.utm.parseUML;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.acceleo.common.utils.ModelUtils;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.utm.UTMDB;
import org.eclipse.utm.parseUML.main.Generate;

import com.google.common.base.Splitter;
/**
 * @author Thomas Colborne
 *
 */
//@SuppressWarnings("restriction")
public class ParseUML {
	
	/*
	 * @constructor 
	 * 
	 */
//	public ParseUML() {
//		
//	}
	private static boolean testCleanUp = true;
	
	private static UTMDB db = new UTMDB();
	
	private static File modelFile;
	
	public static void main(String[] args) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(org.eclipse.uml2.uml.UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put("http://www.eclipse.org/uml2/4.0.0/UML", UMLPackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		//File modelFile = new File("D:/Users/Stew/Documents/GIT/SE500_UTM/test/ExtendedPO2.uml");
		modelFile = selectUmlFile();
		if(modelFile != null) {
			EObject model = ModelUtils.load(modelFile, resourceSet);
			String userDir = System.getProperty("user.dir","temp");
			//System.out.println(userDir);
			//System.out.println(System.getProperty("user.home"));
			String tempGenFolderPath = userDir + "/temp/generated_model_src";
			File tempGenFolder = new File(tempGenFolderPath);
			Generate uml2java = new Generate(model, tempGenFolder, new ArrayList<Object>());
			uml2java.doGenerate(new BasicMonitor());
			processGenDir(tempGenFolder);
			
			if(testCleanUp) {
				if(removeDirectory(new File(tempGenFolder.getParent()))) {
					System.out.println("Temporary Generated Files Removed");
				}
			}
		}
		else {
			System.err.println("Error: No UML File selected.");
		}
		System.exit(0);		
	}
	
	/*
	 * @method Select a UML file
	 */
	public static File selectUmlFile(){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("UML File","uml");
		fileChooser.setFileFilter(filter);
		int result = fileChooser.showOpenDialog(new JFrame());
		if (result == JFileChooser.APPROVE_OPTION) {
		    return fileChooser.getSelectedFile();
		}
		return null;
	}
	
	/*
	 *  @method Process the generated UML files within directory
	 */
	public static boolean processGenDir(File directory) {
		if (directory == null||!directory.exists()||!directory.isDirectory())
			return false;
		
		String[] list = directory.list();

		// Some JVMs return null for File.list() when the directory is empty.
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				if (entry.isDirectory())
				{
					if (!processGenDir(entry))
						return false;
				} 
				else
				{
					try {
						if (!processGenFile(entry))
							return false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		return true;
	}
	
	/*
	 * @method Process each individually generated file
	 */
	public static boolean processGenFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		 
		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 
		String line = null;
		int lineNumber = 0;
		while ((line = br.readLine()) != null) {
			lineNumber++;
			if(!processGenFileLine(line, lineNumber)){
				br.close();
				return false;
			}
				
		}
	 
		br.close();	
		
		return true;
	}
	
	/*
	 * @method Process each line within a generated file and store it appropriately
	 */
	public static boolean processGenFileLine(String line, int lineNumber) {
		//Splitter stringSplitter = Splitter.on(' ').omitEmptyStrings().trimResults();
		
		//System.out.printf("Start Line: %1d\n", lineNumber);
		//Iterable<String> tokens = stringSplitter.split(line);
		//int i = 0;
		String[] tokens = line.split("\\s");
		System.out.printf("%1$s %2$s %3$d\n",tokens[0], tokens[1], tokens.length);
		for(String token : tokens){
			System.out.println(token.trim());
		}
		switch (tokens[0]) {
			case "class" :
				if(tokens.length == 3) {
					db.Open();
					db.NewUMLClass(modelFile.getName(), tokens[1].trim(), tokens[2].trim(), false, false, false);
					db.Close();
				}
				break;
			case "attribute" :
				if(tokens.length == 5) {
					db.Open();
					db.NewUMLAttribute(modelFile.getName(), tokens[1].trim(), tokens[2].trim(), tokens[3].trim(), tokens[4].trim());
					db.Close();
				}
				break;
			case "method" :
				if(tokens.length == 5) {
					db.Open();
					db.NewUMLMethod(modelFile.getName(), tokens[1].trim(), tokens[2].trim(), tokens[3].trim(), tokens[4].trim(), "");
					db.Close();
				}
				break;
			default:
				System.err.println("Error: Unknown model translation");
		}
		
		return true;
	}
	
	
	/*
	 * @method Cleanup after parsing a UML file by deleting any temporary files created.
	 */
	public void cleanUp() {
		
	}
	
	/*
	 * @method Removes a directory from the file system
	 */
	public static boolean removeDirectory(File directory) {

		// System.out.println("removeDirectory " + directory);
		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();

		// Some JVMs return null for File.list() when the
		// directory is empty.
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				//System.out.println("\tremoving entry " + entry);

				if (entry.isDirectory())
				{
					if (!removeDirectory(entry))
						return false;
				} 
				else
				{
					if (!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}
}
