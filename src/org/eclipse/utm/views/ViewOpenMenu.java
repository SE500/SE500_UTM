package org.eclipse.utm.views;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.utm.UTMActivator;
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

	/**
	 * 	The ViewOpenMenu ViewPart ID
	 */
	public static final String ID = "org.eclipse.utm.views.ViewOpenMenu";

	/**
	 *  The parent of this ViewPart
	 */
	private Composite parent;

	/**
	 * 	The selected UML file and Java Source File/Directory
	 */
	private File umlFile = null, 
			javaFile = null;

	/**
	 * 	Text areas that display the selected file names
	 */
	private Text textUML, textJAVA;

	/**
	 * The path to the source code generated from the UML model
	 */
	private String generatedSourcePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() +
			System.getProperty("file.separator") + "org.eclipse.utm.u2j" +
			System.getProperty("file.separator") + "src";


	/**
	 * Empty Constructor
	 */
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

		Group grpTraceUml = new Group(container, SWT.BORDER | SWT.SHADOW_ETCHED_OUT);
		grpTraceUml.setText("Input");
		grpTraceUml.setBounds(20, 30, 400, 95);

		Button btnChooseUmlFile = new Button(grpTraceUml, SWT.NONE);
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

		Button btnChooseSourceCode = new Button(grpTraceUml, SWT.NONE);
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

		this.textUML = new Text(grpTraceUml, SWT.BORDER);
		this.textUML.setBounds(20, 20, 175, 25);

		this.textJAVA = new Text(grpTraceUml, SWT.BORDER);
		this.textJAVA.setBounds(200, 20, 175, 25);

		Label lblWelcome = new Label(container, SWT.NONE);
		lblWelcome.setBounds(141, 10, 151, 15);
		lblWelcome.setText("Welcome to Trace Magic");

		Group grpOutput = new Group(container, SWT.NONE);
		grpOutput.setText("Output");
		grpOutput.setBounds(22, 125, 400, 60);

		Button btnTraceabilityMatrix = new Button(grpOutput, SWT.NONE);
		btnTraceabilityMatrix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(umlFile == null || javaFile == null){
					MessageDialog.openWarning(null, "Warning", 
							"Please Select an UML File or Java Source Code Files!");
				}
				else {
					showConsole();
					hideResults();
					Job job = new Job("UML Trace Magic : " + umlFile.getName()) {
						@Override
						protected IStatus run(IProgressMonitor monitor) {
							computeTraceability();
							if (isModal(this)) {
								// The progress dialog is still open so
								// just show the results view
								showResults();
							} else {
								setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
								setProperty(IProgressConstants.ACTION_PROPERTY, 
										getShowResultsViewAction());
							}
							return Status.OK_STATUS;
						}
					};
					job.setUser(true);
					job.schedule();
				}
			}
		});
		btnTraceabilityMatrix.setBounds(105, 20, 175, 25);
		btnTraceabilityMatrix.setText("Start");

		//		Button btnShowResults = new Button(grpOutput, SWT.NONE);
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
		// Create a Progress Group
		IProgressMonitor UTMProgressGroupMonitor = Job.getJobManager().createProgressGroup();

		try {
			UTMProgressGroupMonitor.beginTask("UML Trace Magic : "+ this.umlFile.getName(), 90);
			// (1) Parse the UML by first generating java
			ParseUML parseUMLJob = new ParseUML(this.umlFile);
			parseUMLJob.setProgressGroup(UTMProgressGroupMonitor, 30);
			parseUMLJob.schedule();
			// (2) Parse the Source Code
			ParseSource parseSourceJob = new ParseSource(this.javaFile);
			parseSourceJob.setProgressGroup(UTMProgressGroupMonitor, 30);
			parseSourceJob.schedule();
			// Jobs 1 & 2 occur concurrently - Wait till both are complete
			parseUMLJob.join();
			parseSourceJob.join();
			// (3) Parse the UML Generated Source Code
			ParseSource parseGeneratedSourceJob = new ParseSource(this.generatedSourcePath, this.umlFile.getName());
			parseGeneratedSourceJob.setProgressGroup(UTMProgressGroupMonitor, 30);
			parseGeneratedSourceJob.schedule();
			// Wait for Job 3 to finish
			parseGeneratedSourceJob.join();
			// (4) Clean up and delete the UML Generated Source Code
			Job removeGeneratedSourceJob = new Job("Remove the source code generated from : "
					+ this.umlFile.getName()) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					removeGeneratedProject(monitor);
					return Status.OK_STATUS;
				}
			};
			removeGeneratedSourceJob.setProgressGroup(UTMProgressGroupMonitor, 10);
			removeGeneratedSourceJob.schedule();
			removeGeneratedSourceJob.join();
		} catch (InterruptedException e) {
			IStatus status = new Status(IStatus.INFO, UTMActivator.PLUGIN_ID, e.getMessage(),  e);
			UTMActivator.getDefault().getLog().log(status);
			e.printStackTrace();
		} finally {
			UTMProgressGroupMonitor.done();
		}
	}

	/**
	 * A method used to trigger output view.
	 * 
	 * @exception PartInitException
	 */
	//	protected void showResultsView() {
	//		IWorkbenchPage page = getSite().getPage();
	//		IViewPart resultsView = page.findView(ViewResult.ID);
	//		if(resultsView == null){
	//			try {
	//				resultsView = page.showView(ViewResult.ID);
	//			} catch (PartInitException e) {
	//				IStatus status = new Status(e.getStatus().getSeverity(), UTMActivator.PLUGIN_ID, e.getStatus().getCode(), e.getMessage(),  e);
	//				UTMActivator.getDefault().getLog().log(status);
	//				e.printStackTrace();
	//			}
	//		}
	//		if(resultsView != null){
	//			getSite().getPage().bringToTop(resultsView);
	//			ViewResult rv = (ViewResult) resultsView;
	//			rv.showResults();
	//		}	
	//	}

	/**
	 * Creates a runnable action to show the results view
	 * @return
	 * 		Returns the "View UML Trace Magic Results" action
	 */
	protected Action getShowResultsViewAction() {
		return new Action("View UML Trace Magic Results") {
			public void run() {
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
		};
	}
	
	/**
	 * A method to show the results from a non-UI thread
	 */
	protected void showResults() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getShowResultsViewAction().run();
			}
		});
	}
	
	/**
	 * Checks whether a job is modal or not
	 * @param job
	 * @return
	 * 		returns true if it is Modal, false if not
	 */
	public boolean isModal(Job job) {
		Boolean isModal = (Boolean)job.getProperty(
				IProgressConstants.PROPERTY_IN_DIALOG);
		if(isModal == null) return false;
		return isModal.booleanValue();
	}
	
	/**
	 * Hides the results view and resets it ready to be recreated
	 */
	private void hideResults() {
		getSite().getPage().hideView(getSite().getPage().findView(ViewResult.ID));
	}
	
	/**
	 * Shows the plug-in specific console
	 */
	private void showConsole() {
		try {
			IWorkbenchPage page = getSite().getPage();
			String id = IConsoleConstants.ID_CONSOLE_VIEW;
			IConsoleView view = (IConsoleView) page.showView(id);
			view.display(UTMActivator.utmConsole);
		} catch (PartInitException e) {
			IStatus status = new Status(e.getStatus().getSeverity(), UTMActivator.PLUGIN_ID, e.getStatus().getCode(), e.getMessage(),  e);
			UTMActivator.getDefault().getLog().log(status);
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes the UML generated workspace project
	 * @param monitor
	 * 		The progress monitor passed to delete
	 */
	public void removeGeneratedProject(IProgressMonitor monitor) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		// Loop over all projects
		for (IProject project : projects) {
			if (project.getName().equals("org.eclipse.utm.u2j")) {
				try {
					project.delete(true, true, monitor);
				} catch (CoreException e) {
					IStatus status = new Status(e.getStatus().getSeverity(), UTMActivator.PLUGIN_ID, e.getStatus().getCode(), e.getMessage(), e);
					UTMActivator.getDefault().getLog().log(status);
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * Parsing the focus request to the viewer's control.
	 */
	public void setFocus() {
		parent.setFocus();
	}
}