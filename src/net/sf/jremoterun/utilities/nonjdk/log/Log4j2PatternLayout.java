package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.message.Message;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter.sep;

public class Log4j2PatternLayout implements Layout<String> {

    private static SimpleDateFormat sdfDayHourMin = new SimpleDateFormat("dd HH:mm:ss");
    public static boolean logLocation = true;
    public boolean logTime = false;

    public static byte[] emptyBytes = "".getBytes();

    public volatile IsLogExceptionStackTrace isLogExceptionStackTrace = new IsLogExceptionStackTrace();

    public static Map<String, Log4j2PatternLayout> customLayouts = new HashMap();

    public static Collection<String> ignoreClassesForCurrentClass = JrrClassUtils.ignoreClassesForCurrentClass;

    public Log4j2PatternLayout() {
    }

    @Override
    public void encode(LogEvent source, ByteBufferDestination destination) {
        byte[] byteArray = toByteArray(source);
        writeTo(byteArray, 0, byteArray.length, destination);
//		try {
//        destination.writeBytes(byteArray, 0, byteArray.length);
//		} catch (Exception e) {
//			System.err.println("got exception " + new String(byteArray));
//			e.printStackTrace();
//		}
    }

    public static void writeTo(final byte[] data, int offset, int length, final ByteBufferDestination destination) {
        int chunk = 0;
        synchronized (destination) {
            ByteBuffer buffer = destination.getByteBuffer();
            do {
                if (length > buffer.remaining()) {
                    buffer = destination.drain(buffer);
                }
                chunk = Math.min(length, buffer.remaining());
                buffer.put(data, offset, chunk);
                offset += chunk;
                length -= chunk;
            } while (length > 0);
        }
    }


    @Override
    public byte[] getFooter() {
        return emptyBytes;
    }

    @Override
    public byte[] getHeader() {
        return emptyBytes;
    }

    @Override
    public byte[] toByteArray(LogEvent event) {
        return toSerializable(event).getBytes();
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public Map<String, String> getContentFormat() {
        return new TreeMap<String, String>();
    }

    @Override
    public String toSerializable(LogEvent event) {
        Log4j2PatternLayout custom = customLayouts.get(event.getLoggerName());
        if (custom != null && custom != this) {
            return custom.toSerializableImpl(event);
        }
        return toSerializableImpl(event);
    }

    public String toSerializableImpl(LogEvent event) {
        StringBuilder sb = new StringBuilder();
        formatImpl(event, sb);
        return sb.toString();
    }


    public void formatImpl(LogEvent logRecord, StringBuilder sb) {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        formatImpl2(logRecord, sb, stackTraces);
    }

    public void formatImpl2(LogEvent logRecord, StringBuilder sb, StackTraceElement[] stackTraces) {
//        System.out.println("Log4j2PatternLayout : "+logRecord.getMessage());
        StackTraceElement location = getStacktraceELement(stackTraces);
        logStackElement(location, sb);
        sb.append(" - ");
        logTime(logRecord, sb);
        Message message = logRecord.getMessage();
        if (message == null) {

        } else {
//            if (message instanceof StringBuilderFormattable) {
//                StringBuilderFormattable stringBuilderFormattable = (StringBuilderFormattable) message;
//                StringBuilder sb3 =new StringBuilder();
//                stringBuilderFormattable.formatTo(sb3);
//            }
            String s = message.getFormattedMessage();
            if (s == null) {

            } else {
                s = s.replace('\t', ' ');
//                if(s.contains("http://search.maven.org/#search")){
//                    Thread.dumpStack();
//                }
                logMessage(sb, s, logRecord);
            }
        }


        logStackTraceIfNeeded(logRecord, sb, stackTraces, location);
        sb.append(sep);
    }

    public void logMessage(StringBuilder sb, String msg, LogEvent logRecord) {
        sb.append(msg);
    }

    public void logStackElement(StackTraceElement se, StringBuilder sb) {
        sb.append(se);
//		String className = se.getClassName();
    }

    public StackTraceElement getStacktraceELement(StackTraceElement[] stackTraces) {
        int k = 0;
        for (StackTraceElement stackTraceElement : stackTraces) {
            k++;
            if (k < 2) {
                continue;
            }
            if (acceptStackTraceElement(stackTraceElement)) {
                return stackTraceElement;
            }
        }
        return null;
    }

    public boolean acceptStackTraceElement(StackTraceElement stackTraceElement) {
        String lcassName = stackTraceElement.getClassName();
        for (String ignore : ignoreClassesForCurrentClass) {
            boolean res = lcassName.startsWith(ignore);
            if (res) {
                return false;
            } else {

            }
        }
        return true;

    }

    public String getTime() {
        String time;
        synchronized (sdfDayHourMin) {
            time = sdfDayHourMin.format(new Date());
        }
        return time;
    }


    public void logTime(LogEvent logRecord, StringBuilder sb) {
        if (Level.WARN.isMoreSpecificThan(logRecord.getLevel())) {
            sb.append(getTime());
            sb.append(" ");
            sb.append(logRecord.getLevel().name());
            sb.append(" ");
        } else {
            if (logTime) {
                sb.append(" ");
                sb.append(getTime());
                sb.append(" - ");
            }
        }
    }

    public void onEmptyStackTrace(LogEvent logRecord, StringBuilder sb, StackTraceElement[] stackTraces, StackTraceElement location, Throwable ti) {

    }


    public boolean isNeedPrintStackTrace(LogEvent logRecord, StringBuilder sb, StackTraceElement[] stackTraces, StackTraceElement location, Throwable ti) {
        if (!Level.WARN.isLessSpecificThan(logRecord.getLevel())) {
            return false;
        }
        boolean isLogStackTrace = isLogExceptionStackTrace.isLogStackTrace(logRecord, sb, stackTraces, location, ti);
        return isLogStackTrace;
    }

    public void logStackTraceIfNeeded(LogEvent logRecord, StringBuilder sb, StackTraceElement[] stackTraces, StackTraceElement location) {
        Throwable ti = logRecord.getThrown();
        final boolean error = isNeedPrintStackTrace(logRecord, sb, stackTraces, location, ti);
        if (ti == null) {
            if (error) {
                writeStackTrace(sb, stackTraces);
            }
        } else {
            sb.append(" ");
            Throwable rootException = JrrUtils.getRootException(ti);
            if (error) {
                StackTraceElement[] stackTraces3 = rootException.getStackTrace();
                if (stackTraces3 == null || stackTraces3.length == 0) {
                    onEmptyStackTrace(logRecord, sb, stackTraces, location, ti);
                    writeStackTrace(sb, stackTraces);
                } else {
                    final StringWriter stringWriter = new StringWriter();
                    rootException.printStackTrace(new PrintWriter(stringWriter));
                    sb.append(stringWriter.getBuffer());
                }
            } else {
                sb.append(rootException);
            }
        }
    }

    public void writeStackTrace(StringBuilder sb, StackTraceElement[] stackTraces) {
        int i = 0;
        for (StackTraceElement stackTraceElement : stackTraces) {
            i++;
            if (i < 5) {
                continue;
            }
            if (acceptStackTraceElement(stackTraceElement)) {
                sb.append(sep);
                sb.append("  ");
                sb.append(stackTraceElement.toString());
            }
        }

    }


}