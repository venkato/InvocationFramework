package net.sf.jremoterun.utilities.nonjdk.eclipse.workingset;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.WorkingSet;

import net.sf.jremoterun.utilities.JrrClassUtils;



public class WorkingSetUpdate {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	void sample() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List<IAdaptable> adaptables = null;
		WorkingSet  ws = findWorkingSetByName("wsNameSample1");
		updateElements(ws, adaptables);
	}

	public static void updateElements(WorkingSet workingSet, List<IAdaptable> adaptables)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		JrrClassUtils.setFieldValue(workingSet, "elements", new ArrayList(adaptables));
	}

	public static WorkingSet findWorkingSetByName(String name) {
		IWorkingSetManager workingSetManager = WorkbenchPlugin.getDefault().getWorkingSetManager();
		return (WorkingSet) workingSetManager.getWorkingSet(name);
	}

	
	// org.eclipse.jdt.internal.debug.ui.console.JavaStackTraceHyperlink
}
