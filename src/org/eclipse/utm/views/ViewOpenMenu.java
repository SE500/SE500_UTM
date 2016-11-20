package org.eclipse.utm.views;

import java.io.File;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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


public class ViewOpenMenu extends ViewPart {
	
	public static final String ID = "org.eclipse.utm.views.ViewOpenMenu";
	
	private Text textUML;
	private Text textJAVA;
	private Button btnChooseUmlFile;
	private Button btnChooseSourceCode;
	private Button btnTraceabilityMatrix;
	private Group grpTraceUml;
	private Group grpOutput;
	File umlFile = null,javaFile = null,projectDirectory;
//	private UTMDB db = null;
	boolean sourceParsed = false;
	boolean umlParsed = false;
	
	Display display = new Display();
	Shell shell = new Shell(display);
	
	public ViewOpenMenu() {}
	
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
   		container.setLayout(null);
	
		grpTraceUml = new Group(container, SWT.BORDER | SWT.SHADOW_ETCHED_OUT);
		grpTraceUml.setText("Input");
		grpTraceUml.setBounds(22, 30, 408, 96);
		
		btnChooseUmlFile = new Button(grpTraceUml, SWT.NONE);
		btnChooseUmlFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				FileDialog browse = new FileDialog(btnChooseUmlFile.getShell(),SWT.OPEN);
//				browse.setText("Choose UML File");
//				final String[] filterEx = {"*.uml","*.java"};
//				browse.setFilterExtensions(filterEx);
//				String selected = browse.open();
//				if(selected == null)  textUML.setText("No UML File selected!");		
//				else textUML.setText(browse.getFileName());
//				textUML.setEnabled(true);
//				umlFile=new File(selected);
				
				umlFile = ParseUML.selectUmlFile();
				textUML.setText(umlFile.getName());
				textUML.setEnabled(true);
			}
		});
		btnChooseUmlFile.setBounds(20, 44, 159, 28);
		btnChooseUmlFile.setText("Choose UML File");
		
		btnChooseSourceCode = new Button(grpTraceUml, SWT.NONE);
		btnChooseSourceCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				FileDialog browse = new FileDialog(btnChooseSourceCode.getShell(),SWT.OPEN);
//				browse.setText("Choose Java Source Files");
//				final String[] filterEx = {"*.uml","*.java"};
//				browse.setFilterExtensions(filterEx);
//				String selected = browse.open();
//				if(selected == null)  textJAVA.setText("No Java Source File selected!");		
//				else textJAVA.setText(browse.getFileName());
//				textJAVA.setEnabled(true);
//				javaFile=new File(selected);
				javaFile = ParseSource.selectSource();
				textJAVA.setText(javaFile.getName());
				textJAVA.setEnabled(true);
			}
		});
		btnChooseSourceCode.setBounds(200, 44, 171, 28);
		btnChooseSourceCode.setText("Choose Java Source");
		
		textUML = new Text(grpTraceUml, SWT.BORDER);
		textUML.setBounds(20, 10, 159, 28);
		
		textJAVA = new Text(grpTraceUml, SWT.BORDER);
		textJAVA.setBounds(200, 10, 171, 28);
		
		Label lblWelcome = new Label(container, SWT.NONE);
		lblWelcome.setBounds(141, 10, 151, 26);
		lblWelcome.setText("Welcome to Trace Magic");
		
		grpOutput = new Group(container, SWT.NONE);
		grpOutput.setText("Output");
		grpOutput.setBounds(22, 124, 408, 58);
		
		btnTraceabilityMatrix = new Button(grpOutput, SWT.NONE);
		btnTraceabilityMatrix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Boolean compute = null;
				if(umlFile == null || javaFile ==null){
					MessageDialog.openWarning(shell, "Warning", 
							"Please Select an UML File or Java Source Code Files!");
					}
				else compute = true;
				computeTraceability(compute);
				}
		});
		btnTraceabilityMatrix.setBounds(91, 6, 171, 28);
		btnTraceabilityMatrix.setText("Start");
}

 private void computeTraceability(Boolean compute) {
	 parseUML();
     parseSource();
     showResultsView();
	
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
	ParseSource parseSource = new ParseSource(javaFile);
	sourceParsed = parseSource.launch();
}

private void parseUML() {
	ParseUML parseUML = new ParseUML(umlFile);
	umlParsed = parseUML.launch(true);		
}
	 
public void setFocus() {
}
}