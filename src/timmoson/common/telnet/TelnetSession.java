package timmoson.common.telnet;

import junit.framework.Assert;
import net.sf.jremoterun.JrrUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.client.RequestInfoCleint;
import timmoson.client.TcpSessionClosedListener;
import timmoson.client.telnet.TelnetTimmosonSessionStoreSimple;
import timmoson.common.transferedobjects.ReponseBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

//import timmoson.client.DGCMonitor;

public class TelnetSession {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public ArrayList<TcpSessionClosedListener> sessionClosedListeners = new ArrayList<TcpSessionClosedListener>();
	public Map<String, RequestInfoCleint> requets = new Hashtable();
	public boolean server = false;
	private volatile boolean isClosed = false;
	// public volatile int errCount = 0;
	/**
	 * used to create unique request id and counting requests
	 */
	public volatile int reqCount = 1;

	/**
	 * used to store find unique for object in session cache.
	 */
	public volatile int reqSeviceCount = 1;
	public volatile Socket socket;
	public volatile OutputStream outputStream;
	public volatile InputStream inputStream;
	// public Map<String, TcpServiceObject> serviceObjectsServer = new
	// HashMap();
	// public Map<String, WeakReference<TcpServiceObject>> serviceObjectsClient
	// = new HashMap();

	public volatile TelnetTimmosonSessionStoreSimple sessionBuilder;
	public final Object sendLock = new Object();


	private TelnetSession() {
	}



	public InetSocketAddress getInetSocketAddress() {
		java.net.InetSocketAddress socketAddress = (InetSocketAddress) socket
				.getRemoteSocketAddress();
		return socketAddress;
	}

	public boolean isClosed() {
		Assert.assertTrue(isClosed + "", isClosed == socket.isClosed());
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public void closeSession() {
		log.info("session closed");
		TelnetSession tcpSession = this;
		if (!tcpSession.isClosed()) {
			tcpSession.setClosed(true);
			try {
				tcpSession.socket.close();
			} catch (IOException e) {
				log.info(null, e);
			}
			try {
				tcpSession.inputStream.close();
			} catch (Exception e) {
				log.info(null, e);
			}
			try {
				tcpSession.outputStream.close();
			} catch (Exception e) {
				log.info(null, e);
			}
			if (!tcpSession.socket.isClosed()) {
				try {
					tcpSession.socket.shutdownInput();
				} catch (Exception e) {
					log.info(null, e);
				}
				try {
					tcpSession.socket.shutdownOutput();
				} catch (Exception e) {
					log.info(null, e);
				}
			}
			for (TcpSessionClosedListener closedListener : new ArrayList<TcpSessionClosedListener>(
					tcpSession.sessionClosedListeners)) {
				closedListener.sessionClosed();
			}
		}
		ArrayList<RequestInfoCleint> requestInfoCleints = new ArrayList(
				requets.values());
		for (RequestInfoCleint requestInfoCleint : requestInfoCleints) {
			synchronized (requestInfoCleint.lock) {
				requestInfoCleint.reponseBean = new ReponseBean();
				try {
					requestInfoCleint.reponseBean.exception = JrrUtils
							.serialize(new RuntimeException("Session is closed"));
				} catch (IOException e) {
					log.info(null, e);
				}
				requestInfoCleint.lock.notify();
			}
		}
		requets.clear();
	}

	// @Override
	// public <T> T makeClient(Class<T> class1, String serviceId) {
	// return ClientSendRequest.makeProxy(class1, sessionBuilder, serviceId);
	// }

	public static TelnetSession buildTcpSession(Socket socket)
			throws IOException {
		TelnetSession tcpSession = new TelnetSession();
		tcpSession.socket = socket;

		InputStream in = socket.getInputStream();
		tcpSession.inputStream = in;
		tcpSession.outputStream = socket.getOutputStream();
		return tcpSession;
	}
}
