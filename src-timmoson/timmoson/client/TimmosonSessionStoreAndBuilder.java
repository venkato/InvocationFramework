package timmoson.client;

import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import timmoson.common.sertcp.RemoteService;
import timmoson.common.sertcp.TcpSession;
import timmoson.server.ServiceInfo;
import timmoson.server.service.TestService;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class TimmosonSessionStoreAndBuilder implements TimmosonSessionStore {

	private static final Log log = LogFactory.getLog(JrrClassUtils.getCurrentClass());
	public Object connectionLock = new Object();
	public ServiceInfo serviceInfo;
	public String tcpHost;
	public int tcpPort;

	public ArrayList<TcpSessionClosedListener> sessionClosedListeners = new ArrayList<TcpSessionClosedListener>();

	private TestService testService = ClientSendRequest.makeProxyForService(
			TestService.class, this);

	public TimmosonSessionStoreAndBuilder(String tcpHost, int tcpPort) {
		this.tcpPort = tcpPort;
		this.tcpHost = tcpHost;
	}

	@Override
	public TcpSession getTcpSession() throws Exception {
		synchronized (connectionLock) {
//			log.info(clienTcpSession);
			if (clienTcpSession == null || clienTcpSession.isClosed()) {
				initTcpConnection();
			}
		}
		log.debug(clienTcpSession.isClosed());
		log.debug(clienTcpSession.socket.isClosed());
		return clienTcpSession;
	}

	public TcpSession clienTcpSession;

	protected TcpSession buildTcpSession(Socket socket) throws Exception {
		return TcpSession.buildTcpSession(socket);
	}

	public void initTcpConnection() throws Exception {
		Socket socket;
		try {
			log.debug("try create new connection to " + tcpHost + ":" + tcpPort);
			socket = new Socket(tcpHost, tcpPort);
		} catch (IOException e) {
//			SwingUtilities.invokeLater(new Runnable() {
//
//				@Override
//				public void run() {
//					JOptionPane.showMessageDialog(null,
//							"Server is restarting. Please again later");
//
//				}
//			});
			throw e;
		}
		clienTcpSession = buildTcpSession(socket);
		clienTcpSession.sessionClosedListeners.addAll(sessionClosedListeners);
		clienTcpSession.sessionBuilder = this;
		// clienTcpSession.sessionClosedListeners
		// .add(SessionCloseListenClient.sessionCloseListenClient);
		RemoteService.defaultRemoteService.handleSocketNewThread(clienTcpSession);
	}

	@Override
	public TestService getTestService() {
		return testService;
	}

}
