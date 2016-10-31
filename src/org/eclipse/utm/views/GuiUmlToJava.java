package org.eclipse.utm.views;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.ImageIcon;

public class GuiUmlToJava extends JFrame {

	private JPanel contentPane;
	private JButton btnNewButton;
	private JTable table;
	private JTextField txtNumOfClass;
	private JTextField txtNumOfRows;
	private JTextField txtDataLen;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiUmlToJava frame = new GuiUmlToJava();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GuiUmlToJava() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		JPanel panel0 = new JPanel();
		contentPane.add(panel0, "t0");
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(-14, 6, 664, 454);
		panel_1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		btnNewButton = new JButton("Back");
		btnNewButton.setIcon(new ImageIcon(GuiUmlToJava.class.getResource("/resources/Go-back-icon_16.png")));
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false); // Hide current frame
				GuiUTMStart.main(null);
//				GuiUTMStart output = new GuiUTMStart();
//				output.setVisible(true);
				}
		});
		
		JButton btnNewButton_1 = new JButton("Save as..");
		btnNewButton_1.setIcon(new ImageIcon(GuiUmlToJava.class.getResource("/resources/Save_icon_16.png")));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fs = new JFileChooser();
				fs.setDialogTitle("Save a File");
				fs.setFileFilter(new FileTypeFilter(".txt","Text File"));
//				fs.setFileFilter(new FileTypeFilter(".pdf","Pdf File"));
				
				int result = fs.showSaveDialog(null);
				if(result == JFileChooser.APPROVE_OPTION){
					String content = table.getToolTipText();
					File fi = fs.getSelectedFile();
					try{
						FileWriter fw = new FileWriter(fi.getPath());
						fw.write(content);
						fw.flush();
						fw.close();
						
					}catch(Exception e2){
						//JOptionPane.showMessageDialog(null, e2.getMessage());
					}
				}
				Document doc = new Document();
				try {
					PdfWriter.getInstance(doc, new FileOutputStream("Report.pdf"));
				} catch (FileNotFoundException | DocumentException e1) {
				
					Logger.getLogger(GuiJavaToUml.class.getName()).log(Level.SEVERE,null,e1);
				}
				try{
				    doc.open();
					doc.add(new Paragraph(table.getToolkit().toString()));
					doc.close();
					JOptionPane.showMessageDialog(null, " saved");
				} catch (DocumentException e1) {
				
					Logger.getLogger(GuiJavaToUml.class.getName()).log(Level.SEVERE,null,e1);
				}}
				
			
		});
		panel0.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Traceability Matrix");
		panel0.add(panel_1);
		
		JPanel panel = new JPanel();
		
		txtNumOfClass = new JTextField();
		txtNumOfClass.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtNumOfClass.setText("Num of Class: ");
		txtNumOfClass.setColumns(10);
		
		txtNumOfRows = new JTextField();
		txtNumOfRows.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtNumOfRows.setText("Num of Methods: ");
		txtNumOfRows.setColumns(10);
		
		txtDataLen = new JTextField();
		txtDataLen.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtDataLen.setText("Data Len.: ");
		txtDataLen.setColumns(10);
		
		JLabel lblTableOfResults = new JLabel("Table of Results");
		lblTableOfResults.setIcon(new ImageIcon(GuiUmlToJava.class.getResource("/resources/Table_icon_16.png")));
		
		JButton btnMatchingDetails = new JButton("Matching Details");
		btnMatchingDetails.setIcon(new ImageIcon(GuiUmlToJava.class.getResource("/resources/details_icon_16.png")));
		btnMatchingDetails.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(49)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblTableOfResults, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(462))
						.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtNumOfClass, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(txtNumOfRows, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtDataLen, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
									.addGap(157))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(btnMatchingDetails)
									.addGap(18)
									.addComponent(btnNewButton_1))))
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 571, GroupLayout.PREFERRED_SIZE))
					.addGap(28))
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(282)
					.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGap(261))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE)
					.addGap(27)
					.addComponent(lblTableOfResults, GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 310, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtNumOfClass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtNumOfRows, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtDataLen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewButton)
						.addComponent(btnNewButton_1)
						.addComponent(btnMatchingDetails))
					.addGap(6))
		);
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 558, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(19, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
					.addGap(14)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		panel.setLayout(gl_panel);
		panel_1.setLayout(gl_panel_1);
		
		JPanel panel1 = new JPanel();
		contentPane.add(panel1, "t1");
		panel1.setLayout(null);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(-18, 26, 691, 381);
		panel1.add(panel_4);
		
		JPanel panel2 = new JPanel();
		contentPane.add(panel2, "t2");
	}
}
