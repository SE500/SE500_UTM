package com.eclipse.utm.views;

import java.io.File;

import javax.swing.JFileChooser;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
/**
 * 
 * @author junqianfeng
 *
 */
public class ViewOpen extends ViewPart {
	public ViewOpen() {
	}

	public static final String ID = "com.free.view.treeview.views.ViewOpen";
	
	 Composite mainPanel;
	private Text textAreaUML;
	private Text textAreaJavaSourceCode;
	private Button btnShowResults;
	private Button btnChooseJavaSource2;
	private Button btnChooseUmlFile;
	private Label lblFilenameSingle;
	File umlFile;
	File javaFile;
	private Button btnBrowseSingle;
	private Button btnCompute;


@Override
public void createPartControl(Composite parent) {
	Composite container = new Composite(parent, SWT.NONE);
   		container.setLayout(null);
   
   		Group grpUml = new Group(container, SWT.NONE);
   		grpUml.setBounds(5, 24, 374, 60);
   		grpUml.setText("Choose Files");
   		Label lblFilenameUML = new Label (grpUml, SWT.NONE);
   		lblFilenameUML.setSize(29, 14);
   		lblFilenameUML.setLocation(163, 17);
   		lblFilenameUML.setText("No file selected");
   		lblFilenameUML.setEnabled(false);
   		lblFilenameUML.pack();
   		
   		Button btnBrowseUML = new Button(grpUml, SWT.PUSH);
   		btnBrowseUML.setText("Choose UML");
   		btnBrowseUML.setLocation(10, 10);
   		btnBrowseUML.pack();
   		btnBrowseUML.addSelectionListener(new SelectionAdapter() {
   			@Override
   			public void widgetSelected(SelectionEvent e) {
   				  FileDialog dlg = new FileDialog(btnBrowseUML.getShell(),  SWT.OPEN  );
   		         dlg.setText("Select Class Diagram");
   		         final String[] allowedExtensions = {"*.uml"};
   		         dlg.setFilterExtensions(allowedExtensions);
   		         String path = dlg.open();
   		         if (path == null) return;
   		         lblFilenameUML.setText(dlg.getFileName());
   		         lblFilenameUML.setEnabled(true);
   		         umlFile = new File(path);
   			}
   		});
   		grpUml.pack();
   Group grpJavaSourceCode = new Group(container, SWT.NONE);
   grpJavaSourceCode.setBounds(5, 89, 217, 60);
   grpJavaSourceCode.setText("Choose Files"); 
   
            lblFilenameSingle = new Label (grpJavaSourceCode, SWT.NONE);
            lblFilenameSingle.setBounds(211, 17, 49, 14);
            lblFilenameSingle.setText("No file selected");
            lblFilenameSingle.setEnabled(false);
            lblFilenameSingle.pack();
            
            btnBrowseSingle = new Button(grpJavaSourceCode, SWT.PUSH);
            btnBrowseSingle.setText("Choose Java Source File");
            //       btnBrowseSingle.setEnabled(ture);
                     btnBrowseSingle.setLocation(10, 10);
                     btnBrowseSingle.pack();
                     btnBrowseSingle.addSelectionListener(new SelectionAdapter() {
                     	@Override
                     	public void widgetSelected(SelectionEvent e) {
                     		 FileDialog dlg = new FileDialog(btnBrowseSingle.getShell(),  SWT.OPEN  );
                             dlg.setText("Select Java File");
                             final String[] allowedExtensions = {"*.java"};
                             dlg.setFilterExtensions(allowedExtensions);
                             String path = dlg.open();
                             if (path == null) return;
                             lblFilenameSingle.setText(dlg.getFileName());
                             lblFilenameSingle.setEnabled(true);
                             javaFile = new File(path);
                     	}
                     });
                     grpJavaSourceCode.pack();
   

   Button btnCompute_1 = new Button(container, SWT.PUSH);
   btnCompute_1.setBounds(5, 155, 58, 28);
   btnCompute_1.setText("Start");
   btnCompute_1.pack();
   
   Label lblWelcomeToUtm = new Label(container, SWT.NONE);
   lblWelcomeToUtm.setBounds(233, 10, 166, 14);
   lblWelcomeToUtm.setText("Welcome To UTM");
   btnCompute_1.addSelectionListener(new SelectionAdapter() {
   	@Override
   	public void widgetSelected(SelectionEvent e) {
   	 Boolean doProject = null;
     if (umlFile == null) {
             //TODO: Show error message - no UML file selected
             return;}else{
            	 doProject = true;
             }
     if(javaFile==null){
    	 return;
     }else{
    	 doProject=true;
     }	 	computeTraceability(doProject);
   	}
   });
	
	createActions();
	initializeToolBar();
	initializeMenu();
}

 private void computeTraceability(Boolean doProject) {
	 parseUML();
      parseJava(doProject);
   //   Compare.compare();
      showResultsView();
	
}
 
private void showResultsView() {
	// TODO Auto-generated method stub
	
}


private void parseJava(Boolean doProject) {
	// TODO Auto-generated method stub
	
}


private void parseUML() {
	// TODO Auto-generated method stub
	
}


private void createActions() {
}
private void initializeToolBar() {
	IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
}	
private void initializeMenu() {
	IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
}	
public void setFocus() {
	// Set the focus
}
}