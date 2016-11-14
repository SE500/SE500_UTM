package org.eclipse.utm.views;
/**
 * @author junqianfeng
 */

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.utm.parseSource.ParseSource;
import org.eclipse.utm.parseUML.ParseUML;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

public class ViewOpenMenu extends ViewPart {
	public ViewOpenMenu() {}

	public static final String ID = "org.eclipse.utm.views.ViewOpenMenu";
	
	private Text textUML;
	private Text textJAVA;
	private Button btnChooseUmlFile;
	private Button btnChooseSourceCode;
	private Button btnTraceabilityMatrix;
	private Group grpTraceUml;
	private Group grpOutput;
	File umlFile,javaFile,projectDirectory;
	Display display = new Display();
	Shell shell = new Shell(display);
	
	@Override
	public void createPartControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
   		container.setLayout(null);
	
		grpTraceUml = new Group(container, SWT.BORDER | SWT.SHADOW_ETCHED_OUT);
		grpTraceUml.setFont(SWTResourceManager.getFont("Times New Roman", 13, SWT.BOLD));
		grpTraceUml.setText("Input");
		grpTraceUml.setBounds(22, 30, 408, 96);
		
		btnChooseUmlFile = new Button(grpTraceUml, SWT.NONE);
		btnChooseUmlFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog browse = new FileDialog(btnChooseUmlFile.getShell(),SWT.OPEN);
				browse.setText("Choose UML File");
				final String[] filterEx = {"*.uml","*.java"};
				browse.setFilterExtensions(filterEx);
				String selected = browse.open();
				if(selected == null)  textUML.setText("No UML File selected!");		
				else textUML.setText(browse.getFileName());
				textUML.setEnabled(true);
				umlFile=new File(selected);
			}
		});
		btnChooseUmlFile.setBounds(20, 44, 159, 28);
		btnChooseUmlFile.setText("Choose UML File");
		
		btnChooseSourceCode = new Button(grpTraceUml, SWT.NONE);
		btnChooseSourceCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog browse = new FileDialog(btnChooseSourceCode.getShell(),SWT.OPEN);
				browse.setText("Choose Java Source Files");
				final String[] filterEx = {"*.uml","*.java"};
				browse.setFilterExtensions(filterEx);
				String selected = browse.open();
				if(selected == null)  textJAVA.setText("No Java Source File selected!");		
				else textJAVA.setText(browse.getFileName());
				textJAVA.setEnabled(true);
				javaFile=new File(selected);
				
			}
		});
		btnChooseSourceCode.setBounds(200, 44, 171, 28);
		btnChooseSourceCode.setText("Choose Source Code");
		
		textUML = new Text(grpTraceUml, SWT.BORDER);
		textUML.setBounds(20, 10, 159, 28);
		
		textJAVA = new Text(grpTraceUml, SWT.BORDER);
		textJAVA.setBounds(200, 10, 171, 28);
		
		Label lblWelcome = new Label(container, SWT.NONE);
		lblWelcome.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblWelcome.setBounds(141, 10, 151, 26);
		lblWelcome.setText("Welcome to Trace Magic");
		
		grpOutput = new Group(container, SWT.NONE);
		grpOutput.setFont(SWTResourceManager.getFont("Times New Roman", 13, SWT.BOLD));
		grpOutput.setText("Output");
		grpOutput.setBounds(22, 124, 408, 58);
		
		Label lblStartTrace = new Label(grpOutput, SWT.NONE);
		lblStartTrace.setBounds(10, 13, 87, 14);
		lblStartTrace.setText("Start Trace");
		
		btnTraceabilityMatrix = new Button(grpOutput, SWT.NONE);
		btnTraceabilityMatrix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Boolean compute = null;
				if(umlFile == null || javaFile ==null){MessageDialog.openWarning(shell, "Warning", "Please Select an UML File or Java Source Code Files!");}
				else{compute = true;}
				computeTraceability(compute);
				}
		});
		btnTraceabilityMatrix.setBounds(91, 6, 171, 28);
		btnTraceabilityMatrix.setText("Start");
}

 private void computeTraceability(Boolean compute) {
	 parseUML();
     parseSource();
     Compare();
     showResultsView();
	
}
 
private void Compare() {
	// TODO Auto-generated method stub
	
}

private void showResultsView() {
	IWorkbenchPage page = getSite().getPage();
	IViewPart resultsView = page.findView(ViewResult.ID);
	if(resultsView == null){
		try {
			resultsView = page.showView(ViewResult.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	if(resultsView != null){
		getSite().getPage().bringToTop(resultsView);
		ViewResult rv = (ViewResult) resultsView;
		rv.showResults();
	}	
}


private void parseSource() {
	File selectedSource = ParseSource.selectSource();
	ParseSource readfile = new ParseSource(selectedSource);
	readfile.launch();
}

private void parseUML() {
	File selectedModel = ParseUML.selectUmlFile();
	ParseUML umlFile = new ParseUML(selectedModel);
	try {
		umlFile.launch(true);
	} catch (IOException e) {
		e.printStackTrace();
	}
		
}
	

 private void emptyDirectory(File thisDir) {
	 File[] listOfFiles = thisDir.listFiles();
	 for (int i = 0; i < listOfFiles.length; i++) {
	 if (listOfFiles[i].isFile()) {
	 listOfFiles[i].delete();
	} else if (listOfFiles[i].isDirectory()) {
	emptyDirectory(listOfFiles[i]);
	listOfFiles[i].delete();
	 }
	 }
}
	 
public void setFocus() {
}
}