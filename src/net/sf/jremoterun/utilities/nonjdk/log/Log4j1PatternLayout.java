package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import static net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter.sep;

public class Log4j1PatternLayout extends Layout {

	private static SimpleDateFormat sdfDayHourMin = new SimpleDateFormat("dd HH:mm:ss");
	public static boolean logTime = false;


	public static Collection<String> ignoreClassesForCurrentClass = JrrClassUtils.ignoreClassesForCurrentClass;

	public Log4j1PatternLayout() {
	}



	@Override
	public void activateOptions() {
	}

	@Override
	public boolean ignoresThrowable() {
		return false;
	}

	@Override
	public String format(LoggingEvent record) {
		StringBuilder sb = new StringBuilder();
		formatImpl(record, sb);
		return sb.toString();
	}



	void formatImpl(LoggingEvent logRecord, StringBuilder sb) {
		StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
		logStackElement(getStacktraceELement(stackTraces), sb);
		sb.append(" - ");
		logTime(logRecord, sb);
		sb.append(logRecord.getMessage());



		logStackTrace(logRecord, sb, stackTraces);
		sb.append(sep);
	}

	void logStackElement(StackTraceElement se, StringBuilder sb) {
		sb.append(se);
		String className = se.getClassName();
	}

	StackTraceElement getStacktraceELement(StackTraceElement[] stackTraces) {
		int k = 0;
		for (StackTraceElement stackTraceElement : stackTraces) {
			k++;
			if (k < 2) {
				continue;
			}
			v:
			{
				String lcassName = stackTraceElement.getClassName();
				for (String ignore : ignoreClassesForCurrentClass) {
					boolean res = lcassName.startsWith(ignore);
					if (res) {
						break v;
					} else {

					}
				}
				return stackTraceElement;
			}

		}
		return null;
	}

	String getTime() {
		String time;
		synchronized (sdfDayHourMin) {
			time = sdfDayHourMin.format(new Date());
		}
		return time;
	}


	void logTime(LoggingEvent logRecord, StringBuilder sb) {
		if (logRecord.getLevel().isGreaterOrEqual( Level.WARN)) {
			sb.append(getTime());
			sb.append(" ");
			sb.append(logRecord.getLevel().toString());
			sb.append(" ");
		} else {
			if (logTime) {
				sb.append(" ");
				sb.append(getTime());
				sb.append(" - ");
			}
		}
	}

	void logStackTrace(LoggingEvent logRecord, StringBuilder sb, StackTraceElement[] stackTraces) {
		boolean error = false;
		if (logRecord.getLevel().isGreaterOrEqual( Level.WARN)) {
			error = true;
		}
		ThrowableInformation ti = logRecord.getThrowableInformation();
		if (ti != null) {
			sb.append(" ");
			Throwable rootException = JrrUtils.getRootException(ti.getThrowable());
			if (error) {
				final StringWriter stringWriter = new StringWriter();
				rootException.printStackTrace(new PrintWriter(stringWriter));
				sb.append(stringWriter.getBuffer());
			} else {
				sb.append(rootException);
			}
		} else if (error) {

			int i = 0;
			for (StackTraceElement stackTraceElement : stackTraces) {
				v:
				{
					i++;
					if (i < 5) {
						continue;
					}

					String lcassName = stackTraceElement.getClassName();
					for (String ignore : ignoreClassesForCurrentClass) {
						if (lcassName.startsWith(ignore)) {
							break v;
						}
					}
					sb.append(sep);
					sb.append("  ");
					sb.append(stackTraceElement.toString());
				}
			}
		}
	}

}