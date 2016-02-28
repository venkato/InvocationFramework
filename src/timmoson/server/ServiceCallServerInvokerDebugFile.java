package timmoson.server;

import junit.framework.Assert;
import net.sf.jremoterun.utilities.FileOutputStream2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.common.sertcp.TcpSession;

import java.io.File;
import java.io.IOException;

public abstract class ServiceCallServerInvokerDebugFile extends ServiceCallServerInvokerDebug {

	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public boolean doLog = true;
	

	public File logDir = null;//new File("tcpmon/");
	public  File logFile = null;//new File(logDir, "findapp-log.txt");
	// public static Writer fileOut;
	public static long maxLength = 1000 * 1000 * 10;
	public static FileOutputStream2 fileOut;

//	public void appendLog(StringBuffer sb) throws IOException {
//		sb.insert(0, sdfShort.format(new Date()) + " ");
//		sb.append("\r\n");
//	}
	
	
	@Override
	public void writeLog(StringBuffer sb) throws IOException {
		synchronized (lockObject) {
			if (fileOut == null) {
				if(logFile==null){
					throw new NullPointerException("log file is null");
				}
				fileOut = new FileOutputStream2(logFile, true);
			}
			// if (logFile.length() > maxLength) {
			// fileOut.flush();
			// fileOut.close();
			// FileUtils.moveFile(logFile, new
			// File(logDir,logFile.getName()+sdfLong.format(new Date())));
			// fileOut = new FileOutputStream(logFile,false);
			// }
			Assert.assertNotNull(fileOut);
			String msg = sb.toString();
			Assert.assertNotNull(msg);
			fileOut.write(msg.getBytes("cp1251"));
		}
		
	}

	@Override
	public abstract void requestToStringGererall(TcpSession tcpSession,
			String metodName, StringBuffer sb);

}