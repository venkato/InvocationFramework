package timmoson.common.debug;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import timmoson.client.RequestInfoCleint;
import timmoson.common.sertcp.TcpSession;
import timmoson.server.TcpCallInfoServer;

import java.util.Date;

public class TcpSessionTrackerBean {

	public final Thread thread=Thread.currentThread();
//	stored in infoCleint.requestBean.stackTrace
//	public Exception stackTrace;
	public Throwable exception;
	public final Date requestDate=new Date(); 
	public Date responseDate; 
	public TcpSession session;
//	public String serviceId;
//	public Object service;
//	public Method method;
//	public Object[] params;
	public TcpCallInfoServer serverCallInfo;
	public RequestInfoCleint requestInfoCleint;
//	public TcpSession session;
	public Object result;
}
