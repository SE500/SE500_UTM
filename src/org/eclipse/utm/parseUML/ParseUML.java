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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
//import org.eclipse.acceleo.common.utils.ModelUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.emf.common.util.URI;
import org.eclipse.utm.UTMActivator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
//import org.eclipse.emf.ecore.resource.ResourceSet;
//import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
//import org.eclipse.uml2.uml.UMLPackage;
//import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.utm.compare.UTMDB;
import org.eclipse.utm.parseSource.ParseSource;
//import org.eclipse.utm.parseUML.main.Generate;
//import com.google.common.base.Splitter;
//import org.eclipse.utm.parseUML.services.UML2JavaConfigurationHolder;
//import org.eclipse.utm.parseUML.utils.IUML2JavaConstants;

/**
 * @author Thomas Colborne
 * 
 * A class that parses a UML file and enters relevant information into
 * the UTMDB - an SQL database
 *
 */
public class ParseUML extends Job{
	
	private File modelFile;
	private File tempGenFolder;
//	private IFile defaultConfig;
//	private EObject model;
//	private URI modelURI;
	private UTMDB db;
//	private UML2JavaConfigurationHolder configuration;
	ILaunchConfiguration config;
	
	/**
     * The type of launch configuration for the UML 2 Java generator.
     */
    String LAUNCH_CONFIGURATION_TYPE = "org.eclipse.umlgen.gen.java.ui.launchConfigurationType";
	
	/**
	 * Empty constructor
	 * @constructor  
	 * If used initialize must be called before launch
	 */
	public ParseUML() {
		super("Parsing the UML");
		setUser(true);
	}
	/**
	 * Constructor with a passed model file
	 * @param model
	 */
	public ParseUML(File model){
		super("Parsing the UML");
		setUser(true);
		initialize(model);
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		final int ticks = 100;
		
		// Convert the given monitor into a progress instance 
        SubMonitor progress = SubMonitor.convert(monitor, "UML Parsing begins", ticks);
        
		String generatedSourcePath = this.tempGenFolder.getAbsolutePath() + System.getProperty("file.separator") 
			+ "org.eclipse.utm.u2j" + System.getProperty("file.separator") 
			+ "src";
//		IFile generatedSource = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(generatedSourcePath));
		ParseSource parseGeneratedSourceJob;
//		monitor.beginTask("UML Parsing begins", ticks);
		try {
			if (progress.isCanceled())
				return Status.CANCEL_STATUS;
			progress.subTask("Generating Java Code from UML");
			 // Launch it
	        if (this.config != null && this.config.exists()) {
	        	UTMActivator.log("Generating Java Code from UML ... This takes time ... PLEASE WAIT");
	        	ILaunch launch = DebugUITools.buildAndLaunch(config, ILaunchManager.RUN_MODE, progress.split(50));
	        }
//			IProcess[] processes = launch.getProcesses();
//			if(processes.length == 0) {
//				UTMActivator.log("WHAT!!! No processes created by the launch!");
//			} else if(processes.length == 1) {
//				UTMActivator.log("Just one process created by the launch: "
//						+ processes[0].getLabel());
//				IProgressMonitor test = processes[0].getAdapter(IProgressMonitor.class);
//				UTMActivator.log("The Progress Monitor: " + test.toString());
//			} else {
//				UTMActivator.log("Multiple Processes: ");
//				for (IProcess process : processes) {
//					UTMActivator.log(process.getLabel());
//				}
//			}
//			monitor.worked(1);
			if (progress.isCanceled())
				return Status.CANCEL_STATUS;
			progress.subTask("Parsing generated Java Code");
//			File generatedSource = new File(generatedSourcePath);
			parseGeneratedSourceJob = new ParseSource(generatedSourcePath, this.modelFile.getName());
//			UTMActivator.log(generatedSource.getLocation().toOSString());
//			int count = 0;
//			int divisor = 1000;
//			while(!launch.isTerminated()) {
//				count++;
//				if((count/divisor) == 1) {
//					UTMActivator.log("Looped: " + divisor);;
//					divisor += 1000;
//				}
//			}
			parseGeneratedSourceJob.schedule(10000);
			parseGeneratedSourceJob.join();
			progress.worked(50);
//			monitor.worked(2);
		} catch (CoreException e) {
			IStatus status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID, e.getMessage(), e);
			UTMActivator.getDefault().getLog().log(status);
			e.printStackTrace();
			return status;
			
		} catch (InterruptedException e) {
			IStatus status = new Status(IStatus.WARNING, UTMActivator.PLUGIN_ID, e.getMessage(), e);
			UTMActivator.getDefault().getLog().log(status);
			e.printStackTrace();
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
		
	/**
	 * Launch the process to parse the UML into the SQL database
	 */
	public boolean launch(IProgressMonitor monitor) {
		if(this.run(monitor) == Status.OK_STATUS)
			return true;
		return false;
//		Uml2java generation;
//		UTMActivator.log(generatedSourcePath);
//		ISelection selection = (ISelection) this.model;
//		LaunchShortcut shortcut = new LaunchShortcut();
//		shortcut.launch(selection, ILaunchManager.ATTR_PRIVATE);
//		IProgressMonitor monitor = new IProgressMonitor){ };
//		try {
//			config.launch(ILaunchManager.DEBUG_MODE, new NullProgressMonitor());
//		} catch (CoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			this.wait(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			generation = new Uml2java(this.modelURI, this.tempGenFolder, new ArrayList<String>());
//			generation.setConfigurationHolder(this.configuration);
//			generation.doGenerate(new BasicMonitor());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		success = processGenDir(this.tempGenFolder);
//		return success;
//		return true;
	}
	
//	public boolean process() {
//		boolean success = false;
//		String generatedSourcePath = this.tempGenFolder.getAbsolutePath() + System.getProperty("file.separator") 
//			+ IUML2JavaConstants.Default.DEFAULT_DEFAULT_PROJECT_NAME + System.getProperty("file.separator") 
//			+ IUML2JavaConstants.Default.DEFAULT_SOURCE_FOLDER_PATH;
//		File generatedSource = new File(generatedSourcePath);
//		UTMActivator.log(generatedSourcePath);
//		ParseSource readfiles = new ParseSource(generatedSource, this.modelFile.getName());
//		success = readfiles.launch();
//		return success;
//	}
	
	/**
	 * Launch the process to parse the UML into the SQL database
	 * and cleans up temporary files if argument is true
	 * @param withCleanUp
	 */
	public boolean launch(IProgressMonitor monitor, boolean withCleanUp){
		if((this.run(monitor) == Status.OK_STATUS) && withCleanUp) {
			this.cleanUp();
			return true;
		}
		return false;
////		Uml2java generation;
//		boolean success = false;
//		String generatedSourcePath = this.tempGenFolder.getAbsolutePath() + System.getProperty("file.separator") 
//			+ IUML2JavaConstants.Default.DEFAULT_DEFAULT_PROJECT_NAME + System.getProperty("file.separator") 
//			+ IUML2JavaConstants.Default.DEFAULT_SOURCE_FOLDER_PATH;
//		File generatedSource = new File(generatedSourcePath);
//		UTMActivator.log(generatedSourcePath);
////		ISelection selection = (ISelection) this.model;
////		LaunchShortcut shortcut = new LaunchShortcut();
////		shortcut.launch(selection, ILaunchManager.ATTR_PRIVATE);
////		try {
////			config.launch(ILaunchManager.DEBUG_MODE, new NullProgressMonitor());
////		} catch (CoreException e) {
//			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		DebugUITools.launch(config, ILaunchManager.RUN_MODE);
////		try {
////			generation = new Uml2java(this.modelURI, this.tempGenFolder, new ArrayList<String>());
////			generation.setConfigurationHolder(this.configuration);
////			generation.doGenerate(new BasicMonitor());
//			ParseSource readfiles = new ParseSource(generatedSource, this.modelFile.getName());
//			success = readfiles.launch();
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////		success = processGenDir(this.tempGenFolder);
//		if(withCleanUp && success)
//			this.cleanUp();
//		return success;
	}
	
	
	/**
	 * This method initializes the ParseUML object
	 * @param model
	 */
	public void initialize(File model) {
		this.db = null;
//		this.tempGenFolder = new File(System.getProperty("user.dir","temp")
//				+ "/temp/generated_model_src");
		this.tempGenFolder = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		
		UTMActivator.log("Initialising prior to parsing the UML Model...");

		
//		this.defaultConfig = getIFile(ParseUML.class.getResource("resources/UTM_Default_Configuration.launch"));
		
//		ResourceSet resourceSet = new ResourceSetImpl();
//		resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
//		resourceSet.getPackageRegistry().put("http://www.eclipse.org/uml2/4.0.0/UML", UMLPackage.eINSTANCE);
//		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		
		this.modelFile = model;
		
//		IFile file = getIFile(model);
//		IContainer container = ResourcesPlugin.getWorkspace().getRoot();
//		if (file != null && container != null && file.isAccessible() && container.isAccessible()) {
//			this.modelURI = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
//			this.configuration = createDefaultConfigurationHolder(file.getFullPath().toString(), this.tempGenFolder.getAbsolutePath());
//			
//			UTMActivator.log("TEST SUCCESS: It is not a file or container issue: " + file.getFullPath().toString());
//		} else {
//			UTMActivator.log("TEST FAILURE: It IS a file or container issue: " + file.getFullPath().toString());
//		}
		
		if(this.modelFile != null) {
//			try {
//				this.model = ModelUtils.load(this.modelFile, resourceSet);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			this.modelURI = URI.createFileURI(this.modelFile.getAbsolutePath());
//			this.configuration = createDefaultConfigurationHolder(this.modelFile.getAbsolutePath(), this.tempGenFolder.getAbsolutePath());
			// Finds or creates a launch configuration for these UML models.
			this.config = this.findLaunchConfiguration();
	        if (this.config == null) {
	        	this.config = this.createConfiguration();
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
	
		
	/**
	 * Creates the configuration holder from the default configuration settings and model path.
	 *
	 * @param modelPath
	 *            Path where the model on which this generator will be used is located.
	 * @param targetFolderPath
	 *            Path for the output folder for this generation : it will be the base path
	 *            against which all file block URLs will be resolved. 
	 * @return The configuration holder from the launch configuration.
	 * 
	 */
//	private UML2JavaConfigurationHolder createDefaultConfigurationHolder(String modelPath, String targetFolderPath) {
//		UML2JavaConfigurationHolder configurationHolder = new UML2JavaConfigurationHolder();
//
//		// General
//		configurationHolder.put(IUML2JavaConstants.UML_MODEL_PATH, modelPath);
//		configurationHolder.put(IUML2JavaConstants.GENERATION_ROOT_PATH, targetFolderPath);
//		configurationHolder.put(IUML2JavaConstants.DEFAULT_PROJECT_NAME,
//				IUML2JavaConstants.Default.DEFAULT_DEFAULT_PROJECT_NAME);
//		configurationHolder.put(IUML2JavaConstants.SOURCE_FOLDER_PATH,
//				IUML2JavaConstants.Default.DEFAULT_SOURCE_FOLDER_PATH);
//		configurationHolder.put(IUML2JavaConstants.OUTPUT_FOLDER_PATH,
//				IUML2JavaConstants.Default.DEFAULT_OUTPUT_FOLDER_PATH);
//		configurationHolder.put(IUML2JavaConstants.JRE_EXECUTION_ENVIRONMENT,
//				IUML2JavaConstants.Default.DEFAULT_JRE_EXECUTION_ENVIRONMENT);
//
//		// Class
//		configurationHolder.put(IUML2JavaConstants.PACKAGES_TO_IGNORE_DURING_GENERATION,
//				IUML2JavaConstants.Default.DEFAULT_PACKAGES_TO_IGNORE_DURING_GENERATION);
//		configurationHolder.put(IUML2JavaConstants.PACKAGES_TO_IGNORE_DURING_IMPORTS,
//				IUML2JavaConstants.Default.DEFAULT_PACKAGES_TO_IGNORE_DURING_IMPORTS);
//		configurationHolder.put(IUML2JavaConstants.GENERATE_GETTERS_AND_SETTERS,
//				IUML2JavaConstants.Default.DEFAULT_GENERATE_GETTERS_AND_SETTERS);
//		configurationHolder.put(IUML2JavaConstants.GENERATE_GETTERS_COLLECTIONS,
//				IUML2JavaConstants.Default.DEFAULT_GENERATE_GETTERS_COLLECTIONS);
//		configurationHolder.put(IUML2JavaConstants.GENERATE_SETTERS_COLLECTIONS,
//				IUML2JavaConstants.Default.DEFAULT_GENERATE_SETTERS_COLLECTIONS);
//		configurationHolder.put(IUML2JavaConstants.GENERATE_ADVANCED_ACCESSORS_COLLECTIONS,
//				IUML2JavaConstants.Default.DEFAULT_GENERATE_ADVANCED_ACCESSORS_COLLECTIONS);
//		configurationHolder.put(IUML2JavaConstants.AUTHOR, IUML2JavaConstants.Default.DEFAULT_AUTHOR);
//		configurationHolder.put(IUML2JavaConstants.VERSION, IUML2JavaConstants.Default.DEFAULT_VERSION);
//		configurationHolder.put(IUML2JavaConstants.COPYRIGHT_AND_LICENSE,
//				IUML2JavaConstants.Default.DEFAULT_COPYRIGHT_AND_LICENSE);
//
//		// Component
//		configurationHolder.put(IUML2JavaConstants.COMPONENTS_TO_IGNORE,
//				IUML2JavaConstants.Default.DEFAULT_COMPONENTS_TO_IGNORE);
//		configurationHolder.put(IUML2JavaConstants.COMPONENTS_ARCHITECTURE,
//				IUML2JavaConstants.Default.DEFAULT_COMPONENT_ARTIFACTS_TYPE_ECLIPSE);
//		configurationHolder.put(IUML2JavaConstants.BUNDLE_PROVIDER,
//				IUML2JavaConstants.Default.DEFAULT_BUNDLE_PROVIDER_NAME);
//
//		// Type
//		configurationHolder.put(IUML2JavaConstants.ORDERED_UNIQUE_TYPE,
//				IUML2JavaConstants.Default.DEFAULT_ORDERED_UNIQUE);
//		configurationHolder.put(IUML2JavaConstants.ORDERED_NOT_UNIQUE_TYPE,
//				IUML2JavaConstants.Default.DEFAULT_NOT_ORDERED_NOT_UNIQUE);
//		configurationHolder.put(IUML2JavaConstants.NOT_ORDERED_UNIQUE_TYPE,
//				IUML2JavaConstants.Default.DEFAULT_NOT_ORDERED_UNIQUE);
//		configurationHolder.put(IUML2JavaConstants.NOT_ORDERED_NOT_UNIQUE_TYPE,
//				IUML2JavaConstants.Default.DEFAULT_NOT_ORDERED_NOT_UNIQUE);
//
//		configurationHolder.put(IUML2JavaConstants.IGNORE_JAVA_TYPES_DURING_GENERATION_AND_IMPORT,
//				IUML2JavaConstants.Default.DEFAULT_IGNORE_JAVA_TYPES_DURING_GENERATION_AND_IMPORT);
//		configurationHolder.put(IUML2JavaConstants.TYPES_TO_IGNORE_DURING_GENERATION,
//				IUML2JavaConstants.Default.DEFAULT_TYPES_TO_IGNORE_DURING_GENERATION);
//		configurationHolder.put(IUML2JavaConstants.TYPES_TO_IGNORE_DURING_IMPORTS,
//				IUML2JavaConstants.Default.DEFAULT_TYPES_TO_IGNORE_DURING_IMPORTS);
//
//		return configurationHolder;
//	}
	
	public static IFile getIFile(File file) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();   
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile ifile = workspace.getRoot().getFileForLocation(location);
		return ifile;

	}
		
	public static IFile getIFile(EObject obj) {
		URI uri = obj.eResource().getURI();
		String fileString = URI.decode(uri.path());
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileString));
		return file;
	}
	
	public static IFile getIFile(URL url) {
		java.net.URI uri;
		try {
			uri = url.toURI();
			String fileString = URI.decode(uri.getPath());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileString));
			return file;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
//	private void copyRequiredResources(IJavaProject project) {
//		
//		
//		try {
//			InputStream inputStream = FileLocator.openStream(
//				    Activator.getDefault().getBundle(), new Path("/resources/UTM_Default_Configuration.launch"), false);
//			
//			
//			inputStream.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	
	/**
     * Returns a newly created launch configuration for the available ".uml" models.
     * 
     * @return A newly created launch configuration for the available ".uml" models.
     */
    protected ILaunchConfiguration createConfiguration() {
    	ILaunchConfiguration config = null;
        ILaunchConfigurationWorkingCopy wc = null;
    	try {
//			if(defaultConfig != null) {
//				ILaunchConfigurationWorkingCopy wc = DebugPlugin.getDefault().getLaunchManager()
//						.getLaunchConfiguration(defaultConfig).getWorkingCopy();
//				wc.setAttribute("uml_model_path", getIFile(this.model).getFullPath().toString());
//				this.config = wc.doSave();
//			}
			ILaunchManager launchMgr = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType launchType = launchMgr.getLaunchConfigurationType(this.LAUNCH_CONFIGURATION_TYPE);
			wc = launchType.newInstance(null, launchMgr.generateLaunchConfigurationName("UTM"));
//			wc.setAttributes(launchAttributes);
//			List<String> = 
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
//			wc.setAttribute("org.eclipse.debug.core.MAPPED_RESOURCE_PATHS", "/org.eclipse.utm.test/models/ExtendedPO2.uml");
//			wc.setAttribute("org.eclipse.debug.core.MAPPED_RESOURCE_TYPES", "1");
//			<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_PATHS">
//			<listEntry value="/org.eclipse.utm.test/models/ExtendedPO2.uml"/>
//			</listAttribute>
//			<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_TYPES">
//			<listEntry value="1"/>
//			</listAttribute>

			config = wc.doSave();
//			ILaunchConfiguration lc = wc.doSave();
//			Launch launch = lc.launch(ILaunchManager.DEBUG_MODE, new NullProgressMonitor());
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

                if (modelPath != null && modelPath.equals(computedModelPath)) {
                    return iLaunchConfiguration;
                }
            }
        } catch (CoreException e) {
            IStatus status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID, e.getMessage(), e);
            UTMActivator.getDefault().getLog().log(status);
        }
        return null;
    }
}
