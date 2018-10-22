package net.sf.jremoterun.utilities.nonjdk.eclipse.misc;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.internal.jobs.Worker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.internal.ide.IDEWorkbenchErrorHandler;
import org.eclipse.ui.statushandlers.StatusAdapter;

public class MyIDEWorkbenchErrorHandler extends IDEWorkbenchErrorHandler {

	private static final Logger log = Logger.getLogger(MyIDEWorkbenchErrorHandler.class.getName());
	public boolean logStackTradeWhereStatusInvokedException = false;
	public IDEWorkbenchErrorHandler nestedHandler;

	private static SimpleDateFormat sdfDayHourMin = new SimpleDateFormat("dd HH:mm:ss");

	public MyIDEWorkbenchErrorHandler() {
		super(null);
	}	

	public MyIDEWorkbenchErrorHandler(IDEWorkbenchErrorHandler nestedHandler) {
		super(null);
		this.nestedHandler = nestedHandler;
	}



	public boolean isLogmsg(StatusAdapter statusAdapter, int style) {
		IStatus s = statusAdapter.getStatus();
		String message = s.getMessage();
		if (message.contains(
				"Using platform encoding (Cp1252 actually) to copy filtered resources, i.e. build is platform dependent!")) {
			return false;
		}
		if ("Using platform encoding (Cp1251 actually) to copy filtered resources, i.e. build is platform dependent!"
				.equals(s.getMessage())) {
			return false;
		}
		return true;
	}

	public void handle(StatusAdapter statusAdapter, int style) {
		boolean propogate = true;
		try {
			IStatus s = statusAdapter.getStatus();

			if (isLogmsg(statusAdapter, style)) {
				logMsg(s);
			} else {
				log.info("ignore maven cp1251");
				propogate = false;
			}
		} catch (Exception e) {
			log.log(Level.INFO, null, e);
			e.printStackTrace();
		}
		if (propogate) {
			nestedHandler.handle(statusAdapter, style);
		}
	}

	public void logMsg(IStatus s) {
		StringBuilder sb = new StringBuilder();
		sb.append(s.getCode() + " " + s.getMessage() + " " + s.getPlugin() + " " + s.getSeverity() + " "
				+ Arrays.toString(s.getChildren()));
		boolean addStackTrace = true;
		if (Thread.currentThread() instanceof Worker) {
			Worker worker = (Worker) Thread.currentThread();
			Job currentJob = worker.currentJob();
			if (currentJob == null) {
				sb.append(" job=null");
			} else {
				sb.append(" toString=" + currentJob);
				sb.append(" name=" + currentJob.getName());
				// sb.append(" "+currentJob.getName());
				sb.append(" class=" + currentJob.getClass().getName());
				addStackTrace = false;
				sb.append(" time=" + sdfDayHourMin.format(new Date()));
			}
		}
		Throwable exception = s.getException();
		if (addStackTrace && exception == null) {
			exception = new Exception("Stack trace");
		} else {
			if (logStackTradeWhereStatusInvokedException) {
				log.log(Level.INFO, null, new Exception("Stack trace"));
			}
		}
		if (exception == null) {
			log.info(sb + "");
		} else {
			log.log(Level.INFO, sb + "", exception);
		}
	}

	public boolean supportsNotification(int type) {
		return nestedHandler.supportsNotification(type);
	}

	public int hashCode() {
		return nestedHandler.hashCode();
	}

	public Map getParams() {
		return nestedHandler.getParams();
	}

	public Object getParam(Object key) {
		return nestedHandler.getParam(key);
	}

	public void setParams(Map params) {
		nestedHandler.setParams(params);
	}

	public boolean equals(Object obj) {
		return nestedHandler.equals(obj);
	}

	public String getId() {
		return nestedHandler.getId();
	}

	public void setId(String id) {
		nestedHandler.setId(id);
	}

	public String toString() {
		return nestedHandler.toString();
	}

}
