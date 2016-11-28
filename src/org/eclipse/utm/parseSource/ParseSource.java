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
	
	/*
	 * Variable declarations
	 */
	private File sourceCode;
	private IFile umlSource = null;
	private String className = null;
	private UTMDB db = null;
	private boolean isUml = false;
	private String umlName = null;
	private int classLinNumber = 0;
	private int methodLinNumber = 0;
	
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
	public ParseSource(IFile source, String umlName) {
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
	}
	
	/**
	 * 
	 * @param source
	 * @param umlName
	 */
	private void initialize(IFile source, String umlName) {
		this.umlSource = source;
		this.umlName = umlName;
		this.isUml = true;
		this.db = new UTMDB();
		this.db.Open();
		this.db.InitDatabase();
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Source Code Parsing begins", 1);
		boolean success = false;
		IStatus status = null;
		try {
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			if(this.umlSource != null) {
				this.sourceCode = new File(this.umlSource.getLocation().toOSString());
				UTMActivator.log("New File created from IFile");
			}
			if(this.sourceCode.exists()){
				success = processDirectoryFiles(this.sourceCode);
				this.db.Commit();
				this.db.Close();
				if(success)
					status = Status.OK_STATUS;
				else
					status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID,
							"Source Code Parsing failed for: "+ super.getName());
			} else if(this.umlName != null){
				schedule(5000);
				status = new Status(IStatus.WARNING, UTMActivator.PLUGIN_ID,
						"Source Code Parsing rescheduled for file/directory: \n" 
						+ this.sourceCode.getPath()
						+ "\nDoes this file exist? " + this.sourceCode.exists() 
						+ "\nJob : " + super.getName());
			} else {
				status = new Status(IStatus.ERROR, UTMActivator.PLUGIN_ID,
						"Source Code Parsing failed for: "+ super.getName());
			}
		} catch (NullPointerException e) {
			schedule(5000);
			status = new Status(IStatus.WARNING, UTMActivator.PLUGIN_ID,
					"Source Code Parsing rescheduled for file/directory: \n" 
					+ this.umlSource.getFullPath().toOSString()
					+ "\nDoes this file exist? " + this.umlSource.exists() 
					+ "\nJob : " + super.getName());
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
					else return false;
				else return false;
			else return true; //false
		}
		catch(FileNotFoundException ex) {
			UTMActivator.log("Unable to open file '" + 
		    		fileName.getName() + "'");
		    return false;
		}
		  
	}
	
	//System.err.println(file.getName() 
	//+ " could not be opened for reading");
	
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
		int lineNumber = 0;
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
						}
						return true;
				}
				
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
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isOther = false;
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
				if(m.find())
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
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isOther = false;
		String methodModifier = "";
		String methodReturnType = "";
		String methodName = "";
		String methodParameter = "";
		String methodType= "";
		int bracketCount = 0;
		
		// Method Declaration Regular Expression
		String methodRgEx="((public\\s|private\\s)?(static\\s)?([a-zA-Z0-9]+)+\\s+([a-zA-Z0-9_]+)+\\s?\\((.+)?\\)\\s?\\{)";
		// Loop through all the file lines
		for(int i=0; i < line.length; i++){
			currentLn = line[i].trim();
			lineNumber++;
			Pattern p = Pattern.compile(methodRgEx);
			Matcher m =p.matcher(currentLn);
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
					}
				}
		}
		return true;
	}
}
