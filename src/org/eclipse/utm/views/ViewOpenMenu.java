package org.eclipse.utm.views;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.utm.UTMActivator;
import org.eclipse.utm.compare.UTMDB;
import org.eclipse.utm.parseSource.ParseSource;
import org.eclipse.utm.parseUML.ParseUML;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
/**
 * The input view part of UML Trace Magic plug-in application that let users input the 
 * UML model and Source Code.
 *  
 * @author junqianfeng,Thomas Colborne
 * *
 */
public class ViewOpenMenu extends ViewPart {

	public static final String ID = "org.eclipse.utm.views.ViewOpenMenu";

	private Text textUML;
	private Text textJAVA;
	private Button btnChooseUmlFile;
	private Button btnChooseSourceCode;
	private Button btnTraceabilityMatrix;
	//private Button btnShowResults;
	private Group grpTraceUml;
	private Group grpOutput;
	private File umlFile = null, 
			javaFile = null;
	private UTMDB db = new UTMDB();
	private ParseUML parseUMLJob;
	private ParseSource parseSourceJob;
	Composite parent;
	IProgressMonitor UTMProgressGroupMonitor;
	IProgressService progressService;
	boolean firstRun = true;
//	@Inject Shell shell;

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
				else {
					if(firstRun){
						firstRun = false;
					} else {
						if(db.IsOpen()) {db.ReInitDatabase();}
						else {
							db.Open();
							db.ReInitDatabase();
						}
					}
					computeTraceability();
				}
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
	 */
	private void computeTraceability() {
		UTMProgressGroupMonitor = Job.getJobManager().createProgressGroup();
		progressService = (IProgressService) getSite().getService(IProgressService.class);

		try {
			UTMProgressGroupMonitor.beginTask("Starting", 100);
			parseSourceJob = new ParseSource(javaFile);
			parseSourceJob.setProgressGroup(UTMProgressGroupMonitor, 33);
			progressService.showInDialog(null, parseSourceJob);
			parseSourceJob.schedule();
			parseSourceJob.join();
			parseUMLJob = new ParseUML(umlFile);
			parseUMLJob.setProgressGroup(UTMProgressGroupMonitor, 67);
			progressService.showInDialog(null, parseUMLJob);
			parseUMLJob.schedule();
			parseUMLJob.join();
		} catch (InterruptedException e) {
			IStatus status = new Status(IStatus.INFO, UTMActivator.PLUGIN_ID, e.getMessage(),  e);
			UTMActivator.getDefault().getLog().log(status);
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
				IStatus status = new Status(e.getStatus().getSeverity(), UTMActivator.PLUGIN_ID, e.getStatus().getCode(), e.getMessage(),  e);
				UTMActivator.getDefault().getLog().log(status);
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
	 * Parsing the focus request to the viewer's control.
	 */
	public void setFocus() {
		parent.setFocus();
	}
}