package net.sf.jremoterun.utilities.nonjdk.eclipse.init;

import java.util.logging.Logger;

import org.eclipse.core.internal.runtime.RuntimeLog;
import org.eclipse.ui.internal.WorkbenchErrorHandlerProxy;
import org.eclipse.ui.statushandlers.StatusManager;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class AddIgnoreClasses implements Runnable{

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	@Override
	public void run() {
		init();		
	}
	
	
	public static void init() {
		JrrClassUtils.ignoreClassesForCurrentClass.add(WorkbenchErrorHandlerProxy.class.getName());
		JrrClassUtils.ignoreClassesForCurrentClass.add(StatusManager.class.getName());
		JrrClassUtils.ignoreClassesForCurrentClass.add(RuntimeLog.class.getName());
	}
	
}
