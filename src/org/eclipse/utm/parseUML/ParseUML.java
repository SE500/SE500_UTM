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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.eclipse.acceleo.common.utils.ModelUtils;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.utm.parseUML.main.Generate;
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
	
	public static void main(String[] args) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(org.eclipse.uml2.uml.UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put("http://www.eclipse.org/uml2/4.0.0/UML", UMLPackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		File modelFile = new File("D:/Users/Stew/Documents/GIT/SE500_UTM/test/ExtendedPO2.uml");
		EObject model = ModelUtils.load(modelFile, resourceSet);
		String userDir = System.getProperty("user.dir","temp");
		System.out.println(userDir);
		System.out.println(System.getProperty("user.home"));
		String filename = userDir + "/temp/generated_model_src";
		File file = new File(filename);
		Generate uml2java = new Generate(model, file, new ArrayList<Object>());
		uml2java.doGenerate(new BasicMonitor());
	}
	/*
	 *  @method Process the generated UML files into data structures
	 */
	public void processFiles() {
		
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
