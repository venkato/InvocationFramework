package timmoson.common.telnet;

import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import timmoson.client.DGCMonitor;
import timmoson.common.CallInfoServer;
import timmoson.common.sertcp.TimmosonSettings;
import timmoson.server.telnet.TelnetServerUtilsStatic;

import java.io.ByteArrayOutputStream;

public class TelnetRemoteService {
	private static final Log log = LogFactory.getLog(JrrClassUtils.getCurrentClass());

	// public static NewSessionBuilder newSessionBuilderServer=new
	// NewSessionBuilderSimple(
	// tcpSession);

	public static ThreadLocal<CallInfoServer> callsInfos = new ThreadLocal();

	// public static TcpSocketLlistener socketLlistener = new
	// TcpSocketLlistener();

	// public static void startListener(int port,
	// TcpSocketLlistener tcpSocketLlistener) throws IOException {
	// ServerSocket serverSocket = new ServerSocket(port);
	// while (true) {
	// final Socket socket = serverSocket.accept();
	// log.info("new client" + socket);
	// TcpSession tcpSession = tcpSocketLlistener.buildTcpSession(socket);
	// if (tcpSession == null) {
	// log.warn("tcp session is null " + socket);
	// continue;
	// }
	// // if (newSessionBuilderServer == null) {
	// tcpSession.sessionBuilder = new NewSessionBuilderSimple(tcpSession);
	// // } else {
	// // tcpSession.sessionBuilder = newSessionBuilderServer;
	// // }
	// handleSocketNewThread(tcpSession);
	// }
	// }

	public static void handleSocketNewThread(final TelnetSession tcpSession) {
		Thread thread = new Thread("Socket "
				+ tcpSession.socket.getRemoteSocketAddress()) {
			@Override
			public void run() {
				try {
					handleSocket(tcpSession);
				} catch (Exception e) {
					if (tcpSession.isClosed()
							&& TelnetServerUtilsStatic.isBreakSocket(e)) {
						log.info(e);
					} else {
						log.error(null, e);
					}
					tcpSession.closeSession();

				}
			}
		};
		thread.start();
	}

	static int bufferSize = 1024;

	private static void handleSocket(TelnetSession tcpSession) throws Exception {
		log.debug("process new client");
		DGCMonitor.registerServices();
		try {
			// handleSocket2(tcpSession);
			//
			// }
			//
			// private static void handleSocket2(TcpSession tcpSession) throws
			// Exception {
			final byte[] buffer = new byte[bufferSize];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while (true) {
				// if(tcpSession.errCount>5) {
				// tcpSession.closeSession();
				// log.info("closing seeion by err count");
				// break;
				// }
				if (tcpSession.isClosed()) {
					// log.info("tcp session is closed");
					break;
				}
				int readBytes;
				readBytes = tcpSession.inputStream.read(buffer);
				if (readBytes == -1) {
					log.info("strange read bytes");
					synchronized (tcpSession.sendLock) {
						tcpSession.outputStream.write("test ignore"
								.getBytes("cp1251"));
						tcpSession.outputStream
								.write(TelnetServerUtilsStatic.sep.getBytes());
						tcpSession.outputStream.flush();
					}
					// Thread.sleep(10000);
					// tcpSession.errCount++;
					// try {
					// ClientSendRequest.getClientParams().waitResult = false;
					// tcpSession.sessionBuilder.getTestService().testCall("");
					// // log.info("end test call");
					// } catch (Exception e) {
					// log.info("test call", e);
					// }
				} else {
					out.write(buffer, 0, readBytes);
					out = checkNext(out, tcpSession);
				}
				// String s = new String(out.toByteArray(), "cp1251");
				// log.info(s);
			}
		} finally {
			tcpSession.closeSession();
		}
	}

	static byte newLineN = "\n".getBytes()[0];
	static byte newLineR = "\r".getBytes()[0];

	private static ByteArrayOutputStream checkNext(ByteArrayOutputStream out,
			TelnetSession tcpSession) throws Exception {
		if (tcpSession.isClosed()) {
			return null;
		}
		byte[] asBytes = out.toByteArray();
		// TelnetConsts type;
		// if(asBytes[0]==TelnetConsts.i.asByte) {
		// type=TelnetConsts.i;
		// }else if(asBytes[0]==TelnetConsts.r.asByte) {
		// type=TelnetConsts.r;
		// }else {
		// log.error("bad type "+new String(asBytes));
		// tcpSession.closeSession();
		// return null;
		// }
		String s = new String(asBytes, TimmosonSettings.defaultEncoding);
		int end = s.indexOf('\r');
		{
			int end2 = s.indexOf('\n');
			if (end2 != -1 && end2 < end) {
				end = end2;
			}
		}
		if (end == -1) {
			return out;
		}
		String thisInkoe = s.substring(0, end);
		log.info(thisInkoe);
		String otherInvoke = s.substring(end + 1);
		while (true) {
			if (otherInvoke.length() == 0) {
				break;
			}
			if (otherInvoke.charAt(0) == '\r' || otherInvoke.charAt(0) == '\n') {
				otherInvoke = otherInvoke.substring(1);
			} else {
				break;
			}
		}
		log.info(otherInvoke);
		// log.info(s);
		// log.info("");
		// if (type==TelnetConsts.i) {
		TelnetServerUtilsStatic.handleInvokation(tcpSession, thisInkoe);
		out = new ByteArrayOutputStream();
		out.write(otherInvoke.getBytes(TimmosonSettings.defaultEncoding));
		if (otherInvoke.length() == 0) {
			return out;
		}
		out = checkNext(out, tcpSession);
		// } else {
		// throw new NotImplementedException();
		// }
		return out;

	}

}
