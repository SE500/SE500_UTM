package org.eclipse.utm.parseSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.utm.compare.UTMDB;
/**
 * 
 * @authors Thomas Colborne, Ziyad Daghriri
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
	private int classLinNumber = 0;
	private int methodLinNumber = 0;
	private String declaration ="";
	private ArrayList<String> javaCodeVar;
	private ArrayList<String> javaCodeMeth;
	private ArrayList<String> javaCode;
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
					if(findClassAttributes(lines, fileName.getName()))
					{
						return true;
					}else
					{
						return false;
					}
				}else {return false;}
			}else {return false;}
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

		int lineNumber = 0;
		String currentLn="";
		String firstPart ="";
		String secondPart ="";
		String classDeclaration ="";
		String classModifier = "";
		String identifier = "";
		String modifier = "";
		String type = "";
		String reference = "";
		boolean extend = false;
		boolean implement = false;
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isAbstract = false;
		boolean isReference = false;
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
				{modifier = "public"; classDeclaration = classDeclaration.replaceFirst("public", "").trim();}

				if (classDeclaration.contains("private"))
				{modifier = "private"; classDeclaration = classDeclaration.replaceFirst("private", "").trim();}

				if (classDeclaration.contains("protected"))
				{modifier = "protected"; classDeclaration = classDeclaration.replaceFirst("protected", "").trim();}

				if (classDeclaration.contains("native"))
				{modifier = "native"; classDeclaration = classDeclaration.replaceFirst("native", "").trim();}

				if (classDeclaration.contains("synchronized"))
				{modifier = "synchronized"; classDeclaration = classDeclaration.replaceFirst("synchronized", "").trim();}

				if (classDeclaration.contains("abstract"))
				{modifier = "abstract"; classDeclaration = classDeclaration.replaceFirst("abstract", "").trim();}

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
						{
							while (classDeclaration.contains(","))
							{
								// first one 
								reference = classDeclaration.substring(0,classDeclaration.indexOf(","));
								classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
								classDeclaration = classDeclaration.replaceFirst(",", "").trim();
								System.out.println("Name reference implements interface  : " + reference);
								//db
							}
							//second* one
							reference = classDeclaration.substring(0,classDeclaration.indexOf(" "));
							classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
							System.out.println("Name reference implements interface  : " + reference);
							//db
						}else {
							reference = classDeclaration.substring(0,classDeclaration.indexOf(" "));
							classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
							System.out.println("Name reference implements interface  : " + reference);
							//db
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
								//db
							}
							//second* one
							reference = classDeclaration.substring(0,classDeclaration.indexOf(" "));
							classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
							System.out.println("Name reference implements interface  : " + reference);
						//db
						}else {
							reference = classDeclaration.substring(0,classDeclaration.indexOf(" "));
							classDeclaration = classDeclaration.replaceFirst(reference, "").trim();
							System.out.println("Name reference extends interface : " + reference);
						//db
						}
					}

				}
				
				System.out.println("modifier : " + modifier);
				System.out.println("type : " + type);
				System.out.println("identifier : " +identifier);																								
				System.out.println("IsExtends : " + extend);
				System.out.println("IsImplements : " + implement);



				if(!isUml) {
					System.out.println( this.className + lineNumber + this.className + classModifier + isStatic + isAbstract + isFinal);
					this.db.NewSourceClass(this.className, lineNumber, this.className, classModifier, isStatic, isAbstract, isFinal);
					if(isReference)
					{
						//public int NewSourceReference(String ClassName, String AccessType, String RefClass)
					//	this.db.NewSourceReference(this.className, m.group(9), m.group(11));
					}
				} 
				else {
					//this.db.NewUMLClass(this.umlName, this.className, m.group(1), isStatic, isAbstract, isFinal);
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

					if (currentLn.contains("native"))
					{attributesModifier = "native"; currentLn = currentLn.replaceFirst("native", "").trim();}

					if (currentLn.contains("synchronized"))
					{attributesModifier = "synchronized"; currentLn = currentLn.replaceFirst("synchronized", "").trim();}

					if (currentLn.contains("abstract"))
					{attributesModifier = "abstract"; currentLn = currentLn.replaceFirst("abstract", "").trim();}

					if (currentLn.contains("threadsafe"))
					{attributesModifier = "threadsafe"; currentLn = currentLn.replaceFirst("threadsafe", "").trim();}

					if (currentLn.contains("transient"))
					{attributesModifier = "transient"; currentLn = currentLn.replaceFirst("transient", "").trim();}

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

						if (currentLn.contains("native"))
						{methodModifier = "native"; currentLn = currentLn.replaceFirst("native", "").trim();}

						if (currentLn.contains("synchronized"))
						{methodModifier = "synchronized"; currentLn = currentLn.replaceFirst("synchronized", "").trim();}

						if (currentLn.contains("abstract"))
						{methodModifier = "abstract"; currentLn = currentLn.replaceFirst("abstract", "").trim();}

						if (currentLn.contains("threadsafe"))
						{methodModifier = "threadsafe"; currentLn = currentLn.replaceFirst("threadsafe", "").trim();}

						if (currentLn.contains("transient"))
						{methodModifier = "transient"; currentLn = currentLn.replaceFirst("transient", "").trim();}

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

	/**
	 * To remove all comments and libraries 
	 * @param String[] lines
	 * @return String[] code
	 */
	private String[] codeCleaner (String[] lines)
	{
		String newLine = "";
		char currentChar;
		this.javaCode = new ArrayList<String>();
		for(int i = 0 ; i < lines.length; i++) {
			newLine = lines[i].trim();

			if (newLine.startsWith("/*") 
					|| newLine.startsWith("*")
					|| newLine.endsWith("*/")
					|| newLine.startsWith("//")
					|| newLine.startsWith("import"))
			{
				for(int y = 0; y < newLine.length() ; y++)
				{
					currentChar = newLine.charAt(y);
					String toStr = new String(new char[] {currentChar});
					newLine = newLine.replace(toStr, " ");
				}
			}
			this.javaCode.add(newLine.trim());
		}
		String [] code = this.javaCode.toArray(new String[this.javaCode.size()]);
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
		this.javaCode = new ArrayList<String>();
		for (int i = 0; i < lines.length; i++)
		{
			if (lines[this.classLinNumber].trim().contains(clDec))
			{
				lines[this.classLinNumber - 2] = " ";

			}else if (decla.contains(clDec)){
				lines[this.classLinNumber - 2 ] = " ";
				lines[this.classLinNumber - 1] = " ";
			}
			this.javaCode.add(lines[i].trim());
		}

		String [] codeWithoutDeclaration = this.javaCode.toArray(new String[this.javaCode.size()]);
		for(String s : codeWithoutDeclaration)
			System.out.println(s);
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
		this.javaCodeMeth = new ArrayList<String>();
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
			this.javaCodeMeth.add(newLine.trim());
		}
		String [] body = this.javaCodeMeth.toArray(new String[this.javaCodeMeth.size()]);
		for(String s : body)
			System.out.println(s);
		return body;
	}
}
