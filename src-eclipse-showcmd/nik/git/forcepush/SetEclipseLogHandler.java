package nik.git.forcepush;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.ide.IDEWorkbenchErrorHandler;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.JrrUtilities3;
import nik.git.forcepush.jrr.JrrCommonEclipseBean;

public class SetEclipseLogHandler {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public static void setLogHandler() {
		try {
			WorkbenchAdvisor workbenchErrorHandler = (WorkbenchAdvisor) JrrClassUtils
					.getFieldValue(Workbench.getInstance(), "advisor");
			IDEWorkbenchErrorHandler ideWorkbenchErrorHandler = JrrCommonEclipseBean.bean.getIdeWorkbenchErrorHandler();
			if (ideWorkbenchErrorHandler == null) {
				ideWorkbenchErrorHandler = (IDEWorkbenchErrorHandler) workbenchErrorHandler.getWorkbenchErrorHandler();
				JrrCommonEclipseBean.bean.setIdeWorkbenchErrorHandler(ideWorkbenchErrorHandler);
			}
			MyIDEWorkbenchErrorHandler errorHandler = new MyIDEWorkbenchErrorHandler();
			errorHandler.handler = ideWorkbenchErrorHandler;
			JrrClassUtils.setFieldValue(workbenchErrorHandler, "ideWorkbenchErrorHandler", errorHandler);
			JrrClassUtils.ignoreClassesForCurrentClass.add(MyIDEWorkbenchErrorHandler.class.getName());
		} catch (Exception e) {
			JrrUtilities3.showException("eclipse 8 start 123", e);
			e.printStackTrace();
			log.log(Level.SEVERE, null, e);
		}
	}
	
}
