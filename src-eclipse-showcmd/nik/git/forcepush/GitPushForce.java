package nik.git.forcepush;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import nik.git.forcepush.jrr.PushForceImpl2;

public class GitPushForce implements IObjectActionDelegate {

	public static IObjectActionDelegate delegate=new PushForceImpl2();


	public GitPushForce() {
	}


	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		delegate.setActivePart(action, targetPart);
	}


	@Override
	public void run(IAction action) {
		delegate.run(action);
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		delegate.selectionChanged(action, selection);
	}



}
