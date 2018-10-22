package timmoson.common;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import timmoson.common.sertcp.TcpSession;
import timmoson.common.transferedobjects.RequestBean;

import java.lang.reflect.Method;

public class TcpCallCommon {

	public volatile TcpSession tcpSession;
	public volatile Object[] params;
	public volatile Method method;
	public volatile RequestBean requestBean=new RequestBean();
}
