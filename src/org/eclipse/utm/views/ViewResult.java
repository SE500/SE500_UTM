package org.eclipse.utm.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.*;
import org.eclipse.utm.compare.UTMDB;
import org.eclipse.utm.compare.UTMDBAttribute;
import org.eclipse.utm.compare.UTMDBClass;
import org.eclipse.utm.compare.UTMDBMethod;
import org.eclipse.swt.layout.FillLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ViewResult extends ViewPart {
	public static final String ID = "org.eclipse.utm.views.ViewResult"; 

	Tree tree1, tree2, tree3;
	Composite container;
	Shell shell;
	/**
	 * Create contents of the view part.
	 * @param container
	 */
	@Override

	public void createPartControl(Composite parent) {
		container = new Composite(parent,SWT.NONE);
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		//Create Tree_1 for showing result of Java Source snippet
		tree1 = new Tree(container,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree1.setBounds(0, 0, 300, 469);
		tree1.setHeaderVisible(true);
		tree1.setLinesVisible(true);

		//Create Tree_2 for showing result of parsing UML 
		tree2 = new Tree(container,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree2.setBounds(300, 0, 300, 469);
		tree2.setHeaderVisible(true);
		tree2.setLinesVisible(true); 

		//Create Tree_3 for showing result of Comparison
		tree3 = new Tree(container,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree3.setBounds(600, 0, 300, 469);
		tree3.setHeaderVisible(true);
		tree3.setLinesVisible(true);

		// Tree 1
		TreeColumn columnSourceClasses = new TreeColumn(tree1, SWT.CENTER);
		columnSourceClasses.setText("Class/Atrribute/Method Found within Source");
		columnSourceClasses.setWidth(300);

		//Tree 2
		TreeColumn columnUmlClasses = new TreeColumn(tree2, SWT.CENTER);
		columnUmlClasses.setText("Class/Atrribute/Method Found within UML");
		columnUmlClasses.setWidth(300);

		//Tree 3
		TreeColumn columnCompare = new TreeColumn(tree3, SWT.CENTER);
		columnCompare.setText("Compare Result");
		columnCompare.setWidth(300);

		//////////////////////////Test case///////////////////////////////////////////////////////////   
		//		    TreeItem item1 = new TreeItem(tree, SWT.NONE);
		//		    item1.setText(new String[]{"               item1","yes","32"});
		//		    TreeItem subItem1 = new TreeItem(item1, SWT.NONE);
		//		    subItem1.setText(new String[]{"               subItem1","yes","55"});
		//		    TreeItem subsubItem1 = new TreeItem(subItem1, SWT.NONE);
		//		    subsubItem1.setText(new String[]{"               subsubitem1","No","88"});
		//		    TreeItem item2 = new TreeItem(tree, SWT.NONE);
		//		    item2.setText(new String[]{"               item2","yes","62"});  
		//		    TreeItem subItem2 = new TreeItem(item2, SWT.NONE);
		//		    subItem2.setText(new String[]{"               subItem2","no","99"});
		//////////////////////////////////////////////////////////////////////////////////////////////// 
	}

	public void showResults(){

		UTMDB db = new UTMDB();
		db.Open();
		db.InitDatabase();
		db.Relate();
		db.Match();
		db.Commit();

		if(db.IsInitialized())
		{
			//Create Tree Root
			/*
			 * Tree Root
			 * 		Class Node
			 * 			Attribute Node (public String SomeAttr1)
			 * 				Filename
			 * 				LineNumber
			 * 			Attribute Node
			 * 			Attribute Node
			 * 			Method Node
			 * 			Method Node
			 * 		Class Node
			 * 			Attribute Node
			 * 			MethodNode
			 */
			//tree = new Tree(parent,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			//			tree1.setHeaderVisible(true);

			ArrayList<UTMDBClass> classList = new ArrayList<UTMDBClass>();
			classList = db.GetSourceClassList();
			for(UTMDBClass utmclass : classList)
			{
				String abstractType = "";
				String finalType = "";
				String staticType = "";
				System.out.print(utmclass);
				if(utmclass.IsAbstract)
					abstractType = "abstract ";
				if(utmclass.IsStatic)
					staticType = "static ";
				if(utmclass.IsFinal)
					finalType = "final ";

				String classNodeText = 
						utmclass.LineNumber + ": " 
								+ utmclass.AccessType + " " 
								+ staticType
								+ abstractType
								+ finalType
								+ utmclass.ClassName + " "
								+ utmclass.NumMismatched;

				// Create Class Node
				TreeItem item = new TreeItem(tree1, SWT.NONE);
				item.setText(new String[] {classNodeText});

				ArrayList<UTMDBAttribute> attributeList = new ArrayList<UTMDBAttribute>();
				attributeList = db.GetSourceAttributesList(utmclass.ClassID);
				for(UTMDBAttribute utmattr : attributeList)
				{
					//					System.out.print(utmattr); 
					String NodeText = 
							utmattr.LineNumber + ": " 
									+ utmattr.AccessType + " " 
									+ utmattr.Name + " " 
									+ utmattr.NumMismatched;

					// Create Attribute Node && Add Attribute Node to Class Node
					TreeItem subItemAttr = new TreeItem(item, SWT.NONE);
					subItemAttr.setText(new String[] {NodeText});
					item.setExpanded(true);

					// Create Filename Node && Add Filename Node to Attribute Node
					TreeItem subsubItemAttrFileName = new TreeItem(subItemAttr, SWT.NONE);
					subsubItemAttrFileName.setText("File Name: " + utmattr.Filename);
					subItemAttr.setExpanded(true);

					//Create LineNum Node && Add LineNum Node to Attribute Node
					TreeItem subsubItemAttrID = new TreeItem(subItemAttr, SWT.NONE);
					subsubItemAttrID.setText("AttributeID: " + String.valueOf(utmattr.AttributeID));
					subItemAttr.setExpanded(true);

					//Create ClassID Node && Add ClassID Node to Attribute Node
					TreeItem subsubItemAttrClassID = new TreeItem(subItemAttr, SWT.NONE);
					subsubItemAttrClassID.setText("ClassID: " + String.valueOf(utmattr.ClassID));
					subItemAttr.setExpanded(true);
				}

				ArrayList<UTMDBMethod> methodList = new ArrayList<UTMDBMethod>();
				db.GetSourceMethodsList(utmclass.ClassID);
				for(UTMDBMethod utmmeth : methodList)
				{
					System.out.print(utmmeth);
					// Create Method Node && Add Method Node to Class Node
					TreeItem subItemMethod = new TreeItem(item, SWT.NONE);
					String MethodNodeText = 
							utmmeth.LineNumber + " " 
									+ utmmeth.AccessType + " " 
									+ utmmeth.Name + " " 
									+ utmmeth.NumMismatched;

					subItemMethod.setText(MethodNodeText);
					item.setExpanded(true);

					// Create Filename Node && Add Filename Node to Attribute Node
					TreeItem subsubItemMethFileName = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethFileName.setText("File Name: "+ utmmeth.Filename);
					subItemMethod.setExpanded(true);

					//Create LineNum Node && Add LineNum Node to Attribute Node
					TreeItem subsubItemMethID = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethID.setText("MethodID: " + String.valueOf(utmmeth.MethodID));
					subItemMethod.setExpanded(true);

					//Create ClassID Node && Add ClassID Node to Attribute Node
					TreeItem subsubItemMethClassID = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethClassID.setText("ClassID: " + String.valueOf(utmmeth.ClassID));
					subItemMethod.setExpanded(true);

					//Create Parameters Node && Add Parameters Node to Attribute Node
					TreeItem subsubItemMethPara = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethPara.setText("Parameters: " + utmmeth.Parameters);
					subItemMethod.setExpanded(true);			
				}
			}

			//tree 2 Parse UML
			ArrayList<UTMDBClass> classList2 = new ArrayList<UTMDBClass>();
			classList2 = db.GetUMLClassList();
			for(UTMDBClass utmclass : classList2)
			{
				String abstractType = "";
				String finalType = "";
				String staticType = "";
				System.out.print(utmclass);
				if(utmclass.IsAbstract)
					abstractType = "abstract ";
				if(utmclass.IsStatic)
					staticType = "static ";
				if(utmclass.IsFinal)
					finalType = "final ";

				String classNodeText = 
						utmclass.LineNumber + ": " 
								+ utmclass.AccessType + " " 
								+ staticType
								+ abstractType
								+ finalType
								+ utmclass.ClassName + " "
								+ utmclass.NumMismatched;

				// Create Class Node
				TreeItem item2 = new TreeItem(tree2, SWT.NONE);
				item2.setText(new String[] {classNodeText});

				ArrayList<UTMDBAttribute> attributeList2 = new ArrayList<UTMDBAttribute>();
				attributeList2 = db.GetUMLAttributesList(utmclass.ClassID);
				for(UTMDBAttribute utmattr : attributeList2)
				{
					//					System.out.print(utmattr); 
					String NodeText = 
							utmattr.LineNumber + ": " 
									+ utmattr.AccessType + " " 
									+ utmattr.Name + " " 
									+ utmattr.NumMismatched;

					// Create Attribute Node && Add Attribute Node to Class Node
					TreeItem subItemAttr = new TreeItem(item2, SWT.NONE);
					subItemAttr.setText(new String[] {NodeText});
					item2.setExpanded(true);

					// Create Filename Node && Add Filename Node to Attribute Node
					TreeItem subsubItemAttrFileName = new TreeItem(subItemAttr, SWT.NONE);
					subsubItemAttrFileName.setText("File Name: " + utmattr.Filename);
					subItemAttr.setExpanded(true);

					//Create LineNum Node && Add LineNum Node to Attribute Node
					TreeItem subsubItemAttrID = new TreeItem(subItemAttr, SWT.NONE);
					subsubItemAttrID.setText("AttributeID: " + String.valueOf(utmattr.AttributeID));
					subItemAttr.setExpanded(true);

					//Create ClassID Node && Add ClassID Node to Attribute Node
					TreeItem subsubItemAttrClassID = new TreeItem(subItemAttr, SWT.NONE);
					subsubItemAttrClassID.setText("ClassID: " + String.valueOf(utmattr.ClassID));
					subItemAttr.setExpanded(true);
				}

				ArrayList<UTMDBMethod> methodList2 = new ArrayList<UTMDBMethod>();
				db.GetUMLMethodsList(utmclass.ClassID);
				for(UTMDBMethod utmmeth : methodList2)
				{
					System.out.print(utmmeth);
					// Create Method Node && Add Method Node to Class Node
					TreeItem subItemMethod = new TreeItem(item2, SWT.NONE);
					String MethodNodeText = 
							utmmeth.LineNumber + " " 
									+ utmmeth.AccessType + " " 
									+ utmmeth.Name + " " 
									+ utmmeth.NumMismatched;

					subItemMethod.setText(MethodNodeText);
					item2.setExpanded(true);

					// Create Filename Node && Add Filename Node to Attribute Node
					TreeItem subsubItemMethFileName = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethFileName.setText("File Name: "+ utmmeth.Filename);
					subItemMethod.setExpanded(true);

					//Create LineNum Node && Add LineNum Node to Attribute Node
					TreeItem subsubItemMethID = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethID.setText("MethodID: " + String.valueOf(utmmeth.MethodID));
					subItemMethod.setExpanded(true);

					//Create ClassID Node && Add ClassID Node to Attribute Node
					TreeItem subsubItemMethClassID = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethClassID.setText("ClassID: " + String.valueOf(utmmeth.ClassID));
					subItemMethod.setExpanded(true);

					//Create Parameters Node && Add Parameters Node to Attribute Node
					TreeItem subsubItemMethPara = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethPara.setText("Parameters: " + utmmeth.Parameters);
					subItemMethod.setExpanded(true);			
				}
			}

			//tree 3

		}

		db.Close();

		// Save output as .txt file for tree 1
		Menu menu = new Menu(tree1);
		tree1.setMenu(menu);

		MenuItem mntmSave = new MenuItem(menu, SWT.NONE);
		mntmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					FileDialog fd = new FileDialog(mntmSave.getParent().getShell(), SWT.SAVE);
					fd.setText("Save");
					fd.setFilterPath(System.getProperty("user.dir"));
					String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
					fd.setFilterExtensions(filterExt);
					String selected = fd.open();
					System.out.println(selected);
					String content = "This is the content to write into file\n";
					System.out.println(" ");
					File file = new File(selected);
					if(!file.exists()){
						file.createNewFile();
					}	
					FileWriter fw = new FileWriter(file.getAbsolutePath());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.newLine();
					bw.write(content);
					int rowCount = tree1.getItemCount(); //rowNum: 2

					for(int row = 0; row <rowCount;row++){
						TreeItem item = tree1.getItem(row);   //row1, row2
						bw.write(item.getText());   //row1, row2
						bw.newLine();
						int subCount = item.getItemCount(); //2  to check if current row has sub rows

						if(subCount >= 1){	  
							for(int subRow = 0; subRow< subCount; subRow++){	

								TreeItem subItem = item.getItem(subRow); //1sub1row1, 1sub1row2, 2sub1row1
								bw.write(" ");
								bw.write(subItem.getText(0));
								bw.newLine();
								int subsubRowCount= subItem.getItemCount();  //2
								if(subsubRowCount >= 1){

									for(int subsubRow = 0; subsubRow< subsubRowCount; subsubRow++){	
										TreeItem subsubItem = subItem.getItem(subsubRow);
										bw.write("  ");
										bw.write(subsubItem.getText(0));
										bw.newLine();
									}
								}
							}
						}
					}
					bw.close();
					fw.close();
					System.out.printf("Done");
					MessageDialog.openConfirm(shell, "Save as Text File", "Data Exported");	
				}catch(Exception ex){
					ex.printStackTrace();
				}				

			}
		});
		mntmSave.setText("save");


		// Save output as .txt file for tree 2
		Menu menu2 = new Menu(tree2);
		tree2.setMenu(menu2);

		MenuItem mntmSave2 = new MenuItem(menu2, SWT.NONE);
		mntmSave2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					FileDialog fd = new FileDialog(mntmSave2.getParent().getShell(), SWT.SAVE);
					fd.setText("Save");
					fd.setFilterPath(System.getProperty("user.dir"));
					String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
					fd.setFilterExtensions(filterExt);
					String selected = fd.open();
					System.out.println(selected);
					String content = "This is the content to write into file\n";
					System.out.println(" ");
					File file = new File(selected);
					if(!file.exists()){
						file.createNewFile();
					}	
					FileWriter fw = new FileWriter(file.getAbsolutePath());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.newLine();
					bw.write(content);
					int rowCount = tree2.getItemCount(); //rowNum: 2

					for(int row = 0; row <rowCount;row++){
						TreeItem item = tree2.getItem(row);   //row1, row2
						bw.write(item.getText());   //row1, row2
						bw.newLine();
						int subCount = item.getItemCount(); //2  to check if current row has sub rows

						if(subCount >= 1){	  
							for(int subRow = 0; subRow< subCount; subRow++){	

								TreeItem subItem = item.getItem(subRow); //1sub1row1, 1sub1row2, 2sub1row1
								bw.write(" ");
								bw.write(subItem.getText(0));
								bw.newLine();
								int subsubRowCount= subItem.getItemCount();  //2
								if(subsubRowCount >= 1){

									for(int subsubRow = 0; subsubRow< subsubRowCount; subsubRow++){	
										TreeItem subsubItem = subItem.getItem(subsubRow);
										bw.write("  ");
										bw.write(subsubItem.getText(0));
										bw.newLine();
									}
								}
							}
						}
					}
					bw.close();
					fw.close();
					System.out.printf("Done");
					MessageDialog.openConfirm(shell, "Save as Text File", "Data Exported");	
				}catch(Exception ex){
					ex.printStackTrace();
				}				

			}
		});
		mntmSave2.setText("save");


		// Save output as .txt file for tree 3
		Menu menu3 = new Menu(tree3);
		tree1.setMenu(menu3);

		MenuItem mntmSave3 = new MenuItem(menu3, SWT.NONE);
		mntmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					FileDialog fd = new FileDialog(mntmSave3.getParent().getShell(), SWT.SAVE);
					fd.setText("Save");
					fd.setFilterPath(System.getProperty("user.dir"));
					String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
					fd.setFilterExtensions(filterExt);
					String selected = fd.open();
					System.out.println(selected);
					String content = "This is the content to write into file\n";
					System.out.println(" ");
					File file = new File(selected);
					if(!file.exists()){
						file.createNewFile();
					}	
					FileWriter fw = new FileWriter(file.getAbsolutePath());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.newLine();
					bw.write(content);
					int rowCount = tree3.getItemCount(); //rowNum: 2

					for(int row = 0; row <rowCount;row++){
						TreeItem item = tree3.getItem(row);   //row1, row2
						bw.write(item.getText());   //row1, row2
						bw.newLine();
						int subCount = item.getItemCount(); //2  to check if current row has sub rows

						if(subCount >= 1){	  
							for(int subRow = 0; subRow< subCount; subRow++){	

								TreeItem subItem = item.getItem(subRow); //1sub1row1, 1sub1row2, 2sub1row1
								bw.write(" ");
								bw.write(subItem.getText(0));
								bw.newLine();
								int subsubRowCount= subItem.getItemCount();  //2
								if(subsubRowCount >= 1){

									for(int subsubRow = 0; subsubRow< subsubRowCount; subsubRow++){	
										TreeItem subsubItem = subItem.getItem(subsubRow);
										bw.write("  ");
										bw.write(subsubItem.getText(0));
										bw.newLine();
									}
								}
							}
						}
					}
					bw.close();
					fw.close();
					System.out.printf("Done");
					MessageDialog.openConfirm(shell, "Save as Text File", "Data Exported");	
				}catch(Exception ex){
					ex.printStackTrace();
				}				

			}
		});
		mntmSave3.setText("save");


	}	

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		container.setFocus();
	}
}
