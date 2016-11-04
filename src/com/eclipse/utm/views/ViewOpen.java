package com.eclipse.utm.views;

import java.io.File;

import javax.swing.JFileChooser;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ViewOpen extends ViewPart {

	public static final String ID = "com.free.view.treeview.views.ViewOpen"; 
	private Text getUMLFileName;
	private Text getJavaSourceFileName;

	public ViewOpen() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
	//Button of choose UML file:	
		Button btnChooseUmlFile = new Button(container, SWT.NONE);
		btnChooseUmlFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JFileChooser fs = new JFileChooser(new File("c:\\"));
				fs.setDialogTitle("Open a File");
				fs.setFileFilter(new FileTypeFilter(".uml","UML File"));
				int result = fs.showOpenDialog(null);
				  StringBuilder sb = new StringBuilder();
				if (result == JFileChooser.APPROVE_OPTION){
//					System.out.println("Folder Name"+ fs.getCurrentDirectory());
//					System.out.println("Folder Name"+ fs.getSelectedFile().getPath());
					sb.append(fs.getSelectedFile().getName());
				}else{
					sb.append("no file was selected.");
				}
				getUMLFileName.setText(sb.toString());	
			}
		});
		btnChooseUmlFile.setBounds(31, 32, 155, 28);
		btnChooseUmlFile.setText("Choose UML file:");
		
	//Button of choose Java source files:	
		Button btnChooseJavaSource = new Button(container, SWT.NONE);
		btnChooseJavaSource.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//open file
				JFileChooser fs = new JFileChooser(new File("c:\\"));
				fs.setDialogTitle("Open a File");
				fs.setFileFilter(new FileTypeFilter(".java","Java File"));
				int result = fs.showOpenDialog(null);
				  StringBuilder sb = new StringBuilder();
				if (result == JFileChooser.APPROVE_OPTION){
//					System.out.println("Folder Name"+ fs.getCurrentDirectory());
//					System.out.println("Folder Name"+ fs.getSelectedFile().getPath());
					sb.append(fs.getSelectedFile().getName());
				}else{
					sb.append("no file was selected.");
				}
				getJavaSourceFileName.setText(sb.toString());	
			}
		});
		btnChooseJavaSource.setBounds(31, 79, 173, 28);
		btnChooseJavaSource.setText("Choose Java Source Files:");
		
	//show File name in text field	
		getUMLFileName = new Text(container, SWT.BORDER);
		getUMLFileName.setBounds(210, 36, 155, 19);
		
		getJavaSourceFileName = new Text(container, SWT.BORDER);
		getJavaSourceFileName.setBounds(210, 83, 155, 19);
		
	//Button of get result of UML to Java Source File:	
		Button btnGetResultsUMLtoJava = new Button(container, SWT.NONE);
		btnGetResultsUMLtoJava.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Method of 
				//IWorkbenchPage.showView(viewId, secondardId, IWorbenchPage.VEW_ACTIVATE);
			//	IWorkbenchPage page = getSite().getPage();
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					page.showView("com.free.view.treeview.views.TreeView");
				} catch (PartInitException e1) {
				
					e1.printStackTrace();
				}
			}
		});
		btnGetResultsUMLtoJava.setBounds(413, 32, 95, 28);
		btnGetResultsUMLtoJava.setText("Get Results:");
		
	//Button of get result of JAVA Source file to UML:	
		Button btnGetResultsJavatoUML = new Button(container, SWT.NONE);
		btnGetResultsJavatoUML.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//get results
			}
		});
		btnGetResultsJavatoUML.setBounds(413, 79, 95, 28);
		btnGetResultsJavatoUML.setText("Get Results:");

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
