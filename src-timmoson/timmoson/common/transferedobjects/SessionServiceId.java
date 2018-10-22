package timmoson.common.transferedobjects;

import timmoson.common.sertcp.TcpSession;
import timmoson.server.ServiceSupport;
import timmoson.server.TcpServiceObject;

import java.util.Date;

public class SessionServiceId implements ServiceId {

	private static final long serialVersionUID = 9126045586318639884L;

	public String id;

	public SessionServiceId(String id) {
		this.id = id;
		
	}
	
	@Override
	public ServiceSupport find(TcpSession tcpSession ) throws Exception {
		TcpServiceObject tcpServiceObject = tcpSession.serviceObjectsServer.get(id);
		if(tcpServiceObject==null) {
			throw new Exception("not found "+id+ " "+tcpSession);
		}
		tcpServiceObject.lastAccess = new Date();
		return tcpServiceObject;
	}
	
	@Override
	public String toString() {
		return id+"";
	}

}
