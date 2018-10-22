package timmoson.server.telnet;


import org.apache.log4j.Logger;
import timmoson.client.telnet.TelnetTimmosonSessionStoreSimple;
import timmoson.common.telnet.TelnetRemoteService;
import timmoson.common.telnet.TelnetSession;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TelnetSocketLlistener {
	private static final Logger log = Logger
			.getLogger(TelnetSocketLlistener.class);
//	public static TcpSocketLlistener defaultTcpSocketLlistener = new TcpSocketLlistener();
	
	public ServerSocket serverSocket;
	
//	public GetReponseHandler reponseHandler=new GetReponseHandler();

	public TelnetSocketLlistener(int port) throws IOException {
		this.serverSocket =new ServerSocket(port);
	}
	public TelnetSocketLlistener(ServerSocket serverSocket) {
	this.serverSocket = serverSocket;
}

	public  void startListener() throws IOException {
		while (true) {
			final Socket socket = serverSocket.accept();
			log.info("new client" + socket);
			TelnetSession tcpSession = buildTcpSession(socket);
			if (tcpSession == null) {
				log.warn("tcp session is null " + socket);
				continue;
			}
			// if (newSessionBuilderServer == null) {
			tcpSession.sessionBuilder = new TelnetTimmosonSessionStoreSimple(tcpSession);
			// } else {
			// tcpSession.sessionBuilder = newSessionBuilderServer;
			// }
			TelnetRemoteService.handleSocketNewThread(tcpSession);
		}
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
	


	public TelnetSession buildTcpSession(Socket socket) throws IOException {
		TelnetSession tcpSession = TelnetSession.buildTcpSession(socket) ;
		return tcpSession;
	}
}
