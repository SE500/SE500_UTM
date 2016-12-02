package org.eclipse.utm.parseSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.utm.UTMActivator;
import org.eclipse.utm.compare.UTMDB;
/**
 * A class that parses source files and enters relevant information into
 * the UTMDB - an SQL database
 * @authors Thomas Colborne, Ziyad Daghriri
 * 
 */
public class ParseSource extends Job {
<<<<<<< HEAD
	
=======

>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
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
<<<<<<< HEAD
=======
	private String declaration ="";
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
	
	/**
	 * Empty Constructor
	 * If used initialize must be called before launch
	 */
	public ParseSource() {
		super("Parsing the Source Code");
		setUser(true);
	}
	
	/**
	 * Constructor for parsing the file or directory
	 * @param source
	 * 		The file or directory to be parsed
	 */
	public ParseSource(File source) {
		super("Parsing the Source Code: "+ source.getName());
		setUser(true);
		initialize(source);
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
		setSystem(true);
		initialize(source, umlName);
	}
	
	
	/**
	 * Initializes the parse source process
	 * @param source
	 * 		The file or directory to be parsed
	 */
	public void initialize(File source) {
		this.sourceCode = source;
		this.db = new UTMDB();
		this.db.Open();
		this.db.InitDatabase();
		UTMActivator.log("\nPreparing to parse Source Code within: " + source + "\n");
	}
	
	/**
	 * 
	 * @param source
	 * @param umlName
	 */
	private void initialize(String source, String umlName) {
		this.umlSource = source;
		this.umlName = umlName;
		this.isUml = true;
		this.db = new UTMDB();
		this.db.Open();
		this.db.InitDatabase();
		UTMActivator.log("\nPreparing to parse UML Generated Source Code from: "+ umlName +" within: " + source + "\n");
	}
<<<<<<< HEAD
	
=======

>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Source Code Parsing begins", 1);
		boolean success = false;
		IStatus status = null;
		try {
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			if(this.umlSource != null) {
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, monitor);
				this.sourceCode = new File(this.umlSource);
				UTMActivator.log("New File created from File");
			}
			if(this.sourceCode.exists()){
				success = processDirectoryFiles(this.sourceCode);
				this.db.Commit();
				this.db.Close();
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
			} else {
				status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID,
						"Source Code Parsing failed for: "+ super.getName());
				UTMActivator.getDefault().getLog().log(status);
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
					"\nFailed when refreshing the resourse hierarchy.");
			UTMActivator.getDefault().getLog().log(status);
			e.printStackTrace();
		} finally {
			monitor.done();
		}
		return status;
		
	}	
	
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
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
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
	 * @return
	 * 		Returns true on successfully parsing all files
	 * 		Returns false on failure
	 */
	private boolean processDirectoryFiles(File directoryOrFile){
		if(directoryOrFile == null||!directoryOrFile.exists()){
			UTMActivator.log(directoryOrFile.getName() +
					" : File or Directory not initialized or doesn't exit");
			return false;
		}
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
		for(File file : files){
			if(file.isDirectory()) {
		    	if(!processDirectoryFiles(file))
		    		return false;
		    } 
		}
        
		// Loop through each java file and process it
		File[] javaFiles = directoryOrFile.listFiles(fileNameFilter);
		for (File file : javaFiles){ 
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
			if(findClass(lines, fileName.getName()))
				if(findClassMethods(lines, fileName.getName()))
					if(findClassAttributes(lines, fileName.getName()))
						return true;
<<<<<<< HEAD
					else return false;
				else return false;
			else return true; //false
=======
					}else {return false;}
				}else {return false;}
			}else {return false;} //false
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
		}
		catch(FileNotFoundException ex) {
			UTMActivator.log("Unable to open file '" + 
		    		fileName.getName() + "'");
		    return false;
		}
		  
	}
	
	//System.err.println(file.getName() 
	//+ " could not be opened for reading");
<<<<<<< HEAD
	
=======

>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
//	private boolean processFile(File file) throws IOException {
//		
//		//Construct BufferedReader from FileReader
//		BufferedReader br = new BufferedReader(new FileReader(file));
//	 
//		String line = null;
//		int lineNumber = 0;
//		while ((line = br.readLine()) != null) {
//			lineNumber++;
//			if(!processFileLine(line, lineNumber, file.getName())){
//				System.err.printf("File %1$s: Line %2$d: Line Processing Failed",
//						file.getName(), lineNumber);
//				br.close();
//				return false;
//			}
//				
//		}
//		br.close();	
//		return true;
//	}
//	 
//	private boolean processFileLine(String line, int lineNumber, String name) {
//		if(!findClass(line, lineNumber, name))
//			if(!findClassAttributes(line, lineNumber, name))
//				findClassMethods(line, lineNumber, name);
//		return true;
//	}
//
//	private boolean findClass(String line, int lineNumber, String name) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	private boolean findClassAttributes(String line, int lineNumber, String name) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	private boolean findClassMethods(String line, int lineNumber, String name) {
//		// TODO Auto-generated method stub
//		return false;
//	}

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
		String currentLn="";
<<<<<<< HEAD
		int lineNumber = 0;
=======
		String firstPart ="";
		String secondPart ="";
		String classDeclaration ="";
		String identifier = "";
		String modifier = "";
		String type = "";
		String reference = "";
		boolean extend = false;
		boolean implement = false;
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isAbstract = false;
		boolean isReference = false;
		String firstPart ="";
		String secondPart ="";
		String classDeclaration ="";
		String classModifier = "";
		
		// Class Declaration Regular Expression
		String classRgEx= 
				// Group 1: AccessModifier
				"(public|private)?\\s?" 
				// Group 2: isStatic
				+ "(static)?\\s?"
				// Group 3: isFinal
				+ "(final)?\\s?"
				// Group 4: isAbstract
				+ "(abstract)?\\s?"
				// Group 5: class
				+ "(class)\\s"
				// Group 6: Class Absolute definition
				+ "([\\w\\.]+\\.)?"
				// Group 7: className
				+ "([\\w]+)"
				// Group 8: Extension or Implementation Declaration
				+ "(\\s"
					// Group 9: extends or implements
					+"(extends|implements)\\s"
					// Group 10: absolute definition
					+ "([\\w\\.]+\\.)?"
					// Group 11: class name
					+ "([\\w]+)"
				+ ")?";
		
		// Loop through all the file lines
		for(int i=0; i < line.length; i++){
			currentLn = line[i];
			lineNumber++;
			Pattern p = Pattern.compile(classRgEx);
			Matcher m = p.matcher(currentLn);
			// To get rid of comments 
			if (currentLn.trim().startsWith("/*") == false 
					&& currentLn.trim().startsWith("//") == false 
					&&  currentLn.trim().startsWith("*") == false)
				{
					if(m.find()) {
						// To combined class declaration 	
						 if (currentLn.endsWith("{") )
							{
								firstPart = currentLn.trim();
							}else
							{						
								secondPart= currentLn.trim() + line[i+1].trim();
							}
						 classDeclaration = firstPart+ " " + secondPart;		
						System.out.println("Found a Class in line " + lineNumber + " " + classDeclaration);
						this.classLinNumber = lineNumber;
<<<<<<< HEAD
						if (classDeclaration.contains("abstract"))
							isAbstract = true;
						if (classDeclaration.contains("final"))
							isFinal = true;
						if (classDeclaration.contains("reference"))
							isReference = true;
						if (classDeclaration.contains("public"))
							classModifier = "public";
						if (classDeclaration.contains("private"))
							classModifier = "private";
						this.className = name.substring(0, name.indexOf("."));
						if(!isUml) {
							System.out.println( this.className + lineNumber + this.className + classModifier + isStatic + isAbstract + isFinal);
						this.db.NewSourceClass(this.className, lineNumber, this.className, classModifier, isStatic, isAbstract, isFinal);
						if(isReference)
=======
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
				{modifier = "public"; classDeclaration = classDeclaration.replaceFirst("public", "").trim();}

				if (classDeclaration.contains("private"))
				{modifier = "private"; classDeclaration = classDeclaration.replaceFirst("private", "").trim();}

//				if (classDeclaration.contains("protected"))
//				{modifier = "protected"; classDeclaration = classDeclaration.replaceFirst("protected", "").trim();}
//
//				if (classDeclaration.contains("native"))
//				{modifier = "native"; classDeclaration = classDeclaration.replaceFirst("native", "").trim();}
//
//				if (classDeclaration.contains("synchronized"))
//				{modifier = "synchronized"; classDeclaration = classDeclaration.replaceFirst("synchronized", "").trim();}

				if (classDeclaration.contains("abstract"))
				{isAbstract = true; classDeclaration = classDeclaration.replaceFirst("abstract", "").trim();}
				
				if (classDeclaration.contains("final"))
				{isFinal = true; classDeclaration = classDeclaration.replaceFirst("final", "").trim();}
				
				if (classDeclaration.contains("static"))
				{isStatic = true; classDeclaration = classDeclaration.replaceFirst("static", "").trim();}

				type = classDeclaration.substring(0, classDeclaration.indexOf(" ")).trim();
				classDeclaration = classDeclaration.replaceFirst(type, "").trim();
				identifier = classDeclaration.substring(0, classDeclaration.indexOf(" ")).trim();
				classDeclaration = classDeclaration.replaceFirst(identifier, "").trim();

				if (classDeclaration.contains("extends"))
				{extend = true; classDeclaration = classDeclaration.replaceFirst("extends", "").trim();}

				if (classDeclaration.contains("implements"))
				{implement = true; classDeclaration = classDeclaration.replaceFirst("implements", "").trim();}

				if (type.contains("class"))
				{
					// To split inherited interfaces 
					//modifier class identifier - extends class_name - - implements interface_name* -  
					if (extend)
					{
						reference = classDeclaration.substring(0,classDeclaration.indexOf(" "));
						classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
						System.out.println("Name reference extends class  : " + reference);
					}
					if (implement)
					{
						if (classDeclaration.contains(","))
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
						{
							//this.db.NewSourceReference(this.className, m.group(9), m.group(11));
						}
						} 
						else {
							this.db.NewUMLClass(this.umlName, this.className, m.group(1), isStatic, isAbstract, isFinal);
							if(isReference)
							{
								//this.db.NewUMLReference(this.className, m.group(9), m.group(11));
							}
<<<<<<< HEAD
=======
							//second* one
							reference = classDeclaration.substring(0,classDeclaration.indexOf(" "));
							classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
							System.out.println("Name reference implements interface  : " + reference);
							//db
						} else 
						{
							reference = classDeclaration.substring(0,classDeclaration.indexOf(" "));
							classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
							System.out.println("Name reference extends interface : " + reference);
							//db
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
						}
						return true;
				}
<<<<<<< HEAD
				
=======

				this.className = identifier;

				if(!isUml) {
					System.out.println(this.className + lineNumber + this.className + modifier + isStatic + isAbstract + isFinal);
					this.db.NewSourceClass(this.className, lineNumber, this.className, modifier, isStatic, isAbstract, isFinal);
					if(isReference)
					{
						//public int NewSourceReference(String ClassName, String AccessType, String RefClass)
						//	this.db.NewSourceReference(this.className, m.group(9), m.group(11));
					}
				} 
				else {
					System.out.println(this.className + lineNumber + this.className + modifier + isStatic + isAbstract + isFinal);
					this.db.NewUMLClass(this.umlName, this.className, modifier, isStatic, isAbstract, isFinal);
					if(isReference)
					{
						//this.db.NewUMLReference(this.className, m.group(9), m.group(11));
					}
				}
				System.out.println("modifier : " + modifier);
				System.out.println("IsStatic : " + isStatic);
				System.out.println("IsFinal : " + isFinal);
				System.out.println("IsAbstract : " + isAbstract);
				System.out.println("type : " + type);
				System.out.println("identifier : " + identifier);																								
				System.out.println("IsExtends : " + extend);
				System.out.println("IsImplements : " + implement);
				return true;
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
			}
		}
		UTMActivator.log("No Class Declaration found within '" + name + "'");
		return false;
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
			if (currentLn.trim().startsWith("/*") == false 
					&& currentLn.trim().startsWith("//") == false 
					&&  currentLn.trim().startsWith("*") == false)
			{
<<<<<<< HEAD
				if(m.find())
=======
				this.className = name.substring(0, name.indexOf("."));
				// Attributes modifiers
				if (currentLn.contains("public"))
				{attributesModifier += "public "; currentLn = currentLn.replaceFirst("public", "").trim();}

				if (currentLn.contains("private"))
				{attributesModifier += "private "; currentLn = currentLn.replaceFirst("private", "").trim();}

				if (currentLn.contains("protected"))
				{attributesModifier += "protected "; currentLn = currentLn.replaceFirst("protected", "").trim();}

				if (currentLn.contains("native"))
				{attributesModifier += "native "; currentLn = currentLn.replaceFirst("native", "").trim();}

				if (currentLn.contains("synchronized"))
				{attributesModifier += "synchronized "; currentLn = currentLn.replaceFirst("synchronized", "").trim();}

				if (currentLn.contains("abstract"))
				{attributesModifier += "abstract "; currentLn = currentLn.replaceFirst("abstract", "").trim();}

				if (currentLn.contains("threadsafe"))
				{attributesModifier += "threadsafe "; currentLn = currentLn.replaceFirst("threadsafe", "").trim();}

				if (currentLn.contains("transient"))
				{attributesModifier += "transient "; currentLn = currentLn.replaceFirst("transient", "").trim();}

				if (currentLn.contains("static"))
				{attributesModifier += "static "; currentLn = currentLn.replaceFirst("static", "").trim();}

				if (currentLn.contains("final"))
				{attributesModifier += "final "; currentLn = currentLn.replaceFirst("final", "").trim();}

				attributeType = currentLn.substring(0, currentLn.indexOf(" ") );currentLn = currentLn.replaceFirst(attributeType, "").trim();
				// To split variables if declared in one line.
				if (currentLn.contains(","))
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
				{
					this.className = name.substring(0, name.indexOf("."));
					// Attributes modifiers
					if (currentLn.contains("public"))
						{attributesModifier = "public"; currentLn = currentLn.replaceFirst("public", "").trim();}
					
					if (currentLn.contains("private"))
						{attributesModifier = "private"; currentLn = currentLn.replaceFirst("private", "").trim();}
					
					if (currentLn.contains("protected"))
						{attributesModifier = "protected"; currentLn = currentLn.replaceFirst("protected", "").trim();}
					
					if (currentLn.contains("static"))
						{isStatic = true; currentLn = currentLn.replaceFirst("static", "").trim();}
					
					if (currentLn.contains("final"))
						{isFinal = true; currentLn = currentLn.replaceFirst("final", "").trim();}
					
					attributeType = currentLn.substring(0, currentLn.indexOf(" ") );currentLn = currentLn.replaceFirst(attributeType, "").trim();
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
					System.out.println("Current lin : " + line[i].trim());
					System.out.println("Name 1: " + attributesName);
					System.out.println("modifier : " + attributesModifier);
					System.out.println("Type : " + attributeType);
					System.out.println(lineNumber);
					attributesModifier = "";	attributesName = "";	attributeType= ""; isStatic = false;	isFinal = false;	isOther = false;
				}
<<<<<<< HEAD
=======
				System.out.println("Line "+ lineNumber + " : " + line[i].trim());
				System.out.println("modifier : " + attributesModifier);
				System.out.println("Type : " + attributeType);
				System.out.println("Name : " + attributesName +"\n");
				attributesModifier = "";	attributesName = "";	attributeType= "";
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
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
<<<<<<< HEAD
		String methodType= "";
		int bracketCount = 0;
		
=======
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
		// Method Declaration Regular Expression
		String methodRgEx="((public\\s|private\\s)?(static\\s)?([a-zA-Z0-9]+)+\\s+([a-zA-Z0-9_]+)+\\s?\\((.+)?\\)\\s?\\{)";
		// Loop through all the file lines
		for(int i=0; i < line.length; i++){
			currentLn = line[i].trim();
			lineNumber++;
			Pattern p = Pattern.compile(methodRgEx);
			Matcher m =p.matcher(currentLn);
<<<<<<< HEAD
			if (currentLn.startsWith("/*") == false 
					&& currentLn.startsWith("//") == false 
					&&  currentLn.startsWith("*") == false)
				{	
					if(m.find())
					{
						if ((line[i-1].endsWith("{") == false 
								|| (line[i-1].endsWith("{") && (i == this.classLinNumber || i == this.classLinNumber + 1))
								) 
								&& line[i-1].endsWith("(") == false
								&& currentLn.contains("else") == false
								&& currentLn.contains("if") == false
								&& currentLn.contains("try") == false
								&& currentLn.contains("catch") == false
								&& currentLn.contains("}") == false
								&& currentLn.contains("SwingUtilities") == false)
						{
					
							if (this.methodLinNumber == 0)
							this.methodLinNumber = lineNumber;
							
							this.className = name.substring(0, name.indexOf("."));
							// Methods modifiers
							if (currentLn.contains("public"))
								{methodModifier = "public"; currentLn = currentLn.replaceFirst("public", "").trim();}
							
							if (currentLn.contains("private"))
								{methodModifier = "private"; currentLn = currentLn.replaceFirst("private", "").trim();}
							
							if (currentLn.contains("protected"))
								{methodModifier = "protected"; currentLn = currentLn.replaceFirst("protected", "").trim();}
							
							if (currentLn.contains("static"))
								{isStatic = true; currentLn = currentLn.replaceFirst("static", "").trim();}
							
							if (currentLn.contains("final"))
								{isFinal = true; currentLn = currentLn.replaceFirst("final", "").trim();}
							
							if (currentLn.contains(this.className) == false) {
								methodReturnType = currentLn.substring(0,currentLn.indexOf(" ")); currentLn = currentLn.replaceFirst(currentLn.substring(0,currentLn.indexOf(" ")), "");
							}
							methodParameter = currentLn.substring(currentLn.indexOf("("), currentLn.indexOf(")") + 1 );
							
							methodName = currentLn.substring(0,currentLn.indexOf("("));
							System.out.println("Current lin : " + line[i].trim());
							System.out.println("Name : " +methodName);																								
							System.out.println("Para : " + methodParameter);
							System.out.println("modifier : " + methodModifier);
							System.out.println("Return Type : " + methodReturnType);
							System.out.println("method Name : " + methodName);
							System.out.println(lineNumber);
							if(!isUml)
								this.db.NewSourceMethod(this.className , lineNumber , this.className , methodModifier , methodType , methodName , methodParameter);
							else
								this.db.NewUMLMethod(this.umlName, this.className, methodModifier , methodType , methodName , methodParameter);
							
							// To clean the variables
							methodModifier = "";	methodReturnType = "";	methodName = "";	methodParameter = "";	methodType= "";
							isStatic = false;	isFinal = false;	isOther = false;
							
						}
=======
			if(m.find())
			{
				if (!currentLn.contains("="))
				{
					if (this.methodLinNumber == 0)
						this.methodLinNumber = lineNumber;

					this.className = name.substring(0, name.indexOf("."));
					// Methods modifiers
					if (currentLn.contains("public"))
					{methodModifier += "public "; currentLn = currentLn.replaceFirst("public", "").trim();}

					if (currentLn.contains("private"))
					{methodModifier += "private "; currentLn = currentLn.replaceFirst("private", "").trim();}

					if (currentLn.contains("protected"))
					{methodModifier += "protected "; currentLn = currentLn.replaceFirst("protected", "").trim();}

					if (currentLn.contains("native"))
					{methodModifier += "native "; currentLn = currentLn.replaceFirst("native", "").trim();}

					if (currentLn.contains("synchronized"))
					{methodModifier += "synchronized "; currentLn = currentLn.replaceFirst("synchronized", "").trim();}

					if (currentLn.contains("abstract"))
					{methodModifier += "abstract "; currentLn = currentLn.replaceFirst("abstract", "").trim();}

					if (currentLn.contains("threadsafe"))
					{methodModifier += "threadsafe "; currentLn = currentLn.replaceFirst("threadsafe", "").trim();}

					if (currentLn.contains("transient"))
					{methodModifier += "transient "; currentLn = currentLn.replaceFirst("transient", "").trim();}

					if (currentLn.contains("static"))
					{methodModifier += "static "; currentLn = currentLn.replaceFirst("static", "").trim();}

					if (currentLn.contains("final"))
					{methodModifier += "final "; currentLn = currentLn.replaceFirst("final", "").trim();}

					if (currentLn.contains(this.className) == false) {
						methodReturnType = currentLn.substring(0,currentLn.indexOf(" ")); 
						currentLn = currentLn.replaceFirst(currentLn.substring(0,currentLn.indexOf(" ")), "");
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
					}
<<<<<<< HEAD
=======
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
					
					// To clean the variables
					methodModifier = "";	methodReturnType = "";	methodName = "";	methodParameter = "";
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
				}
		}
		return true;
	}
<<<<<<< HEAD
=======


	/**
	 * To fined class's methods  
	 * @param String[] lines
	 * @return String[]
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
	 * To remove all comments and libraries 
	 * @param String[] lines
	 * @return String[] code
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
	 * To remove class or the interface declaration  
	 * @param String[] lines
	 * @return String[] codeWithoutDeclaration
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
//		for(String s : codeWithoutDeclaration)
//			System.out.println(s);
		return codeWithoutDeclaration;
	}

	/**
	 * To remove methods' body  
	 * @param String[] lines
	 * @return String[] body
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
//		for(String s : body)
//			System.out.println(s);
		return body;
	}
>>>>>>> branch 'master' of https://github.com/SE500/SE500_UTM.git
}
