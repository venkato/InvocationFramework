package timmoson.common.sertcp;

import junit.framework.Assert;
import net.sf.jremoterun.JrrUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.client.*;
import timmoson.common.CallBackSession;
import timmoson.common.transferedobjects.ReponseBean;
import timmoson.common.transferedobjects.ServiceId;
import timmoson.server.ServiceLocator;
import timmoson.server.ServiceSupport;
import timmoson.server.TcpServiceObject;
import timmoson.server.TcpSocketLlistener;
import timmoson.server.service.TcpService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpSession extends CallBackSession {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public ArrayList<TcpSessionClosedListener> sessionClosedListeners = new ArrayList<TcpSessionClosedListener>();
	public Map<String, RequestInfoCleint> requets = new Hashtable();
	public boolean server = false;
	private volatile boolean isClosed = false;
	
//	public static WeakHashMap<TcpSession, Object> tcpSessions= new WeakHashMap();
	
	// public volatile int errCount = 0;
	/**
	 * used to create unique request id and counting requests
	 */
	public volatile int reqCount = 1;

	// public boolean sideId;

	/**
	 * used to store find unique for object in session cache.
	 */
	public volatile AtomicInteger reqSeviceCount = new AtomicInteger(1);
	public volatile Socket socket;
	public volatile OutputStream outputStream;
	public volatile InputStream inputStream;
	public Map<String, TcpServiceObject> serviceObjectsServer = new HashMap();
	public Map<String, WeakReference<TcpServiceObject>> serviceObjectsClient = new HashMap();

	public volatile TimmosonSessionStore sessionBuilder;
	public final Object sendLock = new Object();

	private volatile static boolean initiedStatic = false;

	@Deprecated
	public  TcpSession() {		
		if (!initiedStatic) {
			initStatic();
		}
	}

	public ServiceSupport getServiceObject(ServiceId serviceId)
			throws Exception {
		ServiceSupport object = serviceId.find(this);
		if (object == null) {
			throw new Exception("bad service " + serviceId);
		}
		return object;
	}

	public static void initStatic() {
		if (!initiedStatic) {
			initiedStatic = true;

			DGCMonitor.registerServices();
			ServiceLocator.regNewService(TcpService.serviceId,
					TcpService.tcpServiceLocal);
			DGCMonitor.runMonitorInNewThread();
		}
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
		log.debug("session closed");
		TcpSession tcpSession = this;
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

	@Override
	public <T> T makeClient(Class<T> class1, String serviceId) {
		return ClientSendRequest.makeProxy(class1, sessionBuilder, serviceId);
	}

	public static TcpSession buildTcpSession(Socket socket) throws IOException {
		TcpSession tcpSession = new TcpSession();
		tcpSession.socket = socket;

		InputStream in = socket.getInputStream();
		tcpSession.inputStream = in;
		tcpSession.outputStream = socket.getOutputStream();
		// tcpSession.sideId=sideId;
//		tcpSessions.put(tcpSession, TcpSocketLlistener.object);
		DGCMonitor.tcpSessions.put(tcpSession, TcpSocketLlistener.object);
		return tcpSession;
	}
}
