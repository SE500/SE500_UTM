package org.eclipse.utm.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.utm.parseUML.ParseUML;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;

public class TestGui extends ViewPart{
	
	public static final String ID = "org.eclipse.utm.views.TestGui"; //$NON-NLS-1$
	private File selectedUmlFile;
	private File selectedJavaSourceFile;
	private Label lblSelectedUmlFilePath;
	private Label lblSelectedJavaSourcePath;
	
	public TestGui() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(null);
		
		Button btnSelectUmlFile = new Button(parent, SWT.NONE);
		btnSelectUmlFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedUmlFile = ParseUML.selectUmlFile();
				lblSelectedUmlFilePath.setText(selectedUmlFile.getName());
			}
		});
		btnSelectUmlFile.setBounds(25, 25, 150, 25);
		btnSelectUmlFile.setText("Select UML File");
		
		lblSelectedUmlFilePath = new Label(parent, SWT.NONE);
		lblSelectedUmlFilePath.setBounds(180, 25, 350, 25);
		lblSelectedUmlFilePath.setText("");		
		
		Button btnSelectJavaSource = new Button(parent, SWT.NONE);
		btnSelectJavaSource.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedJavaSourceFile = selectFile();
				lblSelectedJavaSourcePath.setText(selectedJavaSourceFile.getName());
			}
		});
		btnSelectJavaSource.setBounds(25, 65, 150, 25);
		btnSelectJavaSource.setText("Select Java Source Code");
				
		lblSelectedJavaSourcePath = new Label(parent, SWT.NONE);
		lblSelectedJavaSourcePath.setBounds(180, 65, 350, 25);
		lblSelectedJavaSourcePath.setText("");
		
		Button btnStartProcess = new Button(parent, SWT.NONE);
		btnStartProcess.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(selectedUmlFile != null) {
					ParseUML test = new ParseUML(selectedUmlFile);
					try {
						test.launch(true);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
				else {
					System.err.println("Error: No UML File selected.");
				}
			}
		});
		btnStartProcess.setBounds(25, 105, 150, 25);
		btnStartProcess.setText("Start Comparison");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Select a directory or file
	 * @return the UML file selected or null
	 */
	private File selectFile(){
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }catch(Exception ex) {
	        ex.printStackTrace();
	    }
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
		    return fileChooser.getSelectedFile();
		}
		return null;
	}
}
