package nik.git.forcepush;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.internal.jobs.Worker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.internal.ide.IDEWorkbenchErrorHandler;
import org.eclipse.ui.statushandlers.StatusAdapter;

import sun.reflect.Reflection;

public class MyIDEWorkbenchErrorHandler extends IDEWorkbenchErrorHandler {

	private static final Logger log = Logger.getLogger(Reflection.getCallerClass(1).getName());
	public boolean logStackTradeWhereStatusInvokedException = false;
	public IDEWorkbenchErrorHandler handler;

	public boolean supportsNotification(int type) {
		return handler.supportsNotification(type);
	}

	public int hashCode() {
		return handler.hashCode();
	}

	public Map getParams() {
		return handler.getParams();
	}

	public Object getParam(Object key) {
		return handler.getParam(key);
	}

	private static SimpleDateFormat sdfDayHourMin = new SimpleDateFormat("dd HH:mm:ss");

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
			handler.handle(statusAdapter, style);
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
			log.info(sb+"");
		} else {
			log.log(Level.INFO, sb+"", exception);
		}
	}

	public void setParams(Map params) {
		handler.setParams(params);
	}

	public boolean equals(Object obj) {
		return handler.equals(obj);
	}

	public String getId() {
		return handler.getId();
	}

	public void setId(String id) {
		handler.setId(id);
	}

	public String toString() {
		return handler.toString();
	}

	public MyIDEWorkbenchErrorHandler() {
		super(null);
	}

}
