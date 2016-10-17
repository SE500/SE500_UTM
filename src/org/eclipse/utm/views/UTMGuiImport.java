package org.eclipse.utm.views;

import java.awt.EventQueue;

import org.eclipse.utm.OpenFile;
import org.eclipse.utm.sqliteConnection;

import java.awt.Toolkit;
import java.sql.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UTMGuiImport {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UTMGuiImport window = new UTMGuiImport();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
//	Connection connection = null;
	private JButton btnImportUmlFiles;
	private JButton btnStart;
	private JButton btnImportJavaSource;
	private JTextArea textAreaUML;
	private JTextArea textAreaJavaFiles;
	/**
	 * Create the application.
	 */
	public UTMGuiImport() {
		initialize();
	//	connection = sqliteConnection.dbConnector();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(UTMGuiImport.class.getResource("/org/eclipse/utm/resources/Magic_icon_16.png")));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		btnImportUmlFiles = new JButton("Import UML Files");
		btnImportUmlFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OpenFile of = new OpenFile();
				try {
					of.PickMe();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				textAreaUML.setText(of.sb.toString());
			}
		});
		btnImportUmlFiles.setBounds(21, 196, 164, 29);
		frame.getContentPane().add(btnImportUmlFiles);
		
		btnImportJavaSource = new JButton("Import Java Files");
		btnImportJavaSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OpenFile of = new OpenFile();
				try {
					of.PickMe();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				textAreaJavaFiles.setText(of.sb.toString());
			}
		});
		btnImportJavaSource.setBounds(244, 196, 172, 29);
		frame.getContentPane().add(btnImportJavaSource);
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
//					String query = "select * from UTMDatabase";
					frame.dispose();
					if(btnImportUmlFiles.getSelectedIcon() != null){
						GuiOutputUmlToJava output = new GuiOutputUmlToJava();
						output.setVisible(true);
					}else if(btnImportJavaSource.getSelectedIcon() != null){
						GuiOutputJavaToUml output = new GuiOutputJavaToUml();
						output.setVisible(true);
					}else{
						//System.out.printf("Please Import UML class file or Java Source file!");
						GuiOutputUmlToJava output = new GuiOutputUmlToJava();
						output.setVisible(true);
					}
					
				}catch(Exception ec){
					JOptionPane.showMessageDialog(null, ec);
				}
			}
		});
		
		btnStart.setIcon(new ImageIcon(UTMGuiImport.class.getResource("/org/eclipse/utm/resources/Magic_icon_16.png")));
		btnStart.setBounds(162, 237, 117, 29);
		frame.getContentPane().add(btnStart);
		
		JLabel lblWelcomeToTrace = new JLabel("Welcome to Trace Magic");
		lblWelcomeToTrace.setBounds(136, 6, 164, 16);
		frame.getContentPane().add(lblWelcomeToTrace);
		
		textAreaUML = new JTextArea();
		textAreaUML.setBounds(32, 39, 145, 145);
		frame.getContentPane().add(textAreaUML);
		
		textAreaJavaFiles = new JTextArea();
		textAreaJavaFiles.setBounds(255, 39, 153, 145);
		frame.getContentPane().add(textAreaJavaFiles);
	}


}
