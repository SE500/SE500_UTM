package org.eclipse.utm.views;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.net.URI;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;
import java.awt.Font;

public class GuiUTMStart extends JFrame
{

	private JFrame frame;
	private JButton btnSelectUmlFile;
	private JButton btnSelectJava;
	private JButton btnNewButton_1;
	private JButton btnNewButton;
	private JTextArea textAreaUML;
	private JTextArea textAreaJavaSourceCode;

	
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
					GuiUTMStart window = new GuiUTMStart();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiUTMStart() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 400, 270);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
					MenuAbout moreinfo = new MenuAbout();
					moreinfo.setModal(true);
					moreinfo.setVisible(true);
			}
		});
		mnHelp.add(mntmAbout);
		
		JMenuItem mntmHelp = new JMenuItem("Welcome");
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Welcome welcome = new Welcome();
				welcome.setModal(true);
				welcome.setVisible(true);
			}
		});
		mnHelp.add(mntmHelp);
		
		JLabel lblNewLabel = new JLabel("Welcome to UML Trace Magic");
		lblNewLabel.setIcon(createImageIcon("org.eclipse.utm/src/org/eclipse/utm/resources/Magic_icon_16.png", "A magical icon"));
		lblNewLabel.setBounds(115, 6, 179, 16);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(lblNewLabel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Input", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(26, 31, 345, 108);
		frame.getContentPane().add(panel);
		
		JScrollPane scrollPane = new JScrollPane();
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		btnSelectUmlFile = new JButton("Select Your UML file");
		btnSelectUmlFile.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnSelectUmlFile.addActionListener(new ActionListener() {
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
		
		btnSelectJava = new JButton("Select Your Java Source files");
		btnSelectJava.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnSelectJava.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				OpenFile of = new OpenFile();
				
				try {
					of.PickMe();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				textAreaJavaSourceCode.setText(of.sb.toString());
			
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
					.addGap(15))
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(btnSelectUmlFile, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnSelectJava, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSelectUmlFile)
						.addComponent(btnSelectJava)))
		);
		
		textAreaJavaSourceCode = new JTextArea();
		scrollPane_1.setViewportView(textAreaJavaSourceCode);
		
		textAreaUML = new JTextArea();
		scrollPane.setViewportView(textAreaUML);
		panel.setLayout(gl_panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Start Trace", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(27, 139, 345, 65);
		frame.getContentPane().add(panel_1);
		
		btnNewButton = new JButton("Trace UML file");
		btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					frame.dispose();
					GuiUmlToJava output = new GuiUmlToJava();
					output.setVisible(true);
			}catch(Exception e2){
				JOptionPane.showMessageDialog(null, e2);
			}
			}
		});
		
		btnNewButton_1 = new JButton("Trace Java Source file");
		btnNewButton_1.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					//frame.dispose();
					frame.setVisible(false); 
					GuiJavaToUml output = new GuiJavaToUml();
					output.setVisible(true);
			}catch(Exception e2){
				JOptionPane.showMessageDialog(null, e2);
			}
			}
		});
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
					.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewButton)
						.addComponent(btnNewButton_1))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path, String description) {
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}

}
