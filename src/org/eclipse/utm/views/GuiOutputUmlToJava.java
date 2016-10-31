package org.eclipse.utm.views;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.eclipse.utm.sqliteConnection;

import net.proteanit.sql.DbUtils;

import javax.swing.GroupLayout.Alignment;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GuiOutputUmlToJava extends JFrame {

	private JPanel contentPane;
	private JTable table;

	private JFrame frame;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiOutputUmlToJava frame = new GuiOutputUmlToJava();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	// Connection connection = null;
	private JMenuItem mntmDetails;
	/**
	 * Create the frame.
	 */
	public GuiOutputUmlToJava() {
//		connection = sqliteConnection.dbConnector();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblNewLabel = new JLabel("Traceability Table");
		lblNewLabel.setBounds(157, 5, 139, 16);
		contentPane.setLayout(null);
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(contentPane, popupMenu);
		
		mntmDetails = new JMenuItem("More Information");
		mntmDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MoreInfo moreinfo = new MoreInfo();
				moreinfo.setModal(true);
				moreinfo.setVisible(true);
				
				
			}
		});
		popupMenu.add(mntmDetails);
		contentPane.add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(41, 39, 374, 181);
		contentPane.add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		JButton btnUmlToJava = new JButton("UML to Java Source Code");
		btnUmlToJava.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	//			try {									
					//try get database
	//				String query = "select * from UTMDatabase";
	//				PreparedStatement pst = connection.prepareStatement(query);
	//				ResultSet rs = pst.executeQuery();
	//				table.setModel(DbUtils.resultSetToTableModel(rs));
					
	//			} catch (Exception e1) {
	//				e1.printStackTrace();
	//			}
				
				
			}
		});
		btnUmlToJava.setBounds(138, 232, 180, 29);
		contentPane.add(btnUmlToJava);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				UTMGuiImport input = new UTMGuiImport();
		//		new UTMGuiImport().setVisible(true);
				
			}
		});
		btnBack.setBounds(359, 232, 64, 29);
		contentPane.add(btnBack);
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
