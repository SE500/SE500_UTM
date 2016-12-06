package org.eclipse.utm.parseSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.utm.UTMActivator;
import org.eclipse.utm.compare.UTMDB;
/**
 * A class that parses source files and enters relevant information into
 * the UTMDB - an SQL database
 * @author Thomas Colborne, Ziyad Daghriri
 * 
 */
public class ParseSource extends Job {

	/*
	 * Variable declarations
	 */
	private File sourceCode;
	private String umlSource = null;
	private String className = null;
	private UTMDB db = null;
	private boolean isUml = false;
	private String umlName = null;
	private int classLinNumber = 0;
	private int methodLinNumber = 0;
	private String declaration ="";

	/**
	 * Constructor for parsing the file or directory
	 * @param source
	 * 		The file or directory to be parsed
	 */
	public ParseSource(File source) {
		super("Parsing the Source Code: "+ source.getName());
		setUser(true);
		this.sourceCode = source;
		UTMActivator.log("\nPreparing to parse Source Code within: " + source + "\n");
	}

	/**
	 * Constructor for parsing source files generated from a UML diagram
	 * @param source
	 * 		The file or directory to be parsed
	 * @param umlName
	 * 		The name of the UML diagram that the source code was generated from
	 */
	public ParseSource(String source, String umlName) {
		super("Parsing the UML generated Source Code: "+ umlName);
		setUser(true);
		this.umlSource = source;
		this.umlName = umlName;
		this.isUml = true;
		UTMActivator.log("\nPreparing to parse UML Generated Source Code from: "+ umlName +" within: " + source + "\n");

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
		SubMonitor progress = SubMonitor.convert(monitor,"Parsing Source Code", 100);
		boolean success = false;
		IStatus status = null;
		this.db = new UTMDB();
		this.db.Open();
		try {
			if(this.umlSource != null) {
				progress.subTask("Refresh the workspace");
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, progress.split(25));
				this.sourceCode = new File(this.umlSource);
				UTMActivator.log("New File created from: " + this.umlSource);
				this.db.InitDatabase();
			} else {
				this.db.ReInitDatabase();
			}
			progress.setWorkRemaining(75);			
			if(this.sourceCode.exists()){
				progress.subTask("Process Subdirectories and Files");
				success = processDirectoryFiles(this.sourceCode, progress.split(75));
				this.db.Commit();
				if(success) {status = Status.OK_STATUS;}
				else {
					status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID,
							"Source Code Parsing failed for: "+ super.getName());
					UTMActivator.getDefault().getLog().log(status);
				}
			} else if(this.umlName != null){
				schedule(5000);
				status = new Status(IStatus.WARNING, UTMActivator.PLUGIN_ID,
						"Source Code Parsing rescheduled for file/directory: \n" 
								+ this.sourceCode.getPath()
								+ "\nDoes this file exist? " + this.sourceCode.exists() 
								+ "\nJob : " + super.getName());
				UTMActivator.getDefault().getLog().log(status);
				progress.worked(50);
			} else {
				status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID,
						"Source Code Parsing failed for: "+ super.getName());
				UTMActivator.getDefault().getLog().log(status);
				progress.worked(50);
			}
		} catch (NullPointerException e) {
			schedule(5000);
			status = new Status(IStatus.WARNING, UTMActivator.PLUGIN_ID,
					"Source Code Parsing rescheduled for file/directory: \n" 
							+ this.umlSource
							+ "\nDoes this file exist? " + this.sourceCode.exists() 
							+ "\nJob : " + super.getName());
			UTMActivator.getDefault().getLog().log(status);
		} catch (CoreException e) {
			status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID,
					"Source Code Parsing failed for: "+ super.getName() +
					"\nFailed when refreshing the resource hierarchy.");
			UTMActivator.getDefault().getLog().log(status);
			e.printStackTrace();
		}
		this.db.Close();
		return status;
	}	

	/**
	 * A launcher for the ParseSource Job
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
	 * Provides a dialog for the user to select a Java Source file or directory
	 * @return 
	 * 		the selected Java Source File or Directory or null
	 */
	public static File selectSource(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Choose a Java File or a Directory containing Java Files");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Java File","java"));
		fileChooser.setCurrentDirectory(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile());
		int result = fileChooser.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * Parses all Java files within a directory recursively
	 * or parses a single java file
	 * @param directoryOrFile
	 * 		The directory or file to be parsed
	 * @param monitor
	 * 		The progress monitor to report progress on
	 * @return
	 * 		Returns true on successfully parsing all files
	 * 		Returns false on failure
	 */
	private boolean processDirectoryFiles(File directoryOrFile, IProgressMonitor monitor){
		if(directoryOrFile == null||!directoryOrFile.exists()){
			UTMActivator.log(directoryOrFile.getName() +
					" : File or Directory not initialized or doesn't exit");
			return false;
		}
		SubMonitor progress = SubMonitor.convert(monitor, "Processing : " + directoryOrFile.getName(), 100);
		// create new filename filter
		FilenameFilter fileNameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(name.lastIndexOf('.')>0)
				{
					// get last index for '.' char
					int lastIndex = name.lastIndexOf('.');

					// get extension
					String str = name.substring(lastIndex);

					// match path name extension
					if(str.equals(".java"))
					{
						return true;
					}
				}
				return false;
			}
		};
		// If a single file was passed confirm it is a java file and process it
		if(!directoryOrFile.isDirectory()) {
			if(fileNameFilter.accept(directoryOrFile.getParentFile(), directoryOrFile.getName()))
				return readFile(directoryOrFile);
			else 
			{
				System.err.println("Error: A *.java file was not selected!");
				return false;
			}
		}
		
		
		// Recursively loop through directories
		File[] files = directoryOrFile.listFiles();
		int remaining = files.length;
		// Create a new progress monitor for the loop.
        SubMonitor loopMonitor = progress.split(70).setWorkRemaining(remaining);
		for(File file : files){
			loopMonitor.setWorkRemaining(remaining--);
			// Create a progress monitor for each loop iteration.
            SubMonitor iterationMonitor = loopMonitor.split(1);
			if(file.isDirectory()) {
				if(!processDirectoryFiles(file, iterationMonitor))
					return false;
			}	
		}
		
		// Loop through each java file and process it
		File[] javaFiles = directoryOrFile.listFiles(fileNameFilter);
		remaining = javaFiles.length;
		loopMonitor = progress.setWorkRemaining(remaining);
		for (File file : javaFiles){ 
			// Create a progress monitor for each loop iteration.
            SubMonitor iterationMonitor = loopMonitor.split(1);
            iterationMonitor.setTaskName("Processing : " + file.getName());
			System.out.println(file.getName());
			if(!readFile(file))
				return false;
			this.classLinNumber = 0;
			this.methodLinNumber = 0;
		}
		return true;
	}

	/**
	 * This method reads a file and processes it into the UTM database
	 * @param fileName
	 * 		The file to be parsed
	 * @return
	 * 		Returns true on successfully parsing the file
	 * 		Returns false on failure to parse the file
	 */
	private boolean readFile(File fileName){
		try{
			int ln = 0;
			Scanner scanner1 = new Scanner(fileName);
			while (scanner1.hasNextLine()){
				ln ++;
				scanner1.nextLine();
			}
			scanner1.close();
			String[] lines = new String[ln];
			Scanner scanner2 = new Scanner(fileName);
			for (int i =0 ; i < ln; i++)
			{
				lines[i] = scanner2.nextLine();
			}
			scanner2.close();
			String[] cleanCod = codeCleaner(lines);
			if(findClass(cleanCod, fileName.getName()))
			{
				cleanCod = removeClassInterfaceDeclaration(cleanCod, this.declaration);
				cleanCod = methodCleaner(cleanCod);
				if(findClassMethods(cleanCod, fileName.getName()))
				{
					cleanCod = removeClassMethodsDeclaration(cleanCod);
					if(findClassAttributes(cleanCod, fileName.getName()))
					{
						return true;
					}else {return false;}
				}else {return false;}
			}else {return false;} //false
		}
		catch(FileNotFoundException ex) {
			UTMActivator.log("Unable to open file '" + 
					fileName.getName() + "'");
			return false;
		}

	}

	/**
	 * This method finds the class declaration  
	 * @param line
	 *		An array of lines from the file 'name'
	 * @param name
	 * 		The name of the file
	 * @return
	 * 		returns true on successfully finding the class declaration
	 * 		returns false on a failure
	 */
	private boolean findClass(String[] line, String name){
		this.className = null;
		int lineNumber = 0;
		String currentLn="";
		String firstPart ="";
		String secondPart ="";
		String classDeclaration ="";
		String identifier = "";
		String modifier = "";
		String type = "";
		String reference = "";
		boolean extend = false;
		boolean implement = false;
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isAbstract = false;
		boolean flag = false;
		for(int i=0; i < line.length; i++){
			currentLn = line[i];
			lineNumber++;
			if (currentLn.trim().contains("class") 
					|| currentLn.trim().contains("interface")
					|| currentLn.trim().contains("enum"))
			{
				firstPart = currentLn.trim();
				flag = true;
				while (flag) 
				{
					if (currentLn.endsWith("{") )
					{
						flag = false;
						this.classLinNumber = lineNumber;
					}
					else if (line[i+1].endsWith("{") )
					{
						flag = false;
						secondPart= secondPart +  line[i + 1].trim();
						this.classLinNumber = lineNumber + 1;
					}
					classDeclaration = firstPart+ " " + secondPart;
					this.declaration = classDeclaration;
					System.out.println("Found a Class  " +"in line " + lineNumber + " " + classDeclaration);
				}
				if (classDeclaration.contains("public"))
				{
					modifier = "public"; 
					classDeclaration = classDeclaration.replaceFirst("public", "").trim();
				}
				if (classDeclaration.contains("private"))
				{
					modifier = "private"; 
					classDeclaration = classDeclaration.replaceFirst("private", "").trim();
				}
				if (classDeclaration.contains("abstract"))
				{
					isAbstract = true; 
					classDeclaration = classDeclaration.replaceFirst("abstract", "").trim();
				}
				if (classDeclaration.contains("final"))
				{
					isFinal = true; 
					classDeclaration = classDeclaration.replaceFirst("final", "").trim();
				}
				if (classDeclaration.contains("static"))
				{
					isStatic = true; classDeclaration = classDeclaration.replaceFirst("static", "").trim();
				}
				type = classDeclaration.substring(0, classDeclaration.indexOf(" ")).trim();
				classDeclaration = classDeclaration.replaceFirst(type, "").trim();
				identifier = classDeclaration.substring(0, classDeclaration.indexOf(" ")).trim();
				classDeclaration = classDeclaration.replaceFirst(identifier, "").trim();
				this.className = identifier;
				if (classDeclaration.contains("extends"))
				{
					extend = true; 
					classDeclaration = classDeclaration.replaceFirst("extends", "").trim();
				}
				if (classDeclaration.contains("implements"))
				{
					implement = true; 
					classDeclaration = classDeclaration.replaceFirst("implements", "").trim();
				}
				if (type.contains("class"))
				{
					// To split inherited interfaces 
					//modifier class identifier - extends class_name - - implements interface_name* -  
					if (extend)
					{
						reference = classDeclaration.substring(0,classDeclaration.indexOf(" "));
						classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
						System.out.println("Name reference extends class : " + reference);
						if(!isUml) {
							this.db.NewSourceReference(this.className, "extends" , reference);
						} 
						else {
							this.db.NewUMLReference(this.className, "extends" , reference);
						}
					}
					if (implement)
					{
						if (classDeclaration.contains(","))
						{
							while (classDeclaration.contains(","))
							{
								// first one 
								reference = classDeclaration.substring(0,classDeclaration.indexOf(","));
								classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
								classDeclaration = classDeclaration.replaceFirst(",", "").trim();
								System.out.println("Name reference implements interface : " + reference);
								if(!isUml) {
									this.db.NewSourceReference(this.className, "implements" , reference);
								} 
								else {
									this.db.NewUMLReference(this.className, "implements" , reference);
								}
							}
							//second* one
							reference = classDeclaration.substring(0,classDeclaration.indexOf(" "));
							classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
							System.out.println("Name reference implements interface : " + reference);
							if(!isUml) {
								this.db.NewSourceReference(this.className, "implements" , reference);
							} 
							else {
								this.db.NewUMLReference(this.className, "implements" , reference);
							}
						}else {
							reference = classDeclaration.substring(0,classDeclaration.indexOf(" "));
							classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
							System.out.println("Name reference implements interface : " + reference);
							if(!isUml) {
								this.db.NewSourceReference(this.className, "implements" , reference);
							} 
							else {
								this.db.NewUMLReference(this.className, "implements" , reference);
							}
						}
					}
				}else if (type.contains("interface"))
					//modifier interface identifier - extends interface_name* -
				{
					if (extend)
					{
						if (classDeclaration.contains(","))
						{
							while (classDeclaration.contains(","))
							{
								reference = classDeclaration.substring(0,classDeclaration.indexOf(","));
								classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
								classDeclaration = classDeclaration.replaceFirst(",", "").trim();
								System.out.println("Name reference extends interface : " + reference);
								if(!isUml) {
									this.db.NewSourceReference(this.className, "extends" , reference);
								} 
								else {
									this.db.NewUMLReference(this.className, "extends" , reference);
								}
							}
							//second* one
							reference = classDeclaration.substring(0,classDeclaration.indexOf(" "));
							classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
							System.out.println("Name reference implements interface : " + reference);
							if(!isUml) {
								this.db.NewSourceReference(this.className, "extends" , reference);
							} 
							else {
								this.db.NewUMLReference(this.className, "extends" , reference);
							}
						} else 
						{
							reference = classDeclaration.substring(0,classDeclaration.indexOf("{") -1);
							classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
							System.out.println("Name reference extends interface : " + reference);
							if(!isUml) {
								this.db.NewSourceReference(this.className, "extends" , reference);
							} 
							else {
								this.db.NewUMLReference(this.className, "extends" , reference);
							}
						}
					}
				}
				if(!isUml) {
					System.out.println(this.className + lineNumber + this.className + modifier + isStatic + isAbstract + isFinal);
					this.db.NewSourceClass(this.className, lineNumber, this.className, modifier, isStatic, isAbstract, isFinal);
				} 
				else {
					System.out.println(this.className + lineNumber + this.className + modifier + isStatic + isAbstract + isFinal);
					this.db.NewUMLClass(this.umlName, this.className, modifier, isStatic, isAbstract, isFinal);
				}
				System.out.println("modifier : " + modifier);
				System.out.println("IsStatic : " + isStatic);
				System.out.println("IsFinal : " + isFinal);
				System.out.println("IsAbstract : " + isAbstract);
				System.out.println("type : " + type);
				System.out.println("identifier : " + identifier);																								
				System.out.println("IsExtends : " + extend);
				System.out.println("IsImplements : " + implement + "\n");
				return true;
			}
		}
		UTMActivator.log("No Class Declaration found within '" + name + "'");
		return true;
	}

	/**
	 * This method finds all the class's attribute variables within the file
	 * @param line
	 *		An array of lines from the file 'name'
	 * @param name
	 * 		The name of the file
	 * @return
	 * 		returns true on successfully parsing all lines in the file
	 * 		returns false on a failure
	 */
	private boolean findClassAttributes(String[] line, String name){
		String currentLn="";
		int lineNumber = classLinNumber;
		String attributesModifier = "";
		String attributesName = "";
		String attributeType= "";
		String attributeRgEx="((public\\s|private\\s)?(static\\s|final\\s)?([a-zA-Z0-9]+)+\\s+([a-zA-Z0-9_]+)+\\s?(.+)?\\s?;)";
		// Loop through all the file lines
		for(int i=classLinNumber; i < methodLinNumber; i++) {
			currentLn = line[i].trim();
			lineNumber++;
			Pattern p = Pattern.compile(attributeRgEx);
			Matcher m =p.matcher(currentLn);
			if(m.find())
			{
				this.className = name.substring(0, name.indexOf("."));
				// Attributes modifiers
				if (currentLn.contains("public"))
				{
					attributesModifier += "public "; 
					currentLn = currentLn.replaceFirst("public", "").trim();
				}
				if (currentLn.contains("private"))
				{
					attributesModifier += "private "; 
					currentLn = currentLn.replaceFirst("private", "").trim();
				}
				if (currentLn.contains("protected"))
				{
					attributesModifier += "protected "; 
					currentLn = currentLn.replaceFirst("protected", "").trim();
				}
				if (currentLn.contains("native"))
				{
					attributesModifier += "native "; 
					currentLn = currentLn.replaceFirst("native", "").trim();
				}
				if (currentLn.contains("synchronized"))
				{
					attributesModifier += "synchronized "; 
					currentLn = currentLn.replaceFirst("synchronized", "").trim();
				}
				if (currentLn.contains("abstract"))
				{
					attributesModifier += "abstract "; 
					currentLn = currentLn.replaceFirst("abstract", "").trim();
				}
				if (currentLn.contains("threadsafe"))
				{
					attributesModifier += "threadsafe "; 
					currentLn = currentLn.replaceFirst("threadsafe", "").trim();
				}
				if (currentLn.contains("transient"))
				{
					attributesModifier += "transient "; 
					currentLn = currentLn.replaceFirst("transient", "").trim();
				}
				if (currentLn.contains("static"))
				{
					attributesModifier += "static "; 
					currentLn = currentLn.replaceFirst("static", "").trim();
				}
				if (currentLn.contains("final"))
				{
					attributesModifier += "final "; 
					currentLn = currentLn.replaceFirst("final", "").trim();
				}
				attributeType = currentLn.substring(0, currentLn.indexOf(" ") );
				currentLn = currentLn.replaceFirst(attributeType, "").trim();
				// To split variables if declared in one line.
				if (currentLn.contains(","))
				{
					while (currentLn.contains(","))
					{
						attributesName = currentLn.substring(0,currentLn.indexOf(","));
						currentLn = currentLn.replaceFirst(attributesName, "").trim();
						currentLn = currentLn.replaceFirst(",", "").trim();
						System.out.println("Name Second var : " + attributesName);
						this.db.NewSourceAttribute(this.className, lineNumber, this.className, attributesModifier, attributeType, attributesName);
					} 
					if (currentLn.contains(" "))
						attributesName = currentLn.substring(0,currentLn.indexOf(" "));
					else 
						attributesName = currentLn.substring(0,currentLn.indexOf(";"));
					this.db.NewSourceAttribute(this.className, lineNumber, this.className, attributesModifier, attributeType, attributesName);
				}else {
					if (currentLn.contains(" "))
						attributesName = currentLn.substring(0,currentLn.indexOf(" "));
					else 
						attributesName = currentLn.substring(0,currentLn.indexOf(";"));
					if(!isUml)
						this.db.NewSourceAttribute(this.className, lineNumber, this.className, attributesModifier, attributeType, attributesName);
					else
						this.db.NewUMLAttribute(this.umlName, this.className, attributesModifier, attributeType, attributesName);
				}
				System.out.println("Line "+ lineNumber + " : " + line[i].trim());
				System.out.println("modifier : " + attributesModifier);
				System.out.println("Type : " + attributeType);
				System.out.println("Name : " + attributesName +"\n");
				attributesModifier = "";	attributesName = "";	attributeType= "";
			}
		}
		return true;
	}

	/**
	 * This method finds all the class's methods within the file
	 * @param line
	 * 		An array of lines from the file 'name'
	 * @param name
	 * 		The name of the file
	 * @return
	 * 		returns true on successfully parsing all lines in the file
	 * 		returns false on a failure
	 */
	private boolean findClassMethods(String[] line, String name){

		String currentLn="";
		int lineNumber = 0;
		String methodModifier = "";
		String methodReturnType = "";
		String methodName = "";
		String methodParameter = "";
		// Method Declaration Regular Expression
		String methodRgEx="((public\\s|private\\s)?(static\\s)?([a-zA-Z0-9]+)+\\s+([a-zA-Z0-9_]+)+\\s?\\((.+)?\\)\\s?)";
		// Loop through all the file lines
		for(int i=0; i < line.length; i++){
			currentLn = line[i].trim();
			lineNumber++;
			Pattern p = Pattern.compile(methodRgEx);
			Matcher m =p.matcher(currentLn);
			if(m.find())
			{
				if (!currentLn.contains("="))
				{
					if (this.methodLinNumber == 0)
						this.methodLinNumber = lineNumber;

					this.className = name.substring(0, name.indexOf("."));
					// Methods modifiers
					if (currentLn.contains("public"))
					{
						methodModifier += "public "; 
						currentLn = currentLn.replaceFirst("public", "").trim();
						}
					if (currentLn.contains("private"))
					{
						methodModifier += "private "; 
						currentLn = currentLn.replaceFirst("private", "").trim();}
					if (currentLn.contains("protected"))
					{
						methodModifier += "protected "; 
						currentLn = currentLn.replaceFirst("protected", "").trim();
						}
					if (currentLn.contains("native"))
					{
						methodModifier += "native "; 
						currentLn = currentLn.replaceFirst("native", "").trim();
						}
					if (currentLn.contains("synchronized"))
					{
						methodModifier += "synchronized "; 
						currentLn = currentLn.replaceFirst("synchronized", "").trim();
						}
					if (currentLn.contains("abstract"))
					{
						methodModifier += "abstract "; 
						currentLn = currentLn.replaceFirst("abstract", "").trim();
						}
					if (currentLn.contains("threadsafe"))
					{
						methodModifier += "threadsafe "; 
						currentLn = currentLn.replaceFirst("threadsafe", "").trim();
						}
					if (currentLn.contains("transient"))
					{
						methodModifier += "transient "; 
						currentLn = currentLn.replaceFirst("transient", "").trim();
						}
					if (currentLn.contains("static"))
					{
						methodModifier += "static "; 
						currentLn = currentLn.replaceFirst("static", "").trim();
						}
					if (currentLn.contains("final"))
					{
						methodModifier += "final "; 
						currentLn = currentLn.replaceFirst("final", "").trim();
						}
					if (currentLn.contains(this.className) == false) {
						methodReturnType = currentLn.substring(0,currentLn.indexOf(" ")); 
						currentLn = currentLn.replaceFirst(currentLn.substring(0,currentLn.indexOf(" ")), "");
					}
					methodParameter = currentLn.substring(currentLn.indexOf("("), currentLn.indexOf(")") + 1 );
					methodName = currentLn.substring(0,currentLn.indexOf("("));
					if(!isUml)
						this.db.NewSourceMethod(this.className , lineNumber , this.className , methodModifier , methodReturnType , methodName , methodParameter);
					else
						this.db.NewUMLMethod(this.umlName, this.className, methodModifier , methodReturnType , methodName , methodParameter);
					System.out.println("Line "+ lineNumber + " : " + line[i].trim());
					System.out.println("modifier : " + methodModifier);
					System.out.println("Return Type : " + methodReturnType);
					System.out.println("Method Name : " + methodName);																								
					System.out.println("Para : " + methodParameter +"\n");
					methodModifier = "";	methodReturnType = "";	methodName = "";	methodParameter = "";
				}
			}
		}
		return true;
	}


	/**
	 * This method removes all the class's methods declaration within the file
	 * @param line
	 *		An array of lines from the file 'name'
	 * @return
	 * 		returns lines
	 */
	public String[] removeClassMethodsDeclaration(String[] line )
	{
		String methodRgEx="((public\\s|private\\s|final\\s)?(static\\s)?([a-zA-Z0-9]+)+\\s+([a-zA-Z0-9_]+)+\\s?\\((.+)?\\)\\s?)";
		String currentLn="";
		for (int i=this.classLinNumber ; i<line.length; i++){
			currentLn = line[i];
			Pattern p = Pattern.compile(methodRgEx);
			Matcher m =p.matcher(currentLn);
			if(m.find())
			{
				line[i] = "";
			}
		}
		return line;
	}

	/**
	 * This method removes all the class's comments and libraries within the file
	 * @param lines
	 *		An array of lines from the file 'name'
	 * @return
	 * 		returns lines
	 */
	private String[] codeCleaner (String[] lines)
	{
		String newLine = "";
		char currentChar;
		ArrayList<String> javaCode = new ArrayList<String>();
		for(int i = 0 ; i < lines.length; i++) {
			newLine = lines[i].trim();

			if (newLine.startsWith("/*") 
					|| newLine.startsWith("*")
					|| newLine.endsWith("*/")
					|| newLine.startsWith("//")
					|| newLine.startsWith("import")
					|| newLine.startsWith("package"))
			{
				for(int y = 0; y < newLine.length() ; y++)
				{
					currentChar = newLine.charAt(y);
					String toStr = new String(new char[] {currentChar});
					newLine = newLine.replace(toStr, " ");
				}
			}
			javaCode.add(newLine.trim());
		}
		String [] code = javaCode.toArray(new String[javaCode.size()]);
		return code;
	}

	/**
	 * This method removes the class or the interface declaration within the file
	 * @param lines
	 *		An array of lines from the file 'name'
	 * @param declaration
	 * 		The class declaration to be removed from lines
	 * @return
	 * 		returns codeWithoutDeclaration
	 */
	private String[] removeClassInterfaceDeclaration (String[] lines, String declaration)
	{
		String first = lines[this.classLinNumber - 2].trim();
		String second = lines[this.classLinNumber - 1].trim();
		String decla = first + " "  + second;
		String clDec = declaration.trim();
		ArrayList<String> javaCode = new ArrayList<String>();
		for (int i = 0; i < lines.length; i++)
		{
			if (lines[this.classLinNumber].trim().contains(clDec))
			{
				lines[this.classLinNumber - 2] = " ";

			}else if (decla.contains(clDec)){
				lines[this.classLinNumber - 2 ] = " ";
				lines[this.classLinNumber - 1] = " ";
			}
			javaCode.add(lines[i].trim());
		}
		String [] codeWithoutDeclaration = javaCode.toArray(new String[javaCode.size()]);
		return codeWithoutDeclaration;
	}

	/**
	 * This method removes the methods content within the file
	 * @param lines
	 *		An array of lines from the file 'name'
	 * @return
	 * 		returns body
	 */
	private String[] methodCleaner (String[] lines)
	{
		String newLine = "";
		char currentChar;
		ArrayList<String> javaCodeMeth = new ArrayList<String>();
		boolean flag = false; 
		int brenth = 0;
		for(int i = 0 ; i < lines.length; i++) {
			newLine = lines[i];
			for(int y = 0; y < lines[i].length() ; y++)
			{
				currentChar = newLine.charAt(y);
				if(currentChar == '{')
				{brenth ++; flag = true;}
				if(currentChar == '}')
				{
					brenth --;
					String toStr = new String(new char[] {currentChar});
					newLine = newLine.replace(toStr, " ");
				}
				if (brenth == 0)
					flag = false;
				if(flag)
				{ 
					String toStr = new String(new char[] {currentChar});
					newLine = newLine.replace(toStr, " ");
				}
			}
			javaCodeMeth.add(newLine.trim());
		}
		String [] body = javaCodeMeth.toArray(new String[javaCodeMeth.size()]);
		return body;
	}
}
