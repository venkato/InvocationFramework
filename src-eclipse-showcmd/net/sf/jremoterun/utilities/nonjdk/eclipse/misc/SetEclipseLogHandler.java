package net.sf.jremoterun.utilities.nonjdk.eclipse.misc;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.internal.ui.wizards.buildpaths.SetFilterWizardPage;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.ide.IDEWorkbenchErrorHandler;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.JrrUtilities3;
import nik.git.forcepush.jrr.JrrCommonEclipseBean;

public class SetEclipseLogHandler {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public static WorkbenchAdvisor workbenchAdvisorDefault;
	public static IDEWorkbenchErrorHandler ideWorkbenchErrorHandlerDefault;

	public static IDEWorkbenchErrorHandler findIDEWorkbenchErrorHandler() throws Exception {
		if (ideWorkbenchErrorHandlerDefault == null) {
			ideWorkbenchErrorHandlerDefault = (IDEWorkbenchErrorHandler)findWorkbenchAdvisor().getWorkbenchErrorHandler();
		}
		return ideWorkbenchErrorHandlerDefault;
	}
	
	public static void setMyErrorHandler(WorkbenchAdvisor workbenchAdvisor,IDEWorkbenchErrorHandler errorHandler) throws Exception {
		JrrClassUtils.setFieldValue(workbenchAdvisor, "ideWorkbenchErrorHandler", errorHandler);
	}

	public static WorkbenchAdvisor findWorkbenchAdvisor() throws Exception {
		if (workbenchAdvisorDefault == null) {
			workbenchAdvisorDefault = (WorkbenchAdvisor) JrrClassUtils.getFieldValue(Workbench.getInstance(),
					"advisor");
		}
		return workbenchAdvisorDefault;
	}

	
	public static void setLogHandler() {
		try {
			IDEWorkbenchErrorHandler ideWorkbenchErrorHandler = findIDEWorkbenchErrorHandler();
			MyIDEWorkbenchErrorHandler errorHandler = new MyIDEWorkbenchErrorHandler(ideWorkbenchErrorHandler);
			setMyErrorHandler(workbenchAdvisorDefault, errorHandler);
			JrrClassUtils.ignoreClassesForCurrentClass.add(MyIDEWorkbenchErrorHandler.class.getName());
		} catch (Throwable e) {
			JrrUtilities3.showException("eclipse 8 start 123", e);
			e.printStackTrace();
			log.log(Level.SEVERE, null, e);
		}
	}

}
