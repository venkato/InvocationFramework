package timmoson.server;


import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonutils.CommonToString2;
import timmoson.common.sertcp.TcpSession;
import timmoson.common.transferedobjects.ReponseBean;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ServiceCallServerInvokerDebug extends ServiceCallServerInvoker {

	private static final Log log = LogFactory.getLog(JrrClassUtils.getCurrentClass());

	public boolean doLog = true;
	

	@Override
	public void makeCall(final TcpCallInfoServer callInfo) {
		if (doLog) {
			StringBuffer sb = new StringBuffer();
			sb.append("in  ");
			requestToStringGererall(callInfo.tcpSession,
					callInfo.method.getName(), sb);
			CommonToString2.toString(callInfo.params,
					CommonToString2.maxStingLength, sb);
			sb.append(" ");
			try {
				appendLog(sb);
			} catch (IOException e) {
				log.info(null, e);

			}
		}
		super.makeCall(callInfo);
	}

	@Override
	public void writeResponseException(ReponseBean reponseBean,
			Throwable throwable, TcpCallInfoServer callInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("exc ");
		requestToStringGererall(callInfo.tcpSession, callInfo.method.getName(),
				sb);
		sb.append(JrrUtils.exceptionToString(JrrUtils
				.getRootException(throwable)));
		try {
			appendLog(sb);
		} catch (IOException e) {
			log.info(null, e);

		}
	}

	@Override
	public void writeResponseGeneral(ReponseBean reponseBean, Object result,
			TcpCallInfoServer callInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("res ");
		requestToStringGererall(callInfo.tcpSession, callInfo.method.getName(),
				sb);
		CommonToString2.toString(result, CommonToString2.maxStingLength, sb);
		try {
			appendLog(sb);
		} catch (IOException e) {
			log.info(null, e);

		}
	}

	@Override
	public void writeResponseGeneralNotSerializable(ReponseBean reponseBean,
			Object result, TcpCallInfoServer callInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("res NS ");
		requestToStringGererall(callInfo.tcpSession, callInfo.method.getName(),
				sb);
		sb.append(result.getClass().getName());
		try {
			appendLog(sb);
		} catch (IOException e) {
			log.info(null, e);

		}
	}

	public  Object lockObject = new Object();
	// public static Writer fileOut;
	public  SimpleDateFormat sdfLong = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");
	public  SimpleDateFormat sdfShort = new SimpleDateFormat("HH:mm:ss");

	public void appendLog(StringBuffer sb) throws IOException {
		sb.insert(0, sdfShort.format(new Date()) + " ");
		sb.append("\r\n");
		synchronized (lockObject) {
			writeLog(sb);
		}
	}
	
	public abstract void writeLog(StringBuffer sb) throws IOException;

	public abstract void requestToStringGererall(TcpSession tcpSession,
			String metodName, StringBuffer sb);

}