package timmoson.client.telnet;


import timmoson.common.telnet.TelnetSession;

import java.io.IOException;


public class TelnetTimmosonSessionStoreSimple  {
	private TelnetSession tcpSession;
	

	public TelnetTimmosonSessionStoreSimple(TelnetSession tcpSession) {
		this.tcpSession = tcpSession;
	}

	public TelnetSession getTcpSession() throws IOException {
		if (tcpSession.isClosed()) {
			throw new IOException("session is closed");
		}
		return tcpSession;
	}

}
