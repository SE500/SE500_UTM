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
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.emf.common.util.URI;
import org.eclipse.utm.UTMActivator;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Thomas Colborne
 * 
 * A class that parses a UML file and enters relevant information into
 * the UTMDB - an SQL database
 *
 */
public class ParseUML extends Job{
	
	/**
	 * The UML model file used for input processing
	 */
	private File modelFile;

	/**
	 * The DebugUI launch configuration used to launch the UML 2 Java generator.
	 */
	ILaunchConfiguration config;
	
	/**
     * The type of launch configuration for the UML 2 Java generator.
     */
    String LAUNCH_CONFIGURATION_TYPE = "org.eclipse.umlgen.gen.java.ui.launchConfigurationType";
	
	/**
	 * Empty constructor
	 * If used initialize must be called before launch
	 */
	public ParseUML() {
		super("Parsing the UML");
		setUser(true);
	}
	/**
	 * Constructor with a passed model file
	 * @param model
	 * 		The model to be parsed
	 */
	public ParseUML(File model){
		super("Parsing the UML");
		setUser(true);
		initialize(model);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
	 */
	@Override
	public boolean belongsTo(Object family) {
		return family.equals(UTMActivator.UTM_JOB_FAMILY);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(java.lang.Object)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			 // Launch it
	        if (this.config != null && this.config.exists()) {
	        	UTMActivator.log("Generating Java Code from UML ... This takes time ... PLEASE WAIT");
	        	DebugUITools.buildAndLaunch(config, ILaunchManager.RUN_MODE, monitor);
	        }
		} catch (CoreException e) {
			IStatus status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID, e.getMessage(), e);
			UTMActivator.getDefault().getLog().log(status);
			e.printStackTrace();
			return status;
		}
		return Status.OK_STATUS;
	}
		
	/**
	 * A launcher for the ParseUML Job
	 * @param monitor
	 * 		The progress monitor for the job
	 * @return
	 * 		True if the job runs successfully
	 * 		False if there is an internal error
	 */
	public boolean launch(IProgressMonitor monitor) {
		if(this.run(monitor) == Status.OK_STATUS)
			return true;
		return false;
	}	
	
	/**
	 * This method initializes the ParseUML object
	 * @param model
	 * 		The model to be parsed
	 */
	public void initialize(File model) {
		UTMActivator.log("Initialising prior to parsing the UML Model...");
		
		this.modelFile = model;
		
		if(this.modelFile != null) {
			// Finds or creates a launch configuration for the UML model.
			this.config = this.findLaunchConfiguration();
	        if (this.config == null) {
	        	this.config = this.createConfiguration();
	        }
		}
	}	
	
	
	/**
	 * Select a UML file
	 * @return the UML file selected or null
	 */
	public static File selectUmlFile(){
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }catch(Exception e) {
	    	IStatus status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID, e.getMessage(), e);
			UTMActivator.getDefault().getLog().log(status);
	        e.printStackTrace();
	    }
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile());
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
	 * 		The directory to be processed
	 * @return true if the directory is deleted or false if deletion fails
	 * @deprecated
	 * 		This is no longer used due to newly created dependency on org.eclipse.umlgen.gen.java
	 */
	@SuppressWarnings("unused")
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
						IStatus status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID, e.getMessage(), e);
						UTMActivator.getDefault().getLog().log(status);
						e.printStackTrace();
					}

				}

			}
		}
		return true;
	}
	
	/**
	 * Process each individually generated file
	 * @param file
	 * 		The file to process
	 * @return 	true if all lines are processed successfully 
	 *			false if line fails to be processed
	 * @throws IOException
	 * 		If the file to process cannot be read and exception is thrown
	 * @deprecated
	 * 		This is no longer used due to newly created dependency on org.eclipse.umlgen.gen.java
	 */
	private boolean processGenFile(File file) throws IOException {
				 
		//Construct BufferedReader from FileReader
		BufferedReader br = new BufferedReader(new FileReader(file));
	 
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
	 * 		The line to process
	 * @return 
	 * 		True if successful false if not
	 * @deprecated
	 * 		This is no longer used due to newly created dependency on org.eclipse.umlgen.gen.java
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
					//this.db.NewUMLClass(modelFile.getName(), tokens[1].trim(), tokens[2].trim(), false, false, false);
				}
				break;
			case "attribute" :
				if(tokens.length == 5) {
					//this.db.NewUMLAttribute(modelFile.getName(), tokens[1].trim(), tokens[2].trim(), tokens[3].trim(), tokens[4].trim());
				}
				break;
			case "method" :
				if(tokens.length == 5) {
					//this.db.NewUMLMethod(modelFile.getName(), tokens[1].trim(), tokens[2].trim(), tokens[3].trim(), tokens[4].trim(), "");
				}
				break;
			default:
				System.err.println("Error: Unknown model translation");
		}
		return true;
	}
	
	/**
	 * Removes a directory from the file system
	 * @param directory
	 * 			The directory to be removed
	 * @return
	 * 		True if the directory is removed
	 * 		False otherwise
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
	/**
	 * Utility method to convert a File type to IFIle type
	 * @param file
	 * 		The File to be converted
	 * @return
	 * 		The IFile resource for file
	 */
	public static IFile getIFile(File file) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();   
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile ifile = workspace.getRoot().getFileForLocation(location);
		return ifile;
	}
	
	/**
	 * Utility method to convert an EObject type to IFIle type
	 * @param obj
	 * 		The EObject to be converted
	 * @return
	 * 		The IFile resource for obj
	 */
	public static IFile getIFile(EObject obj) {
		URI uri = obj.eResource().getURI();
		String fileString = URI.decode(uri.path());
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileString));
		return file;
	}
	
	/**
	 * Utility method to convert an URL type to IFIle type
	 * @param url
	 * 		The URL to be converted
	 * @return
	 * 		The IFile resource for url
	 */
	public static IFile getIFile(URL url) {
		java.net.URI uri;
		try {
			uri = url.toURI();
			String fileString = URI.decode(uri.getPath());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileString));
			return file;
		} catch (URISyntaxException e) {
			IStatus status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID, e.getMessage(), e);
			UTMActivator.getDefault().getLog().log(status);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
     * Returns a newly created launch configuration for the available ".uml" models.
     * 
     * @return A newly created launch configuration for the available ".uml" models.
     */
    protected ILaunchConfiguration createConfiguration() {
    	ILaunchConfiguration config = null;
        ILaunchConfigurationWorkingCopy wc = null;
    	try {
    		ILaunchManager launchMgr = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType launchType = launchMgr.getLaunchConfigurationType(this.LAUNCH_CONFIGURATION_TYPE);
			wc = launchType.newInstance(null, launchMgr.generateLaunchConfigurationName("UTM"));
 
			wc.setAttribute("uml_model_path", getIFile(this.modelFile).getFullPath().toString());
			wc.setAttribute("author",  "UML Trace Magic");
			wc.setAttribute("bundle_provider",  "UML Trace Magic");
			wc.setAttribute("default_project_name",  "org.eclipse.utm.u2j");
			wc.setAttribute("jre_execution_environment",  "JavaSE-1.8");
			wc.setAttribute("output_folder_path",  "target");
			wc.setAttribute("source_folder_path",  "src");
			wc.setAttribute("version",  "1.0.0.temp");
			wc.setAttribute("components_architecture", "Eclipse Plugins, Features and Update Sites");
			wc.setAttribute("components_ignore", "java, ");
			wc.setAttribute("copyright_license", "2016, All rights reserved.");
			wc.setAttribute("generate_advanced_accessors_collections", false);
			wc.setAttribute("generate_getters_collections", true);
			wc.setAttribute("generate_getters_setters", true);
			wc.setAttribute("generate_setters_collections", false);
			wc.setAttribute("ignore_java_types_during_generation_and_import", true);
			wc.setAttribute("jre_execution_environment", "JavaSE-1.8");
			wc.setAttribute("not_ordered_not_unique", "java.util.ArrayList");
			wc.setAttribute("not_ordered_unique", "java.util.HashSet");
			wc.setAttribute("ordered_not_unique", "java.util.ArrayList");
			wc.setAttribute("ordered_unique", "java.util.LinkedHashSet");
			wc.setAttribute("packages_ignore_generation", "java, ");
			wc.setAttribute("packages_ignore_imports", "java.lang, ");
			wc.setAttribute("types_to_ignore_during_generation", "");
			wc.setAttribute("types_to_ignore_during_imports", "");

			config = wc.doSave();

		} catch (CoreException e) {
			IStatus status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID, e.getMessage(), e);
			UTMActivator.getDefault().getLog().log(status);
			e.printStackTrace();
		}
        return config;
    }

    /**
     * Returns the first UML to Java launch configuration using all the selected ".uml" models.
     * 
     * @return The first UML to Java launch configuration using all the selected ".uml" models.
     */
    protected ILaunchConfiguration findLaunchConfiguration() {
        String computedModelPath = getIFile(this.modelFile).getFullPath().toString();

        ILaunchConfigurationType configurationType = DebugPlugin.getDefault().getLaunchManager()
                .getLaunchConfigurationType(this.LAUNCH_CONFIGURATION_TYPE);
        try {
            ILaunchConfiguration[] launchConfigurations = DebugPlugin.getDefault().getLaunchManager()
                    .getLaunchConfigurations(configurationType);
            for (ILaunchConfiguration iLaunchConfiguration : launchConfigurations) {
                String modelPath = iLaunchConfiguration.getAttribute("uml_model_path", "");
                String name = iLaunchConfiguration.getName();
                
                if (modelPath != null && modelPath.equals(computedModelPath) && name.contains("UTM")) {
                    return iLaunchConfiguration;
                }
            }
        } catch (CoreException e) {
            IStatus status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID, e.getMessage(), e);
            UTMActivator.getDefault().getLog().log(status);
            e.printStackTrace();
        }
        return null;
    }
}
