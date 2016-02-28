package timmoson.common.debug;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.localcall.LocalSession;

import java.lang.reflect.Method;
import java.util.Date;

public class LocalSessionRequestBean {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));
	
	public final Exception stackTrace=new Exception();
	public Throwable exception;
	public final Date requestDate=new Date(); 
	public Date responseDate; 
//	public String serviceId;
	public Object service;
	public Method method;
	public Object[] params;
	public LocalSession session;
	public Object result;
}
