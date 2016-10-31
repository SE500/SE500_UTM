package org.eclipse.utm.views;

import java.util.Scanner;
import java.io.File;
import javax.swing.JFileChooser;

public class OpenFile {
	
	//Declare Variable
	 JFileChooser fileChooser = new JFileChooser(new File(" "));
	 public StringBuilder sb = new StringBuilder();
	
	public void PickMe() throws Exception {
		fileChooser.setDialogTitle("Open a File");
		fileChooser.setFileFilter(new FileTypeFilter(".java","Java File"));
		fileChooser.setFileFilter(new FileTypeFilter(".Uml","Uml File"));
//		int result = fileChooser.showOpenDialog(null);
		
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
			System.out.println("Folder Name"+ fileChooser.getCurrentDirectory());
			System.out.println("Folder Name"+ fileChooser.getSelectedFile().getPath());
			sb.append(fileChooser.getSelectedFile().getName());
			
//			get the file
//			 java.io.File file = fileChooser.getSelectedFile();
//			File openfile = fileChooser.getSelectedFile();
//			
//			create a scanner for the file
//			Scanner input = new Scanner(openfile);
//			
//			read text from file
//			while(input.hasNext()){
//				sb.append(input.nextLine());
//				sb.append("\n"); 
//			}
//			input.close();	
			
		}else{
			sb.append("no file was selected.");
		}
	}

}
