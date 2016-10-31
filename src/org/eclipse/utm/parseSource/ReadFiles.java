package org.eclipse.utm.parseSource;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class ReadFiles {

	private String fileName;
	private String filePath;
	private ArrayList<String> javaCodeVar;
	private ArrayList<String> javaCodeMeth;
	
	/**
     * To set file name 
     * @param String name
     */
	public void setfileName (String name){
		this.fileName = name;
	}
	/**
     * To get file name 
     */
	public String getFileName(){
		
		return this.fileName;
	}
	/**
     * To set file path 
     * @param String name
     */
	public void setfilePath (String path){
		this.filePath = path;
	}
	/**
     * To get file path 
     */
	public String getFilePath(){
		
		return this.filePath;
	}
	/**
     * To set files 
     */
	public void setFiles(){
		// to insert file path
		this.setfilePath("D:\\ASUS Share\\ERAU\\JavaWorkplace\\Oracl\\src\\");
		this.setfileName("FileChooserDemo.java");
	}
	
	/**
     * Get all Java files 
     * @param String projecPath
     */
	 public void projectFiles(String projectPath){
	        File folder = new File(projectPath);
	        File[] files = folder.listFiles();
	        for (File file : files){
	            if (file.isFile()){
	                System.out.println(file.getName());
	                
	            }
	        }
	    }
	 
	 /**
	     * To read file 
	     * @param String filePath
	     * @return String[]
	     */
	 public String[] readfile (String filePath){
			 try{
				int ln = 0;
				Scanner scanner1 = new Scanner(new File(filePath));
				while (scanner1.hasNextLine()){
					ln ++;
					scanner1.nextLine();
				}
				scanner1.close();
				String[] lines = new String[ln];
	            Scanner scanner2 = new Scanner(new File(filePath));
	             for (int i =0 ; i < ln; i++)
	             {
	            	 lines[i] = scanner2.nextLine();
	             }
	             scanner2.close();
	             finedClass(lines);
	             finedClassVar(lines);
	             finedClassMeth(lines);
				return lines;
			}
			catch(FileNotFoundException ex) {
	            System.out.println(
	                "Unable to open file '" + 
	                		filePath + "'");                
	        }
	       return null;
	 }
	 
	 /**
	     * To fined class deceleration  
	     * @param String[] lines
	     * @return String
	     */
	public String finedClass(String[] line ){
		
		String currentLn="";
		int count = 0;
		//String classRgEx="(^(public interface{0,1})|^(public class{0,1})|extends|impelements)";
		String classRgEx="((public\\s|private\\s)?(static\\s)?(final\\s)?(class)\\s([a-zA-Z0-9_\\.]+\\.)?([a-zA-Z0-9_]+)+(\\s(extends)+\\s([a-zA-Z_]+[a-zA-Z0-9\\._]+)+)?)";
		for (int i=0; i<line.length; i++){
			currentLn = line[i];
			count++;
			Pattern p = Pattern.compile(classRgEx);
			Matcher m =p.matcher(currentLn);
			while (m.find()) {
				System.out.println("Found a Class  " +"in line " + count + " " + m.group() + "" + currentLn);
				return i + "" + currentLn;
			}
		}
		return "No class found";
	}
	
	/**
     * To fined class's instance variables  
     * @param String[] lines
     * @return String[]
     */
	
	public String[] finedClassVar(String[] line ){
	
		int count = 0;
		String classRgEx="((public\\s|private\\s)?(static\\s)?([a-zA-Z0-9]+)+\\s+([a-zA-Z0-9_]+)+\\s?(.+)?\\s?;)";
		String currentLn="";
		javaCodeVar = new ArrayList<String>();
		boolean check = false;
		for (int i=0; i<line.length; i++){
			currentLn = line[i];
			count++;
			Pattern p = Pattern.compile(classRgEx);
			Matcher m =p.matcher(currentLn);
			//while (m.find()) {
			if(m.find())
			{
				System.out.println("Found a " +"in line " + count + " " + m.group() + "" + currentLn);
				javaCodeVar.add(currentLn);
				check = true;
				//return i + "" + currentLn;
			}
			
		}
		if (check){
			String [] var = javaCodeVar.toArray(new String[javaCodeVar.size()]);
			for(String s : var)
			//System.out.println(s);
		return var;
		}
		return null;
	}
	
	/**
     * To fined class's methods  
     * @param String[] lines
     * @return String[]
     */
	
	public String[] finedClassMeth(String[] line ){
	
		int count = 0;
		String classRgEx="((public\\s|private\\s)?(static\\s)?([a-zA-Z0-9]+)+\\s+([a-zA-Z0-9_]+)+\\s?\\((.+)?\\)\\s?{)";
		String currentLn="";
		javaCodeMeth = new ArrayList<String>();
		boolean check = false;
		for (int i=0; i<line.length; i++){
			currentLn = line[i];
			count++;
			Pattern p = Pattern.compile(classRgEx);
			Matcher m =p.matcher(currentLn);
			if(m.find())
			{
				//System.out.println("Found a " +"in line " + count + " " + m.group() + "" + currentLn);
				javaCodeMeth.add(currentLn);
				check = true;
			}
			
		}
		if (check){
			String [] var = javaCodeMeth.toArray(new String[javaCodeMeth.size()]);
			for(String s : var)
			//System.out.println(s);
		return var;
		}
		return null;
		}
}