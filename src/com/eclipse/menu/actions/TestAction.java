package com.eclipse.menu.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class TestAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	 	
	public TestAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction arg0) {
		if(window == null){
			return;
		}
		//action after click the menu
		MessageDialog.openInformation(window.getShell(), "Welcome to Trace Magic", "Please follow instructions!");            
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		window = null;
		
	}

	@Override
	public void init(IWorkbenchWindow arg0) {
		this.window = window;
		
	}

}
