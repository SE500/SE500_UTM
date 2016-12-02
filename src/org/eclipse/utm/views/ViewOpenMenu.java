package org.eclipse.utm.views;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.utm.parseSource.ParseSource;
import org.eclipse.utm.parseUML.ParseUML;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
/**
 * The input view part of UML Trace Magic plug-in application that let users input the 
 * UML model and Source Code.
 * 
 * 
 * @version Neon.1a Release (4.6.1)
 * @author junqianfeng,Thomas Colborne
 * @since 2016-09
 *
 */

public class ViewOpenMenu extends ViewPart {

	public static final String ID = "org.eclipse.utm.views.ViewOpenMenu";

	private Text textUML;
	private Text textJAVA;
	private Button btnChooseUmlFile;
	private Button btnChooseSourceCode;
	private Button btnTraceabilityMatrix;
	private Group grpTraceUml;
	private Group grpOutput;
	File umlFile = null, 
			javaFile = null,
			projectDirectory = null;
	//	private UTMDB db = null;
	boolean sourceParsed = false;
	boolean umlParsed = false;
	Composite parent;
	//	private Button btnShowResults;
	private ParseUML parseUMLJob;
	private ParseSource parseSourceJob;
	IProgressMonitor UTMProgressGroupMonitor;
	IProgressService progressService;

	//Display display = new Display();
	//Shell shell = new Shell(display);

	public ViewOpenMenu() {}

	/**
	 * The method is to create contents of the input view part.
	 * User can select UML File and Java Source Files from directory
	 * and trigger 'start' button to get result.
	 * @param parent, container
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(null);

		grpTraceUml = new Group(container, SWT.BORDER | SWT.SHADOW_ETCHED_OUT);
		grpTraceUml.setText("Input");
		grpTraceUml.setBounds(20, 30, 400, 95);

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
				if(umlFile == null) {textUML.setText("No UML File selected!");}	
				else {textUML.setText(umlFile.getName());}
				textUML.setEnabled(true);
			}
		});
		btnChooseUmlFile.setBounds(20, 50, 175, 25);
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
				if(javaFile == null) {textJAVA.setText("No Java Source File selected!");}		
				else {textJAVA.setText(javaFile.getName());}
				textJAVA.setEnabled(true);
			}
		});
		btnChooseSourceCode.setBounds(200, 50, 175, 25);
		btnChooseSourceCode.setText("Choose Java Source");

		textUML = new Text(grpTraceUml, SWT.BORDER);
		textUML.setBounds(20, 20, 175, 25);

		textJAVA = new Text(grpTraceUml, SWT.BORDER);
		textJAVA.setBounds(200, 20, 175, 25);

		Label lblWelcome = new Label(container, SWT.NONE);
		lblWelcome.setBounds(141, 10, 151, 15);
		lblWelcome.setText("Welcome to Trace Magic");

		grpOutput = new Group(container, SWT.NONE);
		grpOutput.setText("Output");
		grpOutput.setBounds(22, 125, 400, 60);

		btnTraceabilityMatrix = new Button(grpOutput, SWT.NONE);
		btnTraceabilityMatrix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(umlFile == null || javaFile == null){
					MessageDialog.openWarning(null, "Warning", 
							"Please Select an UML File or Java Source Code Files!");
				}
				else computeTraceability();
			}
		});
		btnTraceabilityMatrix.setBounds(105, 20, 175, 25);
		btnTraceabilityMatrix.setText("Start");

		//		btnShowResults = new Button(grpOutput, SWT.NONE);
		//		btnShowResults.addSelectionListener(new SelectionAdapter() {
		//			@Override
		//			public void widgetSelected(SelectionEvent e) {
		//				showResultsView();
		//			}
		//		});
		//		btnShowResults.setText("Show Results");
		//		btnShowResults.setBounds(200, 20, 175, 25);
	}
	/**
	 * This method is the action when click 'start' button, a 'showResultsView' method
	 * is used inside.
	 * 
	 * @exception throws InterruptedException
	 * @param none
	 * @return none
	 */
	private void computeTraceability() {
		UTMProgressGroupMonitor = Job.getJobManager().createProgressGroup();
		progressService = (IProgressService) getSite().getService(IProgressService.class);

		try {
			UTMProgressGroupMonitor.beginTask("Starting", 100);
			parseUML();
			parseUMLJob.setProgressGroup(UTMProgressGroupMonitor, 67);
			progressService.showInDialog(parent.getShell(), parseUMLJob);
			parseUMLJob.schedule();
			parseUMLJob.join();
			parseSource();
			parseSourceJob.setProgressGroup(UTMProgressGroupMonitor, 33);
			progressService.showInDialog(parent.getShell(), parseSourceJob);
			parseSourceJob.schedule();
			parseSourceJob.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			UTMProgressGroupMonitor.done();
		}

		showResultsView();

	}
	/**
	 * A method used to trigger output view.
	 * 
	 * @exception PartInitException
	 */
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
	/**
	 * Method call parseScource method.
	 */

	private void parseSource() {	
		parseSourceJob = new ParseSource(javaFile);
		//		sourceParsed = parseSourceJob.launch(UTMProgressGroupMonitor);
	}

	/**
	 * Method call parseUML method.
	 */

	private void parseUML() {
		parseUMLJob = new ParseUML(umlFile);
		//		umlParsed = parseUMLJob.launch(UTMProgressGroupMonitor);
	}
	/**
	 * Parsing the focus request to the viewer's control.
	 */

	public void setFocus() {
		parent.setFocus();
	}
}