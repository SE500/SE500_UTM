package org.eclipse.utm.parseSource;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.utm.compare.UTMDB;

import java.util.ArrayList;
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
	private File SourceCode;
	private String className = null;
	private UTMDB db = null;
	
	/**
	 * Empty Constructor
	 * If used initialize must be called before launch
	 */
	public ParseSource() {
		
	}
	
	/**
	 * Constructor
	 * @param Source
	 */
	public ParseSource(File Source) {
		initialize(Source);
	}
	
	
	/**
	 * Initializes the parse source process
	 * @param source
	 */
	public void initialize(File source) {
		this.SourceCode = source;
		this.db = new UTMDB();
		this.db.Open();
		this.db.InitDatabase();
	}
	
	public boolean launch() {
		boolean success = projectFiles(this.SourceCode);
		this.db.Close();
		return success;
	}
	
	/**
	 * Select a Java Source file or directory
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
	 * @param projectPath
	 * 		The path the directory to be parsed
	 * @return
	 * 		Returns true on successfully parsing all files
	 * 		Returns false on failure
	 */
	private boolean projectFiles(File projectPath){
		if(projectPath == null||!projectPath.exists())
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
		if(!projectPath.isDirectory()) {
			if(fileNameFilter.accept(projectPath.getParentFile(), projectPath.getName()))
				return readFile(projectPath);
			else 
			{
				System.err.println("Error: A *.java file was not selected!");
				return false;
			}
		}
		
		// Recursively loop through directories
		File[] files = projectPath.listFiles();
		for(File file : files){
			if(file.isDirectory()) {
		    	if(!projectFiles(file))
		    		return false;
		    } 
		}
        
		// Loop through each java file and process it
		File[] javaFiles = projectPath.listFiles(fileNameFilter);
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
				if(findClassVar(lines, fileName.getName()))
					if(findClassMeth(lines, fileName.getName()))
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
		int count = 0;
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isAbstract = false;
		//String classRgEx="(^(public interface{0,1})|^(public class{0,1})|extends|impelements)";
		String classRgEx="((public\\s|private\\s)?(static\\s)?(final\\s)?(abstract\\s)?(class)\\s([a-zA-Z0-9_\\.]+\\.)?([a-zA-Z0-9_]+)+(\\s(extends)+\\s([a-zA-Z_]+[a-zA-Z0-9\\._]+)+)?)";
		for (int i=0; i<line.length; i++){
			currentLn = line[i];
			count++;
			Pattern p = Pattern.compile(classRgEx);
			Matcher m =p.matcher(currentLn);
			while (m.find()) {
				System.out.println("Found a Class in line " + count + " " + m.group() + "" + currentLn);
				String[] tokens = m.group().split("\\s");
				for(String token: tokens) {
					System.out.println(token);
				}
				switch (tokens[1]) {
				case "class":
					this.db.NewSourceClass(name, count, tokens[2], tokens[0], isStatic, isAbstract, isFinal);
					this.className = tokens[2];
					break;
				case "static":
					isStatic = true;
					if(tokens[2].equals("class")) {
						this.db.NewSourceClass(name, count, tokens[3], tokens[0], isStatic, isAbstract, isFinal);
						this.className = tokens[3];
					}
					else
						System.out.println("What is: " + m.group());
					break;
				case "final":
					isFinal = true;
					if(tokens[2].equals("class")) {
						this.db.NewSourceClass(name, count, tokens[3], tokens[0], isStatic, isAbstract, isFinal);
						this.className = tokens[3];
					}
					else
						System.out.println("What is: " + m.group());
					break;
				case "abstract":
					isAbstract = true;
					if(tokens[2].equals("class")) {
						this.db.NewSourceClass(name, count, tokens[3], tokens[0], isStatic, isAbstract, isFinal);
						this.className = tokens[3];
					}
					else
						System.out.println("What is: " + m.group());
					break;
				default:
					System.err.println("No idea what this is: " + m.group());
					return false;
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
	private boolean findClassVar(String[] line, String name){
	
		int count = 0;
		//String classRgEx="((public\\s|private\\s)?(static\\s)?([a-zA-Z0-9]+)+\\s+([a-zA-Z0-9_]+)+\\s?(.+)?\\s?;)";
		String attributeRgEx="((public|private|protected|static|final|native|synchronized|abstract|transient)+\\s)+[\\$_\\w\\<\\>\\[\\]]*\\s+[\\$_\\w]+;";
		String currentLn="";
		new ArrayList<String>();
		for (int i=0; i<line.length; i++) {
			currentLn = line[i];
			count++;
			Pattern p = Pattern.compile(attributeRgEx);
			Matcher m =p.matcher(currentLn);
			if(m.find())
			{
				System.out.println("Found an Attribute in line " + count + " " + m.group() + "" + currentLn);
				String[] tokens = m.group().split("(;|\\s+)");
				for(String token: tokens) {
					System.out.println(token);
				}
				if(tokens.length == 3)
					this.db.NewSourceAttribute(name, count, this.className, tokens[0], tokens[1], tokens[2]);
				else
					return false;
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
	private boolean findClassMeth(String[] line, String name){
	
		int count = 0;
		//String classRgEx="((public\\s|private\\s)?(static\\s)?([a-zA-Z0-9]+)+\\s+([a-zA-Z0-9_]+)+\\s?(\\(.+\\))?\\s?\\{)";
		String methodRgEx="((public|private|protected|static|final|native|synchronized|abstract|transient)+\\s)+[\\$_\\w\\<\\>\\[\\]]*\\s+[\\$_\\w]+\\([^\\)]*\\)?\\s*\\{?[^\\}]*\\}?";
		String currentLn="";
		new ArrayList<String>();
		for (int i=0; i<line.length; i++){
			currentLn = line[i];
			count++;
			Pattern p = Pattern.compile(methodRgEx);
			Matcher m =p.matcher(currentLn);
			if(m.find())
			{
				System.out.println("Found a Method in line " + count + " " + m.group() + "" + currentLn);
				String[] tokens = m.group().split("(\\(|\\)|\\{|\\s)");
				
				for(String token: tokens) {
					System.out.println(token);
				}
				if(tokens.length == 4)
					this.db.NewSourceMethod(name, count, this.className, tokens[0], tokens[1], tokens[2], tokens[3]);
				else if(tokens.length == 3)
					this.db.NewSourceMethod(name, count, this.className, tokens[0], tokens[1], tokens[2], "");
				else
					return false;
			}
			
		}
		return true;
	}
}