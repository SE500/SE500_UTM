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
	TreeColumn columnSourceClasses, columnUmlClasses, columnCompare;
	Composite container;
	Shell shell;
	boolean expand = false;
	Listener listener;
	
	/**
	 * Create contents of the output view part. Initialize three trees show the result of
	 * parse UML, parse Source Code, Comparison, respectively. 
	 *  
	 * @param parent 
	 * 		The parent of the control
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		container = new Composite(parent,SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		/*
		 * Create Tree_1 for showing result of Java Source snippet
		 */
		tree1 = new Tree(container,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree1.setHeaderVisible(true);
		tree1.setLinesVisible(true);

		/*
		 * Create Tree_2 for showing result of parsing UML 
		 */
		tree2 = new Tree(container,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree2.setHeaderVisible(true);
		tree2.setLinesVisible(true); 

		/*
		 * Create Tree_3 for showing result of Comparison
		 */
		tree3 = new Tree(container,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree3.setHeaderVisible(true);
		tree3.setLinesVisible(true);

		/*
		 *  initialize Tree 1
		 */
		columnSourceClasses = new TreeColumn(tree1, SWT.CENTER);
		columnSourceClasses.setText("Class/Atrribute/Method Found within Source");
		columnSourceClasses.setWidth(300);

		Menu menu_1 = new Menu(tree1);
		tree1.setMenu(menu_1);
		MenuItem mntmSave_1 = new MenuItem(menu_1, SWT.NONE);
		mntmSave_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveTree(tree1, mntmSave_1.getParent().getShell());
			}
		});
		mntmSave_1.setText("Save as...");

		/*
		 * Initialize Tree 2
		 */
		columnUmlClasses = new TreeColumn(tree2, SWT.CENTER);
		columnUmlClasses.setText("Class/Atrribute/Method Found within UML");
		columnUmlClasses.setWidth(300);

		Menu menu_2 = new Menu(tree2);
		tree2.setMenu(menu_2);
		MenuItem mntmSave_2 = new MenuItem(menu_2, SWT.NONE);
		mntmSave_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveTree(tree2, mntmSave_2.getParent().getShell());				
			}
		});
		mntmSave_2.setText("Save as...");

		/*
		 * Initialize Tree 3
		 */
		columnCompare = new TreeColumn(tree3, SWT.CENTER);
		columnCompare.setText("Compare Result");
		columnCompare.setWidth(400);

		Menu menu_3 = new Menu(tree3);
		tree3.setMenu(menu_3);

		MenuItem mntmSave_3 = new MenuItem(menu_3, SWT.NONE);
		mntmSave_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveTree(tree3, mntmSave_3.getParent().getShell());				
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
		
		columnSourceClasses.pack();
		columnUmlClasses.pack();
		columnCompare.pack();
		
		listener = new Listener() {

	         @Override
	         public void handleEvent( Event e ) {
	            final TreeItem treeItem = (TreeItem)e.item;
	            Display.getDefault().asyncExec(new Runnable() {

	               @Override
	               public void run() {
	                  for ( TreeColumn tc : treeItem.getParent().getColumns() )
	                     tc.pack();
	               }
	            });
	         }
	      };

	      tree1.addListener(SWT.Collapse, listener);
	      tree1.addListener(SWT.Expand, listener);
	      tree2.addListener(SWT.Collapse, listener);
	      tree2.addListener(SWT.Expand, listener);
	      tree3.addListener(SWT.Collapse, listener);
	      tree3.addListener(SWT.Expand, listener);
	      
	}

	/**
	 * This method used in ViewOpenMenu class, and call UTMDB database to get the 
	 * data into the output view, which includes Class info and Attribute info as 
	 * well as Method info.
	 * 
	 */
	public void showResults(){

		UTMDB db = new UTMDB();
		if(!db.Open()){System.err.println("DB.Open Failed!");}
		db.Open();
		db.Relate();
		db.Match();
		db.Commit();

		if(db.IsInitialized())
		{
			/*
			 * Create Tree Root
			 *
			 *
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

			/*
			 * Tree 1 parse source code.
			 */
			ArrayList<UTMDBClass> SourceclassList = new ArrayList<UTMDBClass>();
			SourceclassList = db.GetSourceClassList();
			for(UTMDBClass utmclass : SourceclassList)
			{
				String abstractType = "";
				String finalType = "";
				String staticType = "";

				if(utmclass.IsAbstract)
					abstractType = "abstract ";
				if(utmclass.IsStatic)
					staticType = "static ";
				if(utmclass.IsFinal)
					finalType = "final ";

				String classNodeText1 = 
						utmclass.LineNumber + ": " 
								+ utmclass.AccessType + " " 
								+ staticType
								+ abstractType
								+ finalType
								+ utmclass.ClassName + " "  + (utmclass.NumMismatched > 0 || utmclass.OtherID < 1 ? "**" : "")
								+ " Mismatchs: "
								+ utmclass.NumMismatched + (utmclass.NumMismatched);


				/*
				 *  Create Class Node
				 */
				TreeItem item1 = new TreeItem(tree1, SWT.NONE);
				item1.setText(new String[] {classNodeText1});

				/*
				 * Create Reference Node
				 */
				ArrayList<UTMDBReference> SourceClassReference = new ArrayList<UTMDBReference>();
				SourceClassReference = db.GetSourceReferencesList(utmclass.ClassID);
				for(UTMDBReference ref : SourceClassReference)
				{
					String RefNodeText1 = 
							ref.AccessType + " " 
							+ ref.ReferenceClassName + (ref.OtherID <= 0 ? "*" : "");

					TreeItem subItemRef1 = new TreeItem(item1, SWT.NONE);
					subItemRef1.setText(new String[] {RefNodeText1});
					item1.setExpanded(expand);	

					/*
					 * Create ReferenceID Node && Add ReferenceID Node to Reference Node
					 */
					TreeItem subsubItemRefID1 = new TreeItem(subItemRef1, SWT.NONE);
					subsubItemRefID1.setText("ReferenceID: " + String.valueOf(ref.ReferenceID));
					subItemRef1.setExpanded(expand);

					/*
					 * Create NumMismatched Node && Add NumMismatched Node to Reference Node
					 */
					TreeItem subsubItemRefNumMismatched1 = new TreeItem(subItemRef1, SWT.NONE);
					subsubItemRefNumMismatched1.setText("Number MisMatched: " + String.valueOf(ref.NumMismatched));
					subItemRef1.setExpanded(expand);

				}

				/*
				 *  Create Attribute Node 
				 */
				ArrayList<UTMDBAttribute> attributeList1 = new ArrayList<UTMDBAttribute>();
				attributeList1 = db.GetSourceAttributesList(utmclass.ClassID);
				for(UTMDBAttribute attr : attributeList1)
				{
					UTMDBAttribute otherAttr = db.GetUMLAttribute(attr.OtherID);
					String NodeText1 = attr.AccessType + (otherAttr == null || otherAttr.AccessType.compareTo(attr.AccessType) != 0 ? "*" : "") + " " + 
							attr.Type + (otherAttr == null || otherAttr.Type.compareTo(attr.Type) != 0 ? "*" : "") + " " + 
							attr.Name + (attr.NumMismatched > 0 || attr.OtherID < 1 ? "**" : "");


					TreeItem subItemAttr1 = new TreeItem(item1, SWT.NONE);
					subItemAttr1.setText(new String[] {NodeText1});
					item1.setExpanded(expand);

					/*
					 *  Create Location Node && Add Location Node to Attribute Node
					 */
					TreeItem subsubItemAttrFileName1 = new TreeItem(subItemAttr1, SWT.NONE);
					subsubItemAttrFileName1.setText(" Location: " + attr.Filename + ":" + attr.LineNumber);
					subItemAttr1.setExpanded(expand);

					/*
					 * Create AttributeID Node && Add AttributeID Node to Attribute Node
					 */
					TreeItem subsubItemAttrID1 = new TreeItem(subItemAttr1, SWT.NONE);
					subsubItemAttrID1.setText("AttributeID: " + String.valueOf(attr.AttributeID));
					subItemAttr1.setExpanded(expand);

					/*
					 * Create ClassID Node && Add ClassID Node to Attribute Node
					 */
					TreeItem subsubItemAttrClassID1 = new TreeItem(subItemAttr1, SWT.NONE);
					subsubItemAttrClassID1.setText("Class ID: " + String.valueOf(attr.ClassID));
					subItemAttr1.setExpanded(expand);

					/*
					 * Create ClassName Node && Add ClassName Node to Attribute Node
					 */
					TreeItem subsubItemAttrClassNa1 = new TreeItem(subItemAttr1, SWT.NONE);
					subsubItemAttrClassNa1.setText("Class Name: " + String.valueOf(attr.ClassName));
					subItemAttr1.setExpanded(expand);

					/*
					 * Create OtherID Node && Add OtherID Node to Attribute Node
					 */
					TreeItem subsubItemAttrOtherID1 = new TreeItem(subItemAttr1, SWT.NONE);
					subsubItemAttrOtherID1.setText("Matched UML ClassID: " + String.valueOf(attr.OtherID));
					subItemAttr1.setExpanded(expand);

					/*
					 * Create NumMismatched Node && Add NumMismatched Node to Attribute Node
					 */
					TreeItem subsubItemAttrNumMismatched1 = new TreeItem(subItemAttr1, SWT.NONE);
					subsubItemAttrNumMismatched1.setText("Number MisMatched: " + String.valueOf(attr.NumMismatched));
					subItemAttr1.setExpanded(expand);
				}

				/*
				 *  Create Method Node && Add Method Node to Class Node
				 */
				ArrayList<UTMDBMethod> methodList1 = new ArrayList<UTMDBMethod>();
				methodList1 = db.GetSourceMethodsList(utmclass.ClassID);
				for(UTMDBMethod utmmeth : methodList1)
				{
					UTMDBMethod otherMethod = db.GetUMLMethod(utmmeth.OtherID);
					TreeItem subItemMethod1 = new TreeItem(item1, SWT.NONE);
					String MethodNodeText1 = utmmeth.AccessType + (otherMethod == null || otherMethod.AccessType.compareTo(utmmeth.AccessType) != 0 ? "*" : "") + " " + 
							utmmeth.Type + (otherMethod == null || otherMethod.Type.compareTo(utmmeth.Type) != 0 ? "*" : "") + " " + 
							utmmeth.Name + 
							"(" + 
									utmmeth.Parameters + 
									(otherMethod == null || otherMethod.Parameters.compareTo(utmmeth.Parameters) != 0 ? "*" : "")  + 
							")" + 
							(utmmeth.NumMismatched > 0 || utmmeth.OtherID < 1 ? "**" : "");

					subItemMethod1.setText(MethodNodeText1);
					item1.setExpanded(expand);

					/*
					 *  Create Location Node && Add Location Node to Method Node
					 */
					TreeItem subsubItemMethFileName1 = new TreeItem(subItemMethod1, SWT.NONE);
					subsubItemMethFileName1.setText("Location: "+ utmmeth.Filename+":"+ utmmeth.LineNumber);
					subItemMethod1.setExpanded(expand);

					/*
					 * Create MethodID Node && Add MethodID Node to Method Node
					 */
					TreeItem subsubItemMethID1 = new TreeItem(subItemMethod1, SWT.NONE);
					subsubItemMethID1.setText("MethodID: " + String.valueOf(utmmeth.MethodID));
					subItemMethod1.setExpanded(expand);

					/*
					 * Create ClassID Node && Add ClassID Node to Method Node
					 */
					TreeItem subsubItemMethClassID1 = new TreeItem(subItemMethod1, SWT.NONE);
					subsubItemMethClassID1.setText("ClassID: " + String.valueOf(utmmeth.ClassID));
					subItemMethod1.setExpanded(expand);

					/*
					 * Create ClassName Node && Add ClassName Node to Method Node
					 */
					TreeItem subsubItemClassName = new TreeItem(subItemMethod1, SWT.NONE);
					subsubItemClassName.setText("Class Name: " + utmmeth.ClassName);
					subItemMethod1.setExpanded(expand);		

					/*
					 * Create NumMismatched Node && Add NumMismatched Node to Method Node
					 */
					TreeItem subsubItemAttrNumMismatched1 = new TreeItem(subItemMethod1, SWT.NONE);
					subsubItemAttrNumMismatched1.setText("Number MisMatched: " + String.valueOf(utmmeth.NumMismatched));
					subItemMethod1.setExpanded(expand);
				}
			}

			/*
			 * tree 2 Parse UML
			 */
			ArrayList<UTMDBClass> UMLclassList2 = new ArrayList<UTMDBClass>();
			UMLclassList2 = db.GetUMLClassList();
			for(UTMDBClass utmclass : UMLclassList2)
			{
				String abstractType = "";
				String finalType = "";
				String staticType = "";

				if(utmclass.IsAbstract)
					abstractType = "abstract ";
				if(utmclass.IsStatic)
					staticType = "static ";
				if(utmclass.IsFinal)
					finalType = "final ";

				String classNodeText2 = 
						utmclass.AccessType + " " 
								+ staticType
								+ abstractType
								+ finalType
								+ utmclass.ClassName + " " + (utmclass.NumMismatched > 0 || utmclass.OtherID < 1 ? "**" : "")
								+ " Mismatchs: "
								+ utmclass.NumMismatched;

				/*
				 *  Create Class Node
				 */
				TreeItem item2 = new TreeItem(tree2, SWT.NONE);
				item2.setText(new String[] {classNodeText2});

				/*
				 * Create UML Reference Node
				 */
				ArrayList<UTMDBReference> umlClassReference = new ArrayList<UTMDBReference>();
				umlClassReference = db.GetUMLReferencesList(utmclass.ClassID);
				for(UTMDBReference ref : umlClassReference)
				{
					String RefNodeText2 = 
							ref.AccessType + " " 
							+ ref.ReferenceClassName;

					TreeItem subItemRef2 = new TreeItem(item2, SWT.NONE);
					subItemRef2.setText(new String[] {RefNodeText2});
					item2.setExpanded(expand);

					/*
					 * Create ReferenceID Node && Add ReferenceID Node to Reference Node
					 */
					TreeItem subsubItemRefID2 = new TreeItem(subItemRef2, SWT.NONE);
					subsubItemRefID2.setText("ReferenceID: " + String.valueOf(ref.ReferenceID));
					subItemRef2.setExpanded(expand);

					/*
					 * Create NumMismatched Node && Add NumMismatched Node to Attribute Node
					 */
					TreeItem subsubItemRefNumMismatched1 = new TreeItem(subItemRef2, SWT.NONE);
					subsubItemRefNumMismatched1.setText("Number MisMatched: " + String.valueOf(ref.NumMismatched));
					subItemRef2.setExpanded(expand);

				}

				/*
				 * Create Attribute Node && Add Attribute Node to Class Node
				 */
				ArrayList<UTMDBAttribute> attributeList2 = new ArrayList<UTMDBAttribute>();
				attributeList2 = db.GetUMLAttributesList(utmclass.ClassID);
				for(UTMDBAttribute attr : attributeList2)
				{
					UTMDBAttribute otherAttr = db.GetUMLAttribute(attr.OtherID);
					String NodeText2 = attr.AccessType + (otherAttr == null || otherAttr.AccessType.compareTo(attr.AccessType) != 0 ? "*" : "") + " " + 
							attr.Type + (otherAttr == null || otherAttr.Type.compareTo(attr.Type) != 0 ? "*" : "") + " " + 
							attr.Name + (attr.NumMismatched > 0 || attr.OtherID < 1 ? "**" : "");

					TreeItem subItemAttr2 = new TreeItem(item2, SWT.NONE);
					subItemAttr2.setText(new String[] {NodeText2});
					item2.setExpanded(expand);

					/*
					 *  Create Location Node && Add Location Node to Attribute Node
					 */
					TreeItem subsubItemAttrFileName2 = new TreeItem(subItemAttr2, SWT.NONE);
					subsubItemAttrFileName2.setText("Location: " + attr.Filename + ":" +attr.LineNumber);
					subItemAttr2.setExpanded(expand);

					/*
					 * Create AttributeID Node && Add AttributeID Node to Attribute Node
					 */
					TreeItem subsubItemAttrID2 = new TreeItem(subItemAttr2, SWT.NONE);
					subsubItemAttrID2.setText("AttributeID: " + String.valueOf(attr.AttributeID));
					subItemAttr2.setExpanded(expand);

					/*
					 * Create ClassID Node && Add ClassID Node to Attribute Node
					 */
					TreeItem subsubItemAttrClassID2 = new TreeItem(subItemAttr2, SWT.NONE);
					subsubItemAttrClassID2.setText("ClassID: " + String.valueOf(attr.ClassID));
					subItemAttr2.setExpanded(expand);

					/*
					 * Create ClassName Node && Add ClassName Node to Attribute Node
					 */
					TreeItem subsubItemAttrClassNa2 = new TreeItem(subItemAttr2, SWT.NONE);
					subsubItemAttrClassNa2.setText("Class Name: " + String.valueOf(attr.ClassName));
					subItemAttr2.setExpanded(expand);

					/*
					 * Create OtherID Node && Add OtherID Node to Attribute Node
					 */
					TreeItem subsubItemAttrOtherID2 = new TreeItem(subItemAttr2, SWT.NONE);
					subsubItemAttrOtherID2.setText("Matched Source ClassID: " + String.valueOf(attr.OtherID));
					subItemAttr2.setExpanded(expand);

					/*
					 * Create NumMismatched Node && Add NumMismatched Node to Attribute Node
					 */
					TreeItem subsubItemAttrNumMismatched2 = new TreeItem(subItemAttr2, SWT.NONE);
					subsubItemAttrNumMismatched2.setText("Number MisMatched: " + String.valueOf(attr.NumMismatched));
					subItemAttr2.setExpanded(expand);
				}

				/*
				 *  Create Method Node && Add Method Node to Class Node
				 */
				ArrayList<UTMDBMethod> methodList2 = new ArrayList<UTMDBMethod>();
				methodList2 = db.GetUMLMethodsList(utmclass.ClassID);
				for(UTMDBMethod utmmeth : methodList2)
				{
					UTMDBMethod otherMethod = db.GetUMLMethod(utmmeth.OtherID);
					TreeItem subItemMethod2 = new TreeItem(item2, SWT.NONE);
					String MethodNodeText2 = utmmeth.AccessType + (otherMethod == null || otherMethod.AccessType.compareTo(utmmeth.AccessType) != 0 ? "*" : "") + " " + 
							utmmeth.Type + (otherMethod == null || otherMethod.Type.compareTo(utmmeth.Type) != 0 ? "*" : "") + " " + 
							utmmeth.Name + 
							"(" + 
									utmmeth.Parameters + 
									(otherMethod == null || otherMethod.Parameters.compareTo(utmmeth.Parameters) != 0 ? "*" : "")  + 
							")" + 
							(utmmeth.NumMismatched > 0 || utmmeth.OtherID < 1 ? "**" : "");

					subItemMethod2.setText(MethodNodeText2);
					item2.setExpanded(expand);

					/*
					 *  Create Location Node && Add Location Node to Method Node
					 */
					TreeItem subsubItemMethFileName2 = new TreeItem(subItemMethod2, SWT.NONE);
					subsubItemMethFileName2.setText("Location: "+ utmmeth.Filename+":"+ utmmeth.LineNumber);
					subItemMethod2.setExpanded(expand);

					/*
					 * Create MethodID Node && Add MethodID Node to Method Node
					 */
					TreeItem subsubItemMethID2 = new TreeItem(subItemMethod2, SWT.NONE);
					subsubItemMethID2.setText("MethodID: " + String.valueOf(utmmeth.MethodID));
					subItemMethod2.setExpanded(expand);

					/*
					 * Create ClassID Node && Add ClassID Node to Method Node
					 */
					TreeItem subsubItemMethClassID2 = new TreeItem(subItemMethod2, SWT.NONE);
					subsubItemMethClassID2.setText("ClassID: " + String.valueOf(utmmeth.ClassID));
					subItemMethod2.setExpanded(expand);

					/*
					 * Create ClassName Node && Add ClassName Node to Method Node
					 */
					TreeItem subsubItemMethPara2 = new TreeItem(subItemMethod2, SWT.NONE);
					subsubItemMethPara2.setText("Class Name: " + utmmeth.ClassName);
					subItemMethod2.setExpanded(expand);		

					/*
					 * Create NumMismatched Node && Add NumMismatched Node to Method Node
					 */
					TreeItem subsubItemAttrNumMismatched2 = new TreeItem(subItemMethod2, SWT.NONE);
					subsubItemAttrNumMismatched2.setText("Number MisMatched: " + String.valueOf(utmmeth.NumMismatched));
					subItemMethod2.setExpanded(expand);
				}
			}

			/*
			 * tree 3 Compare results
			 */
//			TreeItem item3 = new TreeItem(tree3,SWT.NONE);
//			item3.setText("Percentage Of Completion");
			Tree item3 = tree3;

			/*
			 * Create Source Code Percentage of Completion Node
			 */
			TreeItem subitem3_1 = new TreeItem(item3,SWT.NONE);
			subitem3_1.setText("Number of Classes in Source: " + String.valueOf(db.CountSourceClasses()));

			/*
			 *  give result of total numMismatched of Class, Attribute, Method in UML and Source && give result of percentage of completion 
			 */

			float totalSourceNumTotal = 0;
			float totalSourceNumMismatched = 0;

			ArrayList<UTMDBClass> SourceClassList = db.GetSourceClassList();
			for(UTMDBClass curClass : SourceClassList)
			{
				float numTotal = 1;
				float numMismatched = (curClass.NumMismatched > 0 || curClass.OtherID < 1 ? 1 : 0);

				ArrayList<UTMDBReference> SourceClassReference = db.GetSourceReferencesList(curClass.ClassID);
				ArrayList<UTMDBAttribute> SourceAttributeList = db.GetSourceAttributesList(curClass.ClassID);
				ArrayList<UTMDBMethod> SourceMethodList = db.GetSourceMethodsList(curClass.ClassID);

				for(UTMDBReference ref : SourceClassReference)
				{	
					numTotal++;
					if(ref.OtherID <= 0)
					{
						numMismatched++;
					}
				}

				for(UTMDBAttribute attr : SourceAttributeList)
				{
					numTotal++;
					if(attr.NumMismatched > 0 || attr.OtherID < 1)
					{
						numMismatched++;
					}
				}

				for(UTMDBMethod method : SourceMethodList)
				{
					numTotal++;
					if(method.NumMismatched > 0 || method.OtherID < 1)
					{
						numMismatched++;
					}
				}

				System.out.println(curClass.ClassName + " " 
						+ Math.round(numMismatched) 
						+ " Mismatched of " 
						+ Math.round(numTotal) 
						+ " Elements (" + Math.round(((numTotal - numMismatched) / numTotal) * 100) 
						+ "% Matched)");
				/*
				 * Add results to tree3
				 */
				TreeItem subitem3_1_1 = new TreeItem(subitem3_1,SWT.NONE);
				subitem3_1_1.setText(curClass.ClassName + " " 
						+ Math.round(numMismatched) 
						+ " Mismatched of " 
						+ Math.round(numTotal) 
						+ " Elements (" + Math.round(((numTotal - numMismatched) / numTotal) * 100) 
						+ "% Matched)");


				totalSourceNumTotal += numTotal;
				totalSourceNumMismatched += numMismatched;
			}

			System.out.println("Source: " + Math.round(totalSourceNumMismatched) 
			+ " Mismatched of " 
			+ Math.round(totalSourceNumTotal) 
			+ " Elements (" + Math.round(((totalSourceNumTotal - totalSourceNumMismatched) / totalSourceNumTotal) * 100) 
			+ "% Matched)");

			TreeItem subitem3_2 = new TreeItem(item3,SWT.NONE);
			subitem3_2.setText("Source: " + Math.round(totalSourceNumMismatched) 
			+ " Mismatched of " 
			+ Math.round(totalSourceNumTotal) 
			+ " Elements (" + Math.round(((totalSourceNumTotal - totalSourceNumMismatched) / totalSourceNumTotal) * 100) 
			+ "% Matched)");

			/*
			 * Create UML Percentage of Completion Node
			 */
			TreeItem subitem3_3 = new TreeItem(item3,SWT.NONE);
			subitem3_3.setText("Number of Classes in UML: " + String.valueOf(db.CountUMLClasses()));

			/*
			 *  List All UML Elements
			 */
			float totalUMLNumTotal = 0;
			float totalUMLNumMismatched = 0;

			ArrayList<UTMDBClass> UMLClassList = db.GetUMLClassList();
			for(UTMDBClass curClass : UMLClassList)
			{
				float numTotal = 1;
				float numMismatched = (curClass.NumMismatched > 0 || curClass.OtherID < 1 ? 1 : 0);

				ArrayList<UTMDBReference> UMLClassReference = db.GetUMLReferencesList(curClass.ClassID);
				ArrayList<UTMDBAttribute> UMLAttributeList = db.GetUMLAttributesList(curClass.ClassID);
				ArrayList<UTMDBMethod> UMLMethodList = db.GetUMLMethodsList(curClass.ClassID);

				for(UTMDBReference ref : UMLClassReference)
				{

					numTotal++;
					if(ref.NumMismatched > 0)
					{
						numMismatched++;
					}
				}

				for(UTMDBAttribute attr : UMLAttributeList)
				{

					numTotal++;
					if(attr.NumMismatched > 0 || attr.OtherID < 1)
					{
						numMismatched++;
					}
				}

				for(UTMDBMethod method : UMLMethodList)
				{
					// Count Mismatches
					numTotal++;
					if(method.NumMismatched > 0 || method.OtherID < 1)
					{
						numMismatched++;
					}
				}

				System.out.println(curClass.ClassName + " " 
						+ Math.round(numMismatched) 
						+ " Mismatched of " 
						+ Math.round(numTotal) 
						+ " Elements (" + Math.round(((numTotal - numMismatched) / numTotal) * 100) + "% Matched)");

				totalUMLNumTotal += numTotal;
				totalUMLNumMismatched += numMismatched;

				TreeItem subitem3_3_1 = new TreeItem(subitem3_3,SWT.NONE);
				subitem3_3_1.setText(curClass.ClassName + " " 
						+ Math.round(numMismatched) 
						+ " Mismatched of " 
						+ Math.round(numTotal) 
						+ " Elements (" + Math.round(((numTotal - numMismatched) / numTotal) * 100) + "% Matched)");
			}

			System.out.println("UML: " + Math.round(totalUMLNumMismatched) 
			+ " Mismatched of " 
			+ Math.round(totalUMLNumTotal) 
			+ " Elements (" + Math.round(((totalUMLNumTotal - totalUMLNumMismatched) / totalUMLNumTotal) * 100) 
			+ "% Matched)");

			TreeItem subitem3_4 = new TreeItem(item3,SWT.NONE);
			subitem3_4.setText("UML: " + Math.round(totalUMLNumMismatched) 
			+ " Mismatched of " 
			+ Math.round(totalUMLNumTotal) 
			+ " Elements (" + Math.round(((totalUMLNumTotal - totalUMLNumMismatched) / totalUMLNumTotal) * 100) 
			+ "% Matched)");
			
			columnSourceClasses.pack();
			columnUmlClasses.pack();
			columnCompare.pack();

		}
		
		db.Close();

	}
	
	/**
	 * The method saves the passed tree as a text file
	 * @param tree
	 * 		The tree to from the results display to save
	 * @param shell
	 * 		The parent shell
	 */
	private void saveTree(Tree tree, Shell shell){
		try{
			FileDialog fd = new FileDialog(shell, SWT.SAVE);
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
			int rowCount = tree.getItemCount(); //rowNum: 2

			for(int row = 0; row <rowCount;row++){
				TreeItem item = tree.getItem(row);   //row1, row2
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

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		container.setFocus();
	}
}
