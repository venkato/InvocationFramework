package timmoson.client;

import timmoson.common.sertcp.TcpSession;
import timmoson.server.service.TestService;


public interface TimmosonSessionStore {
	public TcpSession getTcpSession() throws Exception;
	
	public TestService getTestService();
}
