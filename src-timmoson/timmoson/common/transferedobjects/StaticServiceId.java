package timmoson.common.transferedobjects;

import timmoson.common.sertcp.TcpSession;
import timmoson.server.ServiceLocator;
import timmoson.server.ServiceSupport;

public class StaticServiceId implements ServiceId {

	private static final long serialVersionUID = 9126045586318639884L;

	public String id;

	public StaticServiceId(String id) {
		this.id = id;

	}

	@Override
	public ServiceSupport find(TcpSession tcpSession) throws Exception {
		return ServiceLocator.getService(id);
	}

	@Override
	public String toString() {
		return id+"";
	}
}
