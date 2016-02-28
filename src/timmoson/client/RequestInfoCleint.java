package timmoson.client;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.common.TcpCallCommon;
import timmoson.common.transferedobjects.ReponseBean;

public class RequestInfoCleint extends TcpCallCommon {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));
	public volatile ClientParams clientParams;
	public volatile ClassLoader deserClassLoader;
	public volatile boolean firstCallInSession=true;
//	public String methodName;
//	public String serviceId;
//	public int paramsCount;
	public volatile int tryInvokactionCount=0;
	public final Object lock=new Object();
	public volatile Thread waitingThread;
	public volatile ReponseBean reponseBean;

	
}
