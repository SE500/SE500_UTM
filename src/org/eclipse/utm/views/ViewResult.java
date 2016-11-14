package org.eclipse.utm.views;
/**
 * @author junqianfeng
 */

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.*;

public class ViewResult extends ViewPart {

	public static final String ID = "org.eclipse.utm.views.ViewResult"; 

	Tree tree;
	Composite parent;
	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		 parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		 Tree tree = new Tree(parent,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		 tree.setHeaderVisible(true);
		 TreeColumn columnClass = new TreeColumn(tree, SWT.CENTER);
		    columnClass.setText("                 Column 1");
		    columnClass.setWidth(200);
		    TreeColumn column2 = new TreeColumn(tree, SWT.CENTER);
		    column2.setText("Column 2");
		    column2.setWidth(200);
		    TreeColumn columnLocation = new TreeColumn(tree, SWT.CENTER);
		    columnLocation.setText("Column 3");
		    columnLocation.setWidth(190);
    //////////////////////////Test case/////////////////////////////   
		    TreeItem item1 = new TreeItem(tree, SWT.NONE);
		    item1.setText(new String[]{"               item1","yes","32"});
		    TreeItem subItem1 = new TreeItem(item1, SWT.NONE);
		    subItem1.setText(new String[]{"               subItem1","yes","55"});
		    TreeItem subsubItem1 = new TreeItem(subItem1, SWT.NONE);
		    subsubItem1.setText(new String[]{"               subsubitem1","No","88"});
		    TreeItem item2 = new TreeItem(tree, SWT.NONE);
		    item2.setText(new String[]{"               item2","yes","62"});  
		    TreeItem subItem2 = new TreeItem(item2, SWT.NONE);
		    subItem2.setText(new String[]{"               subItem2","no","99"});
	////////////////////////////////////////////////////////////////////// 
	}
	
	public void showResults(){
		tree.removeAll();
//		for(CompareResults thisResult : Compare.results){
//			String matchFound, attrMatches, matchLocation;
//			if(thisResult.matchFound) matchFound = "Yes";
//			else matchFound = "No";
//		TreeItem item = new TreeItem(tree, SWT.NONE);
//		item.setText(new String[]{"            "+thisResult.className, matchFound,matchLocation});
//		TreeItem subitem = new TreeItem(item, SWT.NONE);
//		subitem.setText(new String[]{"            "+thisResult.methodName, matchFound,matchLocation});
		}
		

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		parent.setFocus();
	}
}
