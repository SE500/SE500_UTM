package org.eclipse.utm.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
////////
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
///////
public class testview extends ViewPart {

	public static final String ID = "com.eclipse.utm.views.testview"; //$NON-NLS-1$

	public testview() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(null);
////////////
				Display display = new Display();
				final Shell shell = new Shell(display);
				shell.setLayout(new FillLayout());
				Tree tree = new Tree(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
				tree.setHeaderVisible(true);
				TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
				column1.setText("Column 1");
				column1.setWidth(200);
				TreeColumn column2 = new TreeColumn(tree, SWT.CENTER);
				column2.setText("Column 2");
				column2.setWidth(200);
				TreeColumn column3 = new TreeColumn(tree, SWT.RIGHT);
				column3.setText("Column 3");
				column3.setWidth(200);
				for (int i = 0; i < 4; i++) {
					TreeItem item = new TreeItem(tree, SWT.NONE);
					item.setText(new String[] { "item " + i, "abc", "defghi" });
					for (int j = 0; j < 4; j++) {
						TreeItem subItem = new TreeItem(item, SWT.NONE);
						subItem.setText(new String[] { "subitem " + j, "jklmnop", "qrs" });
						for (int k = 0; k < 4; k++) {
							TreeItem subsubItem = new TreeItem(subItem, SWT.NONE);
							subsubItem.setText(new String[] { "subsubitem " + k, "tuv", "wxyz" });
						}
					}
				}
				shell.pack();
				shell.open();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
				display.dispose();
			
		
		/////////////
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

}
