package timmoson.client;


import timmoson.common.sertcp.TcpSession;
import timmoson.server.service.TestService;

import java.io.IOException;


public class TimmosonSessionStoreSimple implements TimmosonSessionStore {
	private TcpSession tcpSession;
	private TestService testService = ClientSendRequest.makeProxyForService(TestService.class, this);
	
	@Override
	public TestService getTestService() {
		return testService;
	}

	public TimmosonSessionStoreSimple(TcpSession tcpSession) {
		this.tcpSession = tcpSession;
	}

	@Override
	public TcpSession getTcpSession() throws IOException {
		if (tcpSession.isClosed()) {
			throw new IOException("session is closed");
		}
		return tcpSession;
	}

}
