package timmoson.common.transferedobjects;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.Java5VM;
import timmoson.common.sertcp.TcpSession;
import timmoson.server.ServiceInfo;
import timmoson.server.ServiceSupport;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class JmxServiceId implements ServiceId {

	private static final long serialVersionUID = 9104133732346120772L;
	public ObjectName objectName;

	public JmxServiceId(ObjectName objectName) {
		this.objectName = objectName;

	}

	@Override
	public ServiceSupport find(TcpSession tcpSession) throws Exception {
		MBeanServer localMBeanServer = JrrUtils.findLocalMBeanServer();
		Object mBeanObject = Java5VM.getMBeanObject(objectName,
				localMBeanServer);
		ServiceInfo tcpServiceObject = new ServiceInfo(objectName.toString(),
				mBeanObject);
		return tcpServiceObject;
	}

	@Override
	public String toString() {
		return objectName+"";
	}
}
