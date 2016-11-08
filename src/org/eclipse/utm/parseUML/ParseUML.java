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
import javax.swing.UIManager;
//import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.acceleo.common.utils.ModelUtils;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.utm.compare.UTMDB;
import org.eclipse.utm.parseUML.main.Generate;
//import com.google.common.base.Splitter;
/**
 * @author Thomas Colborne
 * 
 * A class that parses a UML file and enters relevant information into
 * the UTMDB - an SQL database
 *
 */
public class ParseUML {
	
	private File modelFile;
	private File tempGenFolder = new File(System.getProperty("user.dir","temp")
			+ "/temp/generated_model_src");
	private EObject model;
	private UTMDB db = null;
	
	/**
	 * Empty constructor
	 * @constructor  
	 * If used initialize must be called before launch
	 */
	public ParseUML() {
			
	}
	/**
	 * Constructor with a passed model file
	 * @param model
	 */
	public ParseUML(File model){
		initialize(model);
	}
		
	/**
	 * Launch the process to parse the UML into the SQL database
	 * @throws IOException
	 */
	public boolean launch() throws IOException {
		Generate uml2java = new Generate(this.model, this.tempGenFolder, new ArrayList<Object>());
		uml2java.doGenerate(new BasicMonitor());
		return processGenDir(this.tempGenFolder);		
	}
	
	/**
	 * Launch the process to parse the UML into the SQL database
	 * and cleans up temporary files if argument is true
	 * @param withCleanUp
	 * @throws IOException
	 */
	public boolean launch(boolean withCleanUp) throws IOException {
		Generate uml2java = new Generate(this.model, this.tempGenFolder, new ArrayList<Object>());
		uml2java.doGenerate(new BasicMonitor());
		boolean success = processGenDir(this.tempGenFolder);
		if(withCleanUp)
			this.cleanUp();
		return success;
	}
	
	
	/**
	 * This method initializes the ParseUML object
	 * @param model
	 */
	public void initialize(File model) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(org.eclipse.uml2.uml.UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put("http://www.eclipse.org/uml2/4.0.0/UML", UMLPackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		this.modelFile = model;
		if(this.modelFile != null) {
			try {
				this.model = ModelUtils.load(this.modelFile, resourceSet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.db = new UTMDB();
		this.db.Open();
		this.db.InitDatabase();
	}	
	
	
	/**
	 * Select a UML file
	 * @return the UML file selected or null
	 */
	public static File selectUmlFile(){
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }catch(Exception ex) {
	        ex.printStackTrace();
	    }
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fileChooser.setFileFilter(new FileNameExtensionFilter("UML File","uml"));
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
		    return fileChooser.getSelectedFile();
		}
		return null;
	}
	
	/**
	 * Process the generated UML files within directory
	 * @param directory
	 * @return true if the directory is deleted or false if deletion fails
	 */
	private boolean processGenDir(File directory) {
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
			//this.db.Relate();
			//this.db.Match();
			//this.db.Commit();
			//System.out.println("UML Class Count:" + this.db.CountUMLClasses());
			//System.out.println("UML Attribute Count:" + this.db.CountUMLAttributes());
			//System.out.println("UML Method Count:" + this.db.CountUMLMethods());
		}
		return true;
	}
	
	/**
	 * Process each individually generated file
	 * @param file
	 * @return 	true if all lines are processed successfully 
	 *			false if line fails to be processed
	 * @throws IOException
	 */
	private boolean processGenFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		 
		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 
		String line = null;
		int lineNumber = 0;
		while ((line = br.readLine()) != null) {
			lineNumber++;
			if(!processGenFileLine(line)){
				System.err.printf("File %1$s: Line %2$d: Line Processing Failed",
						file.getName(), lineNumber);
				br.close();
				return false;
			}
				
		}
		br.close();	
		return true;
	}
	
	/**
	 * Process each line within a generated file and store it appropriately
	 * @param line
	 * @return
	 */
	private boolean processGenFileLine(String line) {
		
		String[] tokens = line.split("\\s");
		/*System.out.printf("%1$s %2$s %3$d\n",tokens[0], tokens[1], tokens.length);
		for(String token : tokens){
			System.out.println(token.trim());
		}*/
		switch (tokens[0]) {
			case "class" :
				if(tokens.length == 3) {
					this.db.NewUMLClass(modelFile.getName(), tokens[1].trim(), tokens[2].trim(), false, false, false);
				}
				break;
			case "attribute" :
				if(tokens.length == 5) {
					this.db.NewUMLAttribute(modelFile.getName(), tokens[1].trim(), tokens[2].trim(), tokens[3].trim(), tokens[4].trim());
				}
				break;
			case "method" :
				if(tokens.length == 5) {
					this.db.NewUMLMethod(modelFile.getName(), tokens[1].trim(), tokens[2].trim(), tokens[3].trim(), tokens[4].trim(), "");
				}
				break;
			default:
				System.err.println("Error: Unknown model translation");
		}
		return true;
	}
	
	
	/**
	 * Cleanup after parsing a UML file by deleting any temporary files created
	 * and closing the database.
	 */
	public void cleanUp() {
		removeDirectory(new File(this.tempGenFolder.getParent()));
		//System.out.println("Temporary Generated Files Removed");
		this.db.Commit();
		this.db.Close();
	}
	
	/**
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
