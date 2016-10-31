package org.eclipse.utm.views;

import java.awt.BorderLayout;
import java.awt.EventQueue;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.ImageIcon;

public class GuiJavaToUml extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5800421425665534667L;
	private JPanel contentPane;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_3;
	private JLabel lblTraceabilityMatrix;
	private JPanel panel_4;
	private JScrollPane scrollPane;
	private JTable table;
	private JTextField txtNumberOfClass;
	private JTextField txtNumberOfMethods;
	private JTextField txtDataLen;
	private JLabel lblResultsTable;
	private JButton btnMatchingDetails;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiJavaToUml frame = new GuiJavaToUml();
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
	public GuiJavaToUml() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		panel = new JPanel();
		contentPane.add(panel, "name_26209468114932");
		panel.setLayout(null);
		
		panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(-12, 6, 687, 456);
		panel.add(panel_3);
		
		btnNewButton = new JButton("Back");
		btnNewButton.setIcon(new ImageIcon(GuiJavaToUml.class.getResource("/resources/Go-back-icon_16.png")));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false); // Hide current frame
				GuiUTMStart.main(null);
//				GuiUTMStart output = new GuiUTMStart();
//				output.setVisible(true);
				}
				
		});
		
		btnNewButton_1 = new JButton("Save as..");
		btnNewButton_1.setIcon(new ImageIcon(GuiJavaToUml.class.getResource("/resources/Save_icon_16.png")));
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
						// JOptionPane.showMessageDialog(null, e2.getMessage());
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
		
		lblTraceabilityMatrix = new JLabel("Traceability Matrix");
		
		panel_4 = new JPanel();
		
		txtNumberOfClass = new JTextField();
		txtNumberOfClass.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtNumberOfClass.setText("Number of Class: ");
		txtNumberOfClass.setColumns(10);
		
		txtNumberOfMethods = new JTextField();
		txtNumberOfMethods.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtNumberOfMethods.setText("Number of Methods: ");
		txtNumberOfMethods.setColumns(10);
		
		txtDataLen = new JTextField();
		txtDataLen.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtDataLen.setText("Data Len.: ");
		txtDataLen.setColumns(10);
		
		lblResultsTable = new JLabel("Table of Results");
		lblResultsTable.setIcon(new ImageIcon(GuiJavaToUml.class.getResource("/resources/Table_icon_16.png")));
		
		btnMatchingDetails = new JButton("Matching Details");
		btnMatchingDetails.setIcon(new ImageIcon(GuiJavaToUml.class.getResource("/resources/details_icon_16.png")));
		btnMatchingDetails.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGap(282)
							.addComponent(lblTraceabilityMatrix))
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGap(44)
							.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_3.createSequentialGroup()
									.addComponent(txtNumberOfClass, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtNumberOfMethods, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtDataLen, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE))
								.addComponent(lblResultsTable)
								.addGroup(gl_panel_3.createParallelGroup(Alignment.TRAILING, false)
									.addGroup(gl_panel_3.createSequentialGroup()
										.addComponent(btnNewButton)
										.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnMatchingDetails)
										.addGap(18)
										.addComponent(btnNewButton_1))
									.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))))
					.addContainerGap(69, Short.MAX_VALUE))
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblTraceabilityMatrix)
					.addGap(31)
					.addComponent(lblResultsTable)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(txtNumberOfClass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(4)
							.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnNewButton_1)
								.addComponent(btnNewButton)
								.addComponent(btnMatchingDetails)))
						.addComponent(txtNumberOfMethods, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtDataLen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		
		scrollPane = new JScrollPane();
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 558, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap(14, Short.MAX_VALUE)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 292, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		panel_4.setLayout(gl_panel_4);
		panel_3.setLayout(gl_panel_3);
		
		panel_1 = new JPanel();
		contentPane.add(panel_1, "t1");
		
		panel_2 = new JPanel();
		contentPane.add(panel_2, "t2");
	}
}
