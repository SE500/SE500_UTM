package org.eclipse.utm.views;

import java.io.File;

//import javax.swing.JFileChooser;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.DirectoryDialog;
//import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
//import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.utm.compare.UTMDB;
import org.eclipse.utm.parseSource.ParseSource;
import org.eclipse.utm.parseUML.ParseUML;
import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.layout.GridData;

public class ViewOpen extends ViewPart {
	

	public static final String ID = "org.eclipse.utm.views.ViewOpen";
	
	//private Composite mainPanel;
	//private Label lblFilenameSingle;
	//private Button btnBrowseSingle;

	private File umlFile = null;
	private File javaFile = null;
	private UTMDB db = null;
	boolean sourceParsed = false;
	boolean umlParsed = false;

	public ViewOpen() {
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
	   	container.setLayout(null);
	 
	    Label lblWelcomeToUtm = new Label(container, SWT.NONE);
	    lblWelcomeToUtm.setBounds(20, 10, 95, 15);
	    lblWelcomeToUtm.setText("Welcome To UTM");
	   	
		Group grpUml = new Group(container, SWT.NONE);
		grpUml.setBounds(20, 45, 350, 50);
		grpUml.setText("UML Class Diagram");
		grpUml.setLayout(null);
		Label lblFilenameUML = new Label (grpUml, SWT.NONE);
		lblFilenameUML.setBounds(175, 25, 170, 15);
		lblFilenameUML.setText("No file selected");
		lblFilenameUML.setEnabled(false);
		//lblFilenameUML.pack();
		
		Button btnBrowseUML = new Button(grpUml, SWT.PUSH);
		btnBrowseUML.setLocation(10, 20);
		btnBrowseUML.setText("Choose UML File");
		btnBrowseUML.pack();
		btnBrowseUML.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//FileDialog dlg = new FileDialog(btnBrowseUML.getShell(),  SWT.OPEN  );
		        //dlg.setText("Select UML Class Diagram");
		        //final String[] allowedExtensions = {"*.uml"};
		        //dlg.setFilterExtensions(allowedExtensions);
		        //String path = dlg.open();
		        //if (path == null) return;
		        //lblFilenameUML.setText(dlg.getFileName());
		        //lblFilenameUML.setEnabled(true);
		        //umlFile = new File(path);
				umlFile = ParseUML.selectUmlFile();
				lblFilenameUML.setText(umlFile.getName());
		        lblFilenameUML.setEnabled(true);
			}
		});
		//grpUml.pack();
	   
	
		Group grpJavaSourceCode = new Group(container, SWT.NONE);
		grpJavaSourceCode.setBounds(20, 100, 350, 50);
		grpJavaSourceCode.setText("Java Project Directory or File"); 
	   
		Label lblFilenameSingle = new Label (grpJavaSourceCode, SWT.NONE);
		lblFilenameSingle.setBounds(175, 25, 170, 15);
		lblFilenameSingle.setText("No file selected");
	    lblFilenameSingle.setEnabled(false);
	    //lblFilenameSingle.pack();
	    
	    Button btnBrowseSingle = new Button(grpJavaSourceCode, SWT.PUSH);
	    btnBrowseSingle.setText("Choose Java Source");
		//btnBrowseSingle.setEnabled(ture);
		btnBrowseSingle.setLocation(10, 20);
		btnBrowseSingle.pack();
		btnBrowseSingle.addSelectionListener(new SelectionAdapter() {
			@Override
	     	public void widgetSelected(SelectionEvent e) {
				//FileDialog dlg = new FileDialog(btnBrowseSingle.getShell(),  SWT.OPEN  );
				//dlg.setText("Select Java Project Directory or File");
				//final String[] allowedExtensions = {"*.java"};
				//dlg.setFilterExtensions(allowedExtensions);
				//String path = dlg.open();
				//if (path == null) return;
				//lblFilenameSingle.setText(dlg.getFileName());
				//lblFilenameSingle.setEnabled(true);
				//javaFile = new File(path);
				javaFile = ParseSource.selectSource();
				lblFilenameSingle.setText(javaFile.getName());
				lblFilenameSingle.setEnabled(true);
	     	}
	     });
		//grpJavaSourceCode.pack();
		Button btnStart = new Button(container, SWT.PUSH);
		//btnStart.setBounds(20, 155, 75, 28);
		btnStart.setText("Start");
		btnStart.setLocation(20, 155);
		btnStart.pack();
		btnStart.addSelectionListener(new SelectionAdapter() {
		   	@Override
		   	public void widgetSelected(SelectionEvent e) {
		   		/*Boolean ParseJava = null;
		   		if (umlFile == null) {
		        	return;
		        } else {
		        	ParseJava = true;
		        } 
		   		
		   		if(javaFile == null){
		        	return;
	        	} else {
		        	ParseJava=false;
	        	}	 	
		        computeTraceability(ParseJava);*/
		   		
		   		if(umlFile == null||javaFile == null)
		   			return;
		   		
		   		ParseSource parseSource = new ParseSource(javaFile);
				sourceParsed = parseSource.launch();
				
				ParseUML parseUML = new ParseUML(umlFile);
				umlParsed = parseUML.launch(true);
		   		
				if(sourceParsed && umlParsed) {
					db = new UTMDB();
					db.Open();
					db.InitDatabase();
					db.Relate();
					db.Match();
					db.Commit();
					System.out.println("UML Class Count:\t" + db.CountUMLClasses());
					System.out.println("Source Class Count:\t" + db.CountSourceClasses());
					System.out.println("UML Attribute Count:\t" + db.CountUMLAttributes());
					System.out.println("Source Attribute Count:\t" + db.CountSourceAttributes());
					System.out.println("UML Method Count:\t" + db.CountUMLMethods());
					System.out.println("Source Method Count:\t" + db.CountSourceMethods());
				}
		   	}
		});
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	private void computeTraceability(Boolean ParseJava) {
		 parseUML();
	     parseJava(ParseJava);
	 //    UTMDB.Match();
	     showResultsView();
		
	}
 
	private void showResultsView() {
		 IWorkbenchPage page = getSite().getPage();
	     IViewPart resultsView = page.findView("org.eclipse.utm.views.ViewResult");
	     if (resultsView == null) {
	             try {
	                     resultsView = page.showView("org.eclipse.utm.views.ViewResult");
	             } catch (PartInitException e) {
	                     e.printStackTrace();
	             }
	     }
	     if (resultsView != null) {
	             getSite().getPage().bringToTop(resultsView);
	             ViewResult rv = (ViewResult) resultsView;
	             rv.showResults();
	     }
			
		
	}


	private void parseJava(Boolean doProject) {
	//	 try {
	//         if (doProject) {
	//                 JavaExtractor.collectFiles(projectDirectory, false);
	//         } else {
	//                 JavaExtractor.extractFromFile(javaFile, false);
	//         }
	// } catch (IOException e) {
	//         e.printStackTrace();
	// }
	}


	private void parseUML() {
	//	 File genSrcDir = new File("src-gen");
	//     if (!genSrcDir.exists()) genSrcDir.mkdir();
	//     else {
	//             emptyDirectory(genSrcDir);
	//     }
	//     
	//     URI model = URI.createFileURI(umlFile.getAbsolutePath());
	//     List<String> arguments = new ArrayList<String>();
	//     try {
	//             Generate g = new Generate(model, genSrcDir, arguments);
	//             g.doGenerate(null);
	//             JavaExtractor.collectFiles(genSrcDir, true);
	//     } catch (IOException ex) {
	//             ex.printStackTrace();
	//     }
	//	
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


	private void createActions() {
	}
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
	}	
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
	}	
	public void setFocus() {
	}
}