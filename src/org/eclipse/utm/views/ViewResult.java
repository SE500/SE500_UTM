package org.eclipse.utm.views;

import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.*;
import org.eclipse.utm.compare.*;
import org.eclipse.swt.layout.FillLayout;

import java.io.*;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
/**
 * The output view part of UML Trace Magic plug-in application that displays the result
 * of parsing UML and parsing Source Code in a tree view.
 * 
 * 
 * @version Neon.1a Release (4.6.1)
 * @author junqianfeng, Thomas Colborne
 * @since 2016-09
 *
 */

public class ViewResult extends ViewPart {
	public ViewResult() {}
	public static final String ID = "org.eclipse.utm.views.ViewResult"; 

	Tree tree1, tree2, tree3;
	Composite container;
	Shell shell;
	/**
	 * Create contents of the output view part. Initialize three trees show the result of
	 * parse UML, parse Source Code, Comparison, respectively. 
	 *  
	 * @param container, parent
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

		// initialize Tree 1
		TreeColumn columnSourceClasses = new TreeColumn(tree1, SWT.CENTER);
		columnSourceClasses.setText("Class/Atrribute/Method Found within Source");
		columnSourceClasses.setWidth(300);

		Menu menu_1 = new Menu(tree1);
		tree1.setMenu(menu_1);
		MenuItem mntmSave_1 = new MenuItem(menu_1, SWT.NONE);
		mntmSave_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					FileDialog fd = new FileDialog(mntmSave_1.getParent().getShell(), SWT.SAVE);
					fd.setText("Save as...");
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
		mntmSave_1.setText("Save as...");

		//Initialize Tree 2
		TreeColumn columnUmlClasses = new TreeColumn(tree2, SWT.CENTER);
		columnUmlClasses.setText("Class/Atrribute/Method Found within UML");
		columnUmlClasses.setWidth(300);

		Menu menu_2 = new Menu(tree2);
		tree2.setMenu(menu_2);
		MenuItem mntmSave_2 = new MenuItem(menu_2, SWT.NONE);
		mntmSave_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					FileDialog fd = new FileDialog(mntmSave_2.getParent().getShell(), SWT.SAVE);
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
		mntmSave_2.setText("Save as...");

		//Initialize Tree 3
		TreeColumn columnCompare = new TreeColumn(tree3, SWT.CENTER);
		columnCompare.setText("Compare Result");
		columnCompare.setWidth(300);

		Menu menu_3 = new Menu(tree3);
		tree3.setMenu(menu_3);

		MenuItem mntmSave_3 = new MenuItem(menu_3, SWT.NONE);
		mntmSave_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					FileDialog fd = new FileDialog(mntmSave_3.getParent().getShell(), SWT.SAVE);
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
		mntmSave_3.setText("Save as...");

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

	/**
	 * this method used in ViewOpenMenu class, and call UTMDB database to get the 
	 * data into the output view, which includes Class info and Attrubute info as 
	 * well as Method info.
	 * 
	 */
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

			//Tree 1 parse source code.
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

				String classNodeText1 = 
						utmclass.LineNumber + ": " 
								+ utmclass.ClassName + " "
								+ utmclass.AccessType + " " 
								+ staticType
								+ abstractType
								+ finalType
								+ utmclass.NumMismatched 
								+ "(NumMismatched)";
				
				
				// Create Class Node
				TreeItem item1 = new TreeItem(tree1, SWT.NONE);
				item1.setText(new String[] {classNodeText1});

				ArrayList<UTMDBAttribute> attributeList1 = new ArrayList<UTMDBAttribute>();
				attributeList1 = db.GetSourceAttributesList(utmclass.ClassID);
				for(UTMDBAttribute utmattr : attributeList1)
				{
					//					System.out.print(utmattr); 
					String NodeText1 = 
							utmattr.LineNumber + ": " 
									+ utmattr.AccessType + " " 
									+ utmattr.Name + " " 
									+ utmattr.Type;

					// Create Attribute Node && Add Attribute Node to Class Node
					TreeItem subItemAttr1 = new TreeItem(item1, SWT.NONE);
					subItemAttr1.setText(new String[] {NodeText1});
					item1.setExpanded(true);

					// Create Filename Node && Add Filename Node to Attribute Node
					TreeItem subsubItemAttrFileName1 = new TreeItem(subItemAttr1, SWT.NONE);
					subsubItemAttrFileName1.setText("File Name: " + utmattr.Filename);
					subItemAttr1.setExpanded(true);

					//Create LineNum Node && Add LineNum Node to Attribute Node
					TreeItem subsubItemAttrID1 = new TreeItem(subItemAttr1, SWT.NONE);
					subsubItemAttrID1.setText("AttributeID: " + String.valueOf(utmattr.AttributeID));
					subItemAttr1.setExpanded(true);

					//Create ClassID Node && Add ClassID Node to Attribute Node
					TreeItem subsubItemAttrClassID1 = new TreeItem(subItemAttr1, SWT.NONE);
					subsubItemAttrClassID1.setText("Class Name: " + String.valueOf(utmattr.ClassName));
					subItemAttr1.setExpanded(true);

					//Create NumMismatched Node && Add NumMismatched Node to Attribute Node
					TreeItem subsubItemAttrNumMismatched1 = new TreeItem(subItemAttr1, SWT.NONE);
					subsubItemAttrNumMismatched1.setText("Number MisMatched: " + String.valueOf(utmattr.NumMismatched));
					subItemAttr1.setExpanded(true);
				}

				ArrayList<UTMDBMethod> methodList1 = new ArrayList<UTMDBMethod>();
				db.GetSourceMethodsList(utmclass.ClassID);
				for(UTMDBMethod utmmeth : methodList1)
				{
					//					System.out.print(utmmeth);
					// Create Method Node && Add Method Node to Class Node
					TreeItem subItemMethod1 = new TreeItem(item1, SWT.NONE);
					String MethodNodeText1 = 
							utmmeth.LineNumber + " " 
									+ utmmeth.AccessType + " " 
									+ utmmeth.Name + " " 
									+ utmmeth.Type;

					subItemMethod1.setText(MethodNodeText1);
					item1.setExpanded(true);

					// Create Filename Node && Add Filename Node to Method Node
					TreeItem subsubItemMethFileName1 = new TreeItem(subItemMethod1, SWT.NONE);
					subsubItemMethFileName1.setText("File Name: "+ utmmeth.Filename);
					subItemMethod1.setExpanded(true);

					//Create LineNum Node && Add LineNum Node to Method Node
					TreeItem subsubItemMethID1 = new TreeItem(subItemMethod1, SWT.NONE);
					subsubItemMethID1.setText("MethodID: " + String.valueOf(utmmeth.MethodID));
					subItemMethod1.setExpanded(true);

					//Create ClassID Node && Add ClassID Node to Method Node
					TreeItem subsubItemMethClassID1 = new TreeItem(subItemMethod1, SWT.NONE);
					subsubItemMethClassID1.setText("ClassID: " + String.valueOf(utmmeth.ClassID));
					subItemMethod1.setExpanded(true);

					//Create Parameters Node && Add Parameters Node to Method Node
					TreeItem subsubItemMethPara1 = new TreeItem(subItemMethod1, SWT.NONE);
					subsubItemMethPara1.setText("Parameters: " + utmmeth.Parameters);
					subItemMethod1.setExpanded(true);		

					//Create NumMismatched Node && Add NumMismatched Node to Method Node
					TreeItem subsubItemAttrNumMismatched1 = new TreeItem(subItemMethod1, SWT.NONE);
					subsubItemAttrNumMismatched1.setText("Number MisMatched: " + String.valueOf(utmmeth.NumMismatched));
					subItemMethod1.setExpanded(true);
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

				String classNodeText2 = 
						utmclass.LineNumber + ": " 
								+ utmclass.AccessType + " " 
								+ staticType
								+ abstractType
								+ finalType
								+ utmclass.ClassName + " "
								+ utmclass.NumMismatched
								+ "(NumMismatched)";

				// Create Class Node
				TreeItem item2 = new TreeItem(tree2, SWT.NONE);
				item2.setText(new String[] {classNodeText2});

				ArrayList<UTMDBAttribute> attributeList2 = new ArrayList<UTMDBAttribute>();
				attributeList2 = db.GetUMLAttributesList(utmclass.ClassID);
				for(UTMDBAttribute utmattr : attributeList2)
				{
					//					System.out.print(utmattr); 
					String NodeText2 = 
							utmattr.LineNumber + ": " 
									+ utmattr.AccessType + " " 
									+ utmattr.Name + " " 
									+ utmattr.Type;

					// Create Attribute Node && Add Attribute Node to Class Node
					TreeItem subItemAttr2 = new TreeItem(item2, SWT.NONE);
					subItemAttr2.setText(new String[] {NodeText2});
					item2.setExpanded(true);

					// Create Filename Node && Add Filename Node to Attribute Node
					TreeItem subsubItemAttrFileName2 = new TreeItem(subItemAttr2, SWT.NONE);
					subsubItemAttrFileName2.setText("File Name: " + utmattr.Filename);
					subItemAttr2.setExpanded(true);

					//Create LineNum Node && Add LineNum Node to Attribute Node
					TreeItem subsubItemAttrID2 = new TreeItem(subItemAttr2, SWT.NONE);
					subsubItemAttrID2.setText("AttributeID: " + String.valueOf(utmattr.AttributeID));
					subItemAttr2.setExpanded(true);

					//Create ClassID Node && Add ClassID Node to Attribute Node
					TreeItem subsubItemAttrClassID2 = new TreeItem(subItemAttr2, SWT.NONE);
					subsubItemAttrClassID2.setText("ClassID: " + String.valueOf(utmattr.ClassID));
					subItemAttr2.setExpanded(true);

					//Create NumMismatched Node && Add NumMismatched Node to Attribute Node
					TreeItem subsubItemAttrNumMismatched2 = new TreeItem(subItemAttr2, SWT.NONE);
					subsubItemAttrNumMismatched2.setText("Number MisMatched: " + String.valueOf(utmattr.NumMismatched));
					subItemAttr2.setExpanded(true);
				}

				ArrayList<UTMDBMethod> methodList2 = new ArrayList<UTMDBMethod>();
				db.GetUMLMethodsList(utmclass.ClassID);
				for(UTMDBMethod utmmeth : methodList2)
				{
					//					System.out.print(utmmeth);
					// Create Method Node && Add Method Node to Class Node
					TreeItem subItemMethod2 = new TreeItem(item2, SWT.NONE);
					String MethodNodeText2 = 
							utmmeth.LineNumber + " " 
									+ utmmeth.AccessType + " " 
									+ utmmeth.Name + " " 
									+ utmmeth.Type;

					subItemMethod2.setText(MethodNodeText2);
					item2.setExpanded(true);

					// Create Filename Node && Add Filename Node to Attribute Node
					TreeItem subsubItemMethFileName2 = new TreeItem(subItemMethod2, SWT.NONE);
					subsubItemMethFileName2.setText("File Name: "+ utmmeth.Filename);
					subItemMethod2.setExpanded(true);

					//Create LineNum Node && Add LineNum Node to Attribute Node
					TreeItem subsubItemMethID2 = new TreeItem(subItemMethod2, SWT.NONE);
					subsubItemMethID2.setText("MethodID: " + String.valueOf(utmmeth.MethodID));
					subItemMethod2.setExpanded(true);

					//Create ClassID Node && Add ClassID Node to Attribute Node
					TreeItem subsubItemMethClassID2 = new TreeItem(subItemMethod2, SWT.NONE);
					subsubItemMethClassID2.setText("ClassID: " + String.valueOf(utmmeth.ClassID));
					subItemMethod2.setExpanded(true);

					//Create Parameters Node && Add Parameters Node to Attribute Node
					TreeItem subsubItemMethPara2 = new TreeItem(subItemMethod2, SWT.NONE);
					subsubItemMethPara2.setText("Parameters: " + utmmeth.Parameters);
					subItemMethod2.setExpanded(true);		

					//Create NumMismatched Node && Add NumMismatched Node to Method Node
					TreeItem subsubItemAttrNumMismatched2 = new TreeItem(subItemMethod2, SWT.NONE);
					subsubItemAttrNumMismatched2.setText("Number MisMatched: " + String.valueOf(utmmeth.NumMismatched));
					subItemMethod2.setExpanded(true);
				}
			}

			//tree 3 compare results
			
			TreeItem item3_1 = new TreeItem(tree3,SWT.NONE);
			item3_1.setText("Class Count");
			TreeItem subitem3_1_1 = new TreeItem(item3_1,SWT.NONE);
			subitem3_1_1.setText("UML Class Count: "+ String.valueOf(db.CountUMLClasses()));
			TreeItem subitem3_1_2 = new TreeItem(item3_1,SWT.NONE);
			subitem3_1_2.setText("Source Class Count: "+ String.valueOf(db.CountSourceClasses()));
			
			TreeItem item3_2 = new TreeItem(tree3,SWT.NONE);
			item3_2.setText("Attribute Count");
			TreeItem subitem3_2_1 = new TreeItem(item3_2,SWT.NONE);
			subitem3_2_1.setText("UML Attribute Count: " + String.valueOf(db.CountUMLAttributes()));
			TreeItem subitem3_2_2 = new TreeItem(item3_2,SWT.NONE);
			subitem3_2_2.setText("Source Attribute Count: " + String.valueOf(db.CountSourceAttributes()));
			
			TreeItem item3_3 = new TreeItem(tree3,SWT.NONE);
			item3_3.setText("Method Count");
			TreeItem subitem3_3_1 = new TreeItem(item3_3,SWT.NONE);
			subitem3_3_1.setText("UML Method Count: "+ String.valueOf(db.CountUMLMethods()));
			TreeItem subitem3_3_2 = new TreeItem(item3_3,SWT.NONE);
			subitem3_3_2.setText("Source Method Count: "+ String.valueOf(db.CountSourceMethods()));
			
			// give result of total numMismatched of Class, Attribute, Method in UML and Source
				
			// give result of percentage of completion 
			
			// give result of comparison of misMatched classes or attributes or methods.
			
			

		}

		db.Close();

	}	

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		container.setFocus();
	}
}
