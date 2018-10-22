package timmoson.server;

import org.apache.log4j.Logger;
import timmoson.client.TimmosonSessionStoreSimple;
import timmoson.common.sertcp.RemoteService;
import timmoson.common.sertcp.TcpSession;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.WeakHashMap;

public class TcpSocketLlistener {
	private static final Logger log = Logger.getLogger(TcpSocketLlistener.class);
	// public static TcpSocketLlistener defaultTcpSocketLlistener = new
	// TcpSocketLlistener();

	public ServerSocket serverSocket;

	public static WeakHashMap<TcpSocketLlistener, Object> tcpSocketLlisteners = new WeakHashMap();
	public WeakHashMap<TcpSession, Object> tcpSessions = new WeakHashMap();

	// public GetReponseHandler reponseHandler=new GetReponseHandler();

	public TcpSocketLlistener(int port) throws IOException {
		this(new ServerSocket(port));
		tcpSocketLlisteners.put(this, object);
	}

	public TcpSocketLlistener(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;

	}

	public static final Object object = new Object();

	private volatile boolean stop = false;

	public void startListener() throws IOException {
		while (true) {
			if (stop) {
				log.info("stop requested");
				break;
			}
			final Socket socket;
			try {
				socket = serverSocket.accept();
			} catch(SocketException e) {
				if(stop) {
					log.info("stop requested "+e);
					break;
				}
				throw e;
			}
			log.debug("new client" + socket);
			TcpSession tcpSession = buildTcpSession(socket);
			tcpSessions.put(tcpSession, object);
			if (tcpSession == null) {
				log.warn("tcp session is null " + socket);
				continue;
			}
			// if (newSessionBuilderServer == null) {
			tcpSession.sessionBuilder = new TimmosonSessionStoreSimple(tcpSession);
			// } else {
			// tcpSession.sessionBuilder = newSessionBuilderServer;
			// }
			RemoteService.defaultRemoteService.handleSocketNewThread(tcpSession);
		}
	}

	public void stop() throws IOException {
		if (!stop) {
			stop = true;
			serverSocket.close();
		}
	}

	public void handleSocketNewThread(TcpSession tcpSession) {
		RemoteService.defaultRemoteService.handleSocketNewThread(tcpSession);
	}

	public Thread startListenerInNewThread() throws IOException {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					startListener();
				} catch (IOException e) {
					log.warn(null, e);
				}

			};
		};
		thread.start();
		return thread;
	}

	public TcpSession buildTcpSession(Socket socket) throws IOException {
		TcpSession tcpSession = TcpSession.buildTcpSession(socket);
		return tcpSession;
	}
}
