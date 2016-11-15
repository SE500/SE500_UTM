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

import org.eclipse.utm.compare.UTMDB;
/**
 * 
 * @author Thomas Colborne
 * 
 * A class that parses source files and enters relevant information into
 * the UTMDB - an SQL database
 */
public class ParseSource {
	
	/*
	 * Variable declarations
	 */
	private File sourceCode;
	private String className = null;
	private UTMDB db = null;
	private boolean isUml = false;
	private String umlName = null;
	private int classLinNumber;
	private int methodLinNumber;
	
	/**
	 * Empty Constructor
	 * If used initialize must be called before launch
	 */
	public ParseSource() {
		
	}
	
	/**
	 * Constructor for parsing the file or directory
	 * @param source
	 * 		The file or directory to be parsed
	 */
	public ParseSource(File source) {
		initialize(source);
	}
	
	/**
	 * Constructor for parsing source files generated from a UML diagram
	 * @param source
	 * 		The file or directory to be parsed
	 * @param umlName
	 * 		The name of the UML diagram that the source code was generated from
	 */
	public ParseSource(File source, String umlName) {
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
	private void initialize(File source, String umlName) {
		this.sourceCode = source;
		this.umlName = umlName;
		this.isUml = true;
		this.db = new UTMDB();
		this.db.Open();
		this.db.InitDatabase();
	}
	
	public boolean launch() {
		boolean success = processDirectoryFiles(this.sourceCode);
		this.db.Commit();
		this.db.Close();
		return success;
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
		if(directoryOrFile == null||!directoryOrFile.exists())
			return false;
		
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
			else return false;
		}
		catch(FileNotFoundException ex) {
		    System.out.println(
		        "Unable to open file '" + 
		    		fileName.getName() + "'");
		    return false;
		}
		  
	}
	
	//System.err.println(file.getName() 
	//+ " could not be opened for reading");
	
	private boolean processFile(File file) throws IOException {
		
		//Construct BufferedReader from FileReader
		BufferedReader br = new BufferedReader(new FileReader(file));
	 
		String line = null;
		int lineNumber = 0;
		while ((line = br.readLine()) != null) {
			lineNumber++;
			if(!processFileLine(line, lineNumber, file.getName())){
				System.err.printf("File %1$s: Line %2$d: Line Processing Failed",
						file.getName(), lineNumber);
				br.close();
				return false;
			}
				
		}
		br.close();	
		return true;
	}
	 
	private boolean processFileLine(String line, int lineNumber, String name) {
		if(!findClass(line, lineNumber, name))
			if(!findClassAttributes(line, lineNumber, name))
				findClassMethods(line, lineNumber, name);
		return true;
	}

	private boolean findClass(String line, int lineNumber, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean findClassAttributes(String line, int lineNumber, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean findClassMethods(String line, int lineNumber, String name) {
		// TODO Auto-generated method stub
		return false;
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
		String currentLn="";
		int lineNumber = 0;
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isAbstract = false;
		boolean isReference = false;
		
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
			if(m.find()) {
				System.out.println("Found a Class in line " + lineNumber + " " + m.group() + "" + currentLn);
				for(int y = 1; y <= m.groupCount(); y++) {
					if(m.group(y) != null) {
						System.out.println("Group: " + y + " = " + m.group(y));
						this.classLinNumber = lineNumber;
						switch(y) {
						case 2:
							isStatic = true;
							break;
						case 3:
							isFinal = true;
							break;
						case 4:
							isAbstract = true;
							break;
						case 7:
							this.className = m.group(y);
						case 8:
							isReference = true;
							break;
						default:						
						}					
					}
				}
				if(!isUml) {
					this.db.NewSourceClass(name, lineNumber, this.className, m.group(1), isStatic, isAbstract, isFinal);
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
		int lineNumber = 0;
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isAbstract = false;
		boolean isOther = false;
		
		// Attribute Declaration Regular Expression
		String attributeRgEx=
				// Group 1: Access Modifier
				"(public|private|protected)?\\s?"
				// Group 2: isStatic
				+ "(static)?\\s?"
				// Group 3: isFinal
				+ "(final)?\\s?"
				// Group 4: isAbstract
				+ "(abstract)?\\s?"
				// Group 5: isOther
				+ "(native|synchronized|transient)?\\s?"
				// Group 6: Variable Type
				+ "([\\$_\\w\\<\\>\\[\\]]*)\\s+"
				// Group 7: Variable Name
				+ "([\\$_\\w]+)"
				
				+ "([\\s\\$\\w,]*)"
				// Group 8: Variable Definition or Assignment
				+ "(\\s=|;)";
		
		// Loop through all the file lines
		for(int i=classLinNumber; i < methodLinNumber; i++) {
			currentLn = line[i];
			lineNumber++;
			Pattern p = Pattern.compile(attributeRgEx);
			Matcher m =p.matcher(currentLn);
			if(m.find())
			{
				System.out.println("Found an Attribute in line " + lineNumber + " " + m.group() + "" + currentLn);
				for(int y = 1; y <= m.groupCount(); y++) {
					if(m.group(y) != null) {
						System.out.println("Group: " + y + " = " + m.group(y));
						switch(y) {
						case 2:
							isStatic = true;
							break;
						case 3:
							isFinal = true;
							break;
						case 4:
							isAbstract = true;
							break;
						case 5:
							isOther = true;
							break;
						default:						
						}					
					}
				}
				if(!isUml)
					this.db.NewSourceAttribute(name, lineNumber, this.className, m.group(1), m.group(6), m.group(7));
				else
					this.db.NewUMLAttribute(this.umlName, this.className, m.group(1), m.group(6), m.group(7));
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
		boolean isAbstract = false;
		boolean isOther = false;
		
		// Method Declaration Regular Expression
		String methodRgEx=
				// Group 1: Access Modifier
				"(public|private|protected)?\\s?"
				// Group 2: isStatic
				+ "(static)?\\s?"
				// Group 3: isFinal
				+ "(final)?\\s?"
				// Group 4: isAbstract
				+ "(abstract)?\\s?"
				// Group 5: isOther
				+ "(native|synchronized|transient)?\\s?"
				// Group 6: Method Return Type
				+ "([\\$\\w\\<\\>\\[\\]]*)\\s?"
				// Group 7: Method Name
				+ "([\\$\\w]+)\\s?"
				// Group 8: Method Arguments
				+ "\\(([^\\)]*)\\)";
				// "\\s*\\{?[^\\}]*\\}?" - don't need the extra info
		
		// Loop through all the file lines
		for(int i=0; i < line.length; i++){
			currentLn = line[i];
			lineNumber++;
			Pattern p = Pattern.compile(methodRgEx);
			Matcher m =p.matcher(currentLn);
			if(m.find())
			{
				System.out.println("Found a Method in line " + lineNumber + " " + m.group() + "" + currentLn);
				for(int y = 1; y <= m.groupCount(); y++) {
					if(m.group(y) != null) {
						if (methodLinNumber == 0)
							this.methodLinNumber = lineNumber;
						System.out.println("Group: " + y + " = " + m.group(y));
						switch(y) {
						case 2:
							isStatic = true;
							break;
						case 3:
							isFinal = true;
							break;
						case 4:
							isAbstract = true;
							break;
						case 5:
							isOther = true;
							break;
						default:						
						}					
					}
				}
				if(!isUml)
					this.db.NewSourceMethod(name, lineNumber, this.className, m.group(1), m.group(6), m.group(7), m.group(8));
				else
					this.db.NewUMLMethod(this.umlName, this.className, m.group(1), m.group(6), m.group(7), m.group(8));
			}
			
		}
		return true;
	}
}