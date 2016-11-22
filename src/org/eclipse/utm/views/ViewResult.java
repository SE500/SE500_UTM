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

	Tree tree;
	Composite parent;
	Shell shell;
	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		 tree = new Tree(parent,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		 tree.setHeaderVisible(true);
		 tree.setLinesVisible(true);
		 
		 TreeColumn columnClass = new TreeColumn(tree, SWT.CENTER);
		    columnClass.setText("          Class/Atrribute/Method Found");
		    columnClass.setWidth(400);
		    
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
			tree.setHeaderVisible(true);
			
			ArrayList<UTMDBClass> classList = new ArrayList<UTMDBClass>();
			classList = db.GetSourceClassList();
			for(UTMDBClass utmclass : classList)
			{
				System.out.print(utmclass);
				String classNodeText = utmclass.ClassName + " " + utmclass.AccessType + " " + utmclass.LineNumber + " " 
				+ utmclass.NumMismatched + " " + utmclass.IsAbstract + " " + utmclass.IsStatic + " " + utmclass.IsFinal;
				
				// Create Class Node
				TreeItem item = new TreeItem(tree, SWT.NONE);
				item.setText(new String[] {classNodeText});
				
				ArrayList<UTMDBAttribute> attributeList = new ArrayList<UTMDBAttribute>();
				attributeList = db.GetSourceAttributesList(utmclass.ClassID);
				for(UTMDBAttribute utmattr : attributeList)
				{
					System.out.print(utmattr);
					String NodeText = utmattr.Name + " " + utmattr.Type + " " + utmattr.AccessType + " " + utmattr.NumMismatched;
					
					// Create Attribute Node && Add Attribute Node to Class Node
					TreeItem subItemAttr = new TreeItem(item, SWT.NONE);
					subItemAttr.setText(new String[] {NodeText});
					item.setExpanded(true);
					
					// Create Filename Node && Add Filename Node to Attribute Node
					TreeItem subsubItemAttrFileName = new TreeItem(subItemAttr, SWT.NONE);
					subsubItemAttrFileName.setText(utmattr.Filename);
					subItemAttr.setExpanded(true);
					
					//Create LineNum Node && Add LineNum Node to Attribute Node
					TreeItem subsubItemAttrLineNum = new TreeItem(subItemAttr, SWT.NONE);
					subsubItemAttrLineNum.setText(String.valueOf(utmattr.LineNumber));
					subItemAttr.setExpanded(true);
					
					//Create ClassID Node && Add ClassID Node to Attribute Node
					TreeItem subsubItemAttrClassID = new TreeItem(subItemAttr, SWT.NONE);
					subsubItemAttrClassID.setText(String.valueOf(utmattr.ClassID));
					subItemAttr.setExpanded(true);
				}
				
				ArrayList<UTMDBMethod> methodList = new ArrayList<UTMDBMethod>();
				db.GetSourceMethodsList(utmclass.ClassID);
				for(UTMDBMethod utmmeth : methodList)
				{
					System.out.print(utmmeth);
					// Create Method Node && Add Method Node to Class Node
					TreeItem subItemMethod = new TreeItem(item, SWT.NONE);
					String MethodNodeText = utmmeth.Name + " " + utmmeth.Type + " " + utmmeth.AccessType + " " + utmmeth.NumMismatched;
					subItemMethod.setText(MethodNodeText);
					item.setExpanded(true);
					
					// Create Filename Node && Add Filename Node to Attribute Node
					TreeItem subsubItemMethFileName = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethFileName.setText(utmmeth.Filename);
					subItemMethod.setExpanded(true);
					
					//Create LineNum Node && Add LineNum Node to Attribute Node
					TreeItem subsubItemMethLineNum = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethLineNum.setText(String.valueOf(utmmeth.LineNumber));
					subItemMethod.setExpanded(true);
					
					//Create ClassID Node && Add ClassID Node to Attribute Node
					TreeItem subsubItemMethClassID = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethClassID.setText(String.valueOf(utmmeth.ClassID));
					subItemMethod.setExpanded(true);
					
					//Create Parameters Node && Add Parameters Node to Attribute Node
					TreeItem subsubItemMethPara = new TreeItem(subItemMethod, SWT.NONE);
					subsubItemMethPara.setText(utmmeth.Parameters);
					subItemMethod.setExpanded(true);			
				}
			}
		}
		
		db.Close();
		
		// Save output as .txt file
		Menu menu = new Menu(tree);
		tree.setMenu(menu);
		
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
		});
		mntmSave.setText("save");
		
		
		}	

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		parent.setFocus();
	}
}
