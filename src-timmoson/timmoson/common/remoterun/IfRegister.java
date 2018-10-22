package timmoson.common.remoterun;

import net.sf.jremoterun.ICodeForExecuting;
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.SimpleFindParentClassLoader;
import net.sf.jremoterun.mbeans.Runner;
import net.sf.jremoterun.utilities.DefaultObjectName;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.MBeanFromJavaBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import timmoson.client.TimmosonSessionStoreSimple;
import timmoson.common.sertcp.IfSharedObjects;
import timmoson.common.sertcp.RemoteService;
import timmoson.common.sertcp.TcpSession;
import timmoson.server.ServiceLocator;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class IfRegister implements ICodeForExecuting, DefaultObjectName {

	public static ObjectName objectName = JrrUtils
			.createObjectName("if:type=if");
	public static String ifClassloaderId = "timmoson.classloader";

	@Override
	public Object run(List params, Map previousCode) throws Exception {
		MBeanServer beanServer = JrrUtils.findLocalMBeanServer();
		if (beanServer == null) {
			throw new Exception("mbean server is null");
		}
		if (beanServer.isRegistered(objectName)) {
			throw new Exception("mbean already refistered " + objectName);
		}
		MBeanFromJavaBean.registerMBean(this);
		// ServiceLocator.classLoaderCheck();
		if (!IfSharedObjects.services.containsKey(Runner.class.getName())) {
			ServiceLocator.regNewService(SharedObjectsUtils.getRunnerObject());
		}
		SimpleFindParentClassLoader.setDefaultClassLoader(JrrClassUtils
				.getCurrentClassLoader());
		SharedObjectsUtils.getClassLoaders().put(ifClassloaderId,
				ServiceLocator.class.getClassLoader());
		return null;
	}

	public void connectToHost(String host, int port) throws Exception {
		// ServiceLocator.classLoaderCheck();
		Socket socket = new Socket(host, port);
		TcpSession tcpSession = TcpSession.buildTcpSession(socket);
		tcpSession.sessionBuilder = new TimmosonSessionStoreSimple(tcpSession);
		// tcpSession.sessionClosedListeners.add(new TcpSessionClosedListener()
		// {
		//
		// @Override
		// public void sessionClosed() {
		// log.info("session closed " + new Date());
		// }
		// });
		RemoteService.defaultRemoteService.handleSocketNewThread(tcpSession);
	}

	@Override
	public ObjectName getDefaultObjectName()
			throws MalformedObjectNameException {
		return objectName;
	}

}
