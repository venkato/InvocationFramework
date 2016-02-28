package timmoson.common.sertcp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.client.ClientSendRequest;
import timmoson.client.ClientStaticUtils;
import timmoson.client.DGCMonitor;
import timmoson.common.CallInfoServer;
import timmoson.server.ServerUtilsStatic;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class RemoteService {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	// public static NewSessionBuilder newSessionBuilderServer=new
	// NewSessionBuilderSimple(
	// tcpSession);

	public static boolean writeStrangeBytes = true;
	public static ThreadLocal<CallInfoServer> callsInfos = IfSharedObjects.getCallInfo();

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

	public static void handleSocketNewThread(final TcpSession tcpSession) {
		Thread thread = new Thread("Socket "
				+ tcpSession.socket.getRemoteSocketAddress()) {
			@Override
			public void run() {
				try {
					handleSocket(tcpSession);
				} catch (Exception e) {
					if (tcpSession.isClosed()
							&& e instanceof java.net.SocketException
							&& "Connection reset".equals(e.getMessage())) {

					} else {
						log.warn(null, e);
						// }
					}
				}
			}
		};
		thread.start();
	}

	static int bufferSize = 1024;

//	public static final byte[] beginCallDescB = Consts.beginCallDesc.name()
//			.getBytes();
//	public static final byte[] endCallDescB = Consts.endCallDesc.name()
//			.getBytes();

	private static void handleSocket(TcpSession tcpSession) throws Exception {
		log.debug("process new client");
		DGCMonitor. registerServices();
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
					if(writeStrangeBytes) {
						log.info("strange read bytes");
					}
					// Thread.sleep(10000);
					// tcpSession.errCount++;
					try {
						ClientSendRequest.getClientParams().waitResult = false;
						tcpSession.sessionBuilder.getTestService().testCall("");
						// log.info("end test call");
					} catch (Exception e) {
						if(writeStrangeBytes) {
							log.info("test call", e);
						}
					}
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

	private static ByteArrayOutputStream checkNext(ByteArrayOutputStream out,
			TcpSession tcpSession) throws Exception {
		if(tcpSession.isClosed()) {
			return null;
		}
		String s = new String(out.toByteArray(), "cp1251");
		// log.info(s);
		int i = s.indexOf(Consts.endCallDesc.name());
		int k = s.indexOf(Consts.resultEnd.name());
		// log.info("");
		if (i != -1 && (k == -1 || i < k)) {
			byte[] remain = ServerUtilsStatic.handleInvokation(tcpSession, out,
					i, s);
			out = new ByteArrayOutputStream();
			out.write(remain);
			out = checkNext(out, tcpSession);
		} else if (k != -1) {
			byte[] remain = ClientStaticUtils.handleClientReponse(tcpSession,
					out, k, s);
			out = new ByteArrayOutputStream();
			out.write(remain);
			out = checkNext(out, tcpSession);
		}
		return out;
	}

	public static int waitWord(InputStream in, ByteArrayOutputStream out,
			String word) throws Exception {
		final byte[] buffer = new byte[bufferSize];
		int readBytes;
		while (true) {
			String s = new String(out.toByteArray(), "cp1251");
			log.debug(s);
			int i = s.indexOf(word);
			if (i != -1) {
				return i;
			}
			readBytes = in.read(buffer);
			out.write(buffer, 0, readBytes);
		}
		// throw new Exception("end stream");
	}

	public static byte[][] findBetween(String begin, String end,
			InputStream in, ByteArrayOutputStream out) throws Exception {

		int k = waitWord(in, out, end);

		byte[] bb = out.toByteArray();
		String dsfd = new String(bb, "cp1251");
		log.debug(dsfd);
		int i = dsfd.indexOf(begin);
		if (i == -1) {
			throw new Exception(begin + " not found in " + dsfd);
		}
		int beginI = i + begin.length() + 1;
		int endI = k;
		int diff = endI - beginI;
		byte[] bb2 = new byte[diff + 1];

		log.debug(beginI);
		log.debug(endI);
		log.debug(new String(bb, beginI, diff + 1, "cp1251"));
		System.arraycopy(bb, beginI - 1, bb2, 0, bb2.length);
		log.debug(new String(bb2));
		byte[][] bbbw = new byte[2][];
		bbbw[0] = bb2;
		byte[] remain = new byte[bb.length - endI - end.length()];

		System.arraycopy(bb, endI + end.length(), remain, 0, remain.length);
		bbbw[1] = remain;
		log.debug(remain.length + " " + new String(remain));
		return bbbw;

	}

}
