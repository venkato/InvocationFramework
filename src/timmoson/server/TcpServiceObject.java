package timmoson.server;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.common.transferedobjects.ServiceId;

import java.util.Date;

public class TcpServiceObject extends ServiceSupport{
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));
	public final Date createDate=new Date();
	public Date lastAccess=createDate;
	public ServiceId serviceInSessionId;

}
