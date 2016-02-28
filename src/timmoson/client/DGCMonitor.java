package timmoson.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.common.sertcp.TcpSession;
import timmoson.server.ServiceLocator;
import timmoson.server.TcpServiceObject;
import timmoson.server.service.TcpService;
import timmoson.server.service.TestService;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.WeakHashMap;

public class DGCMonitor {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public static WeakHashMap<TcpSession, Object> tcpSessions = new WeakHashMap();
	private static boolean isRun = false;

	public volatile static boolean servicesRegisterd = false;

	public static void registerServices() {
		if (!servicesRegisterd) {
			ServiceLocator.regNewService(new TestService());
			servicesRegisterd = true;
		}
	}

	public static synchronized void runMonitorInNewThread() {
		if (isRun) {
			return;
		}
		isRun = true;
		Thread thread = new Thread() {
			@Override
			public void run() {
				f1();
			}
		};
		DGCMonitor. registerServices();
		thread.setDaemon(true);
		thread.start();
	}

	private static void f1() {
		while (true) {

			Collection<TcpSession> tcpSessions2 = new LinkedList(
					tcpSessions.keySet());
			for (TcpSession tcpSession : tcpSessions2) {
				// if(tcpSession.socket!=null
				Collection<Entry<String, WeakReference<TcpServiceObject>>> set;
				try {
					set = new LinkedList(
							tcpSession.serviceObjectsClient.entrySet());
				} catch (ConcurrentModificationException e) {
					log.info("known issue", e);
					continue;
				}
				for (Entry<String, WeakReference<TcpServiceObject>> entry : set) {
					if (entry.getValue().get() == null) {
						log.info("removing ref " + entry.getKey());
						ClientSendRequest.getClientParams().overrideTcpSession = tcpSession;
						TcpService.tcpServiceRemote
								.removeLocalSessionObject(entry.getKey());
						if (tcpSession.serviceObjectsClient.remove(entry
								.getKey()) == null) {
							log.info("stange remove " + entry.getKey());
						}
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.info(null, e);
			}
		}
	}

}
