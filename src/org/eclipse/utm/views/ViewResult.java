package org.eclipse.utm.views;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.*;
import org.eclipse.utm.views.ViewResult.TreeObject;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.*;
import org.eclipse.core.runtime.IAdaptable;

public class ViewResult extends ViewPart {
	public ViewResult() {
	}

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.utm.views.ViewResult";


	Composite mainPanel;
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
		 
	class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;
		
		public TreeObject(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setParent(TreeParent parent) {
			this.parent = parent;
		}
		public TreeParent getParent() {
			return parent;
		}
		public String toString() {
			return getName();
		}
		public <T> T getAdapter(Class<T> key) {
			return null;
		}
	}
	
	class TreeParent extends TreeObject {
		private ArrayList<TreeObject> children;
		public TreeParent(String name) {
			super(name);
			children = new ArrayList<TreeObject>();
		}
		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}
		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}
		public TreeObject [] getChildren() {
			return (TreeObject [])children.toArray(new TreeObject[children.size()]);
		}
		public boolean hasChildren() {
			return children.size()>0;
		}
	}

	class ViewContentProvider implements ITreeContentProvider {
		private TreeParent invisibleRoot;

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot==null) initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}
		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject)child).getParent();
			}
			return null;
		}
		public Object [] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent)parent).getChildren();
			}
			return new Object[0];
		}
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent)parent).hasChildren();
			return false;
		}

		private void initialize() {
			invisibleRoot = new TreeParent("Results:");
		}
	}

	class ViewLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			return obj.toString();
		}
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof TreeParent)
			   imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}


	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(this.mainPanel, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new ViewLabelProvider());
		
		TreeObject to1 = new TreeObject("Method 1");
		TreeObject to2 = new TreeObject("Method 2");
		TreeObject to3 = new TreeObject("Method 3");
		TreeParent p1 = new TreeParent("Class 1");
		p1.addChild(to1);
		p1.addChild(to2);
		p1.addChild(to3);
		
		TreeObject to4 = new TreeObject("Method 4");
		TreeObject to5 = new TreeObject("Method 5");
		TreeParent p2 = new TreeParent("Class 2");
		p2.addChild(to4);
		p2.addChild(to5);
		
		TreeObject to6 = new TreeObject("Method 6");
		TreeObject to7 = new TreeObject("Method 7");
		TreeParent p3 = new TreeParent("Class 3");
		p3.addChild(to7);
		p3.addChild(to6);
		
		
		TreeParent root = new TreeParent("Results:");
		root.addChild(p1);
		root.addChild(p2);
		root.addChild(p3);
				
	}
	
	public void showResults(){
		
//		viewer.removeAll;
//		for (CompareResults thisResult : Compare.results) {
//		String matchFound, attrMatches, methMatches;
//		if (thisResult.matchFound) matchFound = "Yes";
//		else matchFound = "No";
//		attrMatches = thisResult.attributesFound+"/"+thisResult.attributesTotal;
//		methMatches = thisResult.methodsFound+"/"+thisResult.methodsTotal;
//		TableItem item = new TableItem(table, SWT.NONE);
//		item.setText(new String[] { thisResult.className, matchFound, attrMatches, methMatches });
//		}

		
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ViewResult.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
	
	}

	private void fillContextMenu(IMenuManager manager) {
		
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
	
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}


	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Results",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
		mainPanel.setFocus();

	}
}
