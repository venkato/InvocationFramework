package timmoson.server.telnet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.common.telnet.TelnetSession;
import timmoson.server.ServiceSupport;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class TelnetRequestBean  {

	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));
	public ServiceSupport seriveObject; 
	public String serviceId;
	public String methodName;
	public ArrayList<String> params=new ArrayList<String>();
	public Object[] paramsAsObject;
	
	public TelnetSession telnetSession;
	public Method method;

}
