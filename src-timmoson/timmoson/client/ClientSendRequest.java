package timmoson.client;

import junit.framework.Assert;
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.URLClassLoaderExt;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.JrrUtilities;
import net.sf.jremoterun.utilities.javassist.InvokcationAccessor;
import net.sf.jremoterun.utilities.javassist.JavassistProxyFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import timmoson.common.debug.TcpSessionNotifier;
import timmoson.common.debug.TcpSessionTrackerBean;
import timmoson.common.sertcp.Consts;
import timmoson.common.sertcp.MakeProxyDiffClassLoader;
import timmoson.common.sertcp.TcpSession;
import timmoson.common.transferedobjects.RemoteObjectClient;
import timmoson.common.transferedobjects.ServiceId;
import timmoson.common.transferedobjects.SessionServiceId;
import timmoson.common.transferedobjects.StaticServiceId;
import timmoson.server.ServiceCallServerInvoker;
import timmoson.server.ServiceSupport;
import timmoson.server.TcpServiceObject;
import timmoson.server.service.TestService;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ClientSendRequest {
	private static final Log log = LogFactory.getLog(JrrClassUtils.getCurrentClass());

	public static ThreadLocal<ClientParams> clientParams = new ThreadLocal();

	public static long  defaultWaitTime =10000;
	public static TcpSessionNotifier sessionNotifier;

	public static ClientParams getClientParams() {
		ClientParams clientParams = ClientSendRequest.clientParams.get();
		if (clientParams == null) {
			clientParams = new ClientParams();
			ClientSendRequest.clientParams.set(clientParams);
		}
		return clientParams;
	}

	// public static int serverPort = 2132;

	//
	// public static Object invokeMethod(RequestInfo requestInfo) throws
	// Exception {
	// Socket socket = new Socket("l", serverPort);
	// TcpSession session = new TcpSession();
	// requestInfo.tcpSession = session;
	// session.outputStream = socket.getOutputStream();
	// session.inputStream = socket.getInputStream();
	// return invokeMethod2(requestInfo);
	// }

	public static Map<ArrayList, TimmosonSessionStoreAndBuilder> sessionStoreCache = new HashMap();

	public static <T> T makeProxyForService(final Class<T> clazz, String host, int port) {
		ArrayList key = new ArrayList();
		key.add(host);
		key.add(port);
		TimmosonSessionStoreAndBuilder findAppSessionBuilder2 = sessionStoreCache.get(key);
		if (findAppSessionBuilder2 == null) {
			findAppSessionBuilder2 = new TimmosonSessionStoreAndBuilder(host, port);
			sessionStoreCache.put(key, findAppSessionBuilder2);
		}
		T findAppService2 = ClientSendRequest.makeProxyForService(clazz, findAppSessionBuilder2);
		return findAppService2;
	}

	public static <T> T makeProxyForService(final Class<T> clazz, TimmosonSessionStore tcpSession) {
		return makeProxy(clazz, tcpSession, clazz.getName(), false);
	}

	public static <T> T makeProxy(final Class<T> clazz, TimmosonSessionStore tcpSession, String serviceId) {
		return makeProxy(clazz, tcpSession, serviceId, false);
	}

	public static <T> T makeProxy(final Class<T> clazz, TimmosonSessionStore tcpSession, String serviceId,
			boolean sessionObject) {
		ServiceId serviceId2;
		if (sessionObject) {
			serviceId2 = new SessionServiceId(serviceId);
		} else {
			serviceId2 = new StaticServiceId(serviceId);
		}
		return makeProxy(clazz, tcpSession, serviceId2);
	}

	public static Method defineClassMethod;

	static {
		try {
			defineClassMethod = JrrClassUtils.findMethodByCount(ClassLoader.class, "defineClass", 3);
		} catch (NoSuchMethodException e) {
			log.warn(null, e);
			throw new Error(e);
		}
	}

	public static Class defineClass(ClassLoader classLoader, byte[] clazz)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return (Class) defineClassMethod.invoke(classLoader, new Object[] { clazz, 0, clazz.length });
	}

	public static <T> T makeProxy(final Class<T> clazz, TimmosonSessionStore tcpSession, ServiceId serviceId) {
		final URLClassLoaderExt classLoader2 = MakeProxyDiffClassLoader.findClassLoader(clazz.getClassLoader());
		// Thread.currentThread().setContextClassLoader(classLoader2);
		Class defineUsualClass;
		{
			try {
				Class<?> loadClass = classLoader2.loadClass(ProxyCallDiffClassloader.class.getName());
				if (loadClass == ProxyCallDiffClassloader.class) {
					Assert.assertEquals(classLoader2.loadClass(ProxyCall.class.getName()), ProxyCall.class);
					defineUsualClass = ProxyCall.class;
				} else {
					defineUsualClass = loadClass;
				}
			} catch (ClassNotFoundException e) {
				try {
					log.info("defining ProxyCallDiffClassloader class");
					defineUsualClass = classLoader2
							.defineClassPublic(JrrUtils.convertClassToBytes(ProxyCallDiffClassloader.class));
				} catch (IOException e1) {
					log.warn(null, e1);
					throw new Error(e1);
				}
			}

		}
		ProxyCallInvocation proxyCallInvocation = new ProxyCallInvocation(clazz, tcpSession, serviceId);
		JavassistProxyFactory f = new JavassistProxyFactory(proxyCallInvocation, true, classLoader2, clazz,
				defineUsualClass);
		T t;
		try {
			t = (T) f.createInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			log.warn(null, e);
			throw new Error(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			log.warn(null, e);
			throw new Error(e);
		}
		proxyCallInvocation.serviceObject22.service = t;
		// if (t instanceof ProxyCall) {
		// ProxyCall proxyCall = (ProxyCall) t;
		// proxyCall.getProxyCallInvocationObject().serviceObject22.service = t;
		// } else {
		// try {
		// Object invokeMethod = JrrClassUtils.invokeMethod(t,
		// "getProxyCallInvocationObject2");
		// Assert.assertEquals(invokeMethod, proxyCallInvocation);
		// } catch (Exception e) {
		// log.warn(clazz.getName(), e);
		// throw new Error(e);
		// }
		// }
		return t;

	}

	public static Object makeCall(RequestInfoCleint infoClient) throws Exception {
		ClientParams clientParams = (ClientParams) ClientSendRequest.getClientParams().clone();
		infoClient.clientParams = clientParams;
		return ClientParams.clientSendRequestDefault.makeCallNoThreadLocal(infoClient);
	}

	public Object makeCallNoThreadLocal(RequestInfoCleint infoClient) throws Exception {
		TcpSessionTrackerBean sessionTrackerBean = null;
		if (ClientSendRequest.sessionNotifier != null) {
			sessionTrackerBean = new TcpSessionTrackerBean();
			sessionTrackerBean.requestInfoCleint = infoClient;
			sessionTrackerBean.session = infoClient.tcpSession;
			// sessionTrackerBean.stackTrace = new Exception();
			ClientSendRequest.sessionNotifier.makeRequest(sessionTrackerBean);
		}
		infoClient.waitingThread = Thread.currentThread();
		synchronized (infoClient.tcpSession.sendLock) {
			infoClient.requestBean.requestId = infoClient.waitingThread.getId() + "-" + infoClient.tcpSession.reqCount;
			infoClient.tcpSession.reqCount++;
		}
		for (int i = 0; i < infoClient.params.length; i++) {
			Object param = infoClient.params[i];
			Class class1 = infoClient.method.getParameterTypes()[i];
			boolean isRef = ServiceCallServerInvoker.passByRef(param, class1);
			if (isRef) {
				TcpServiceObject tcpServiceObjects = ServiceCallServerInvoker
						.putObjectToSessionAsServerObject(infoClient.tcpSession, param);
				RemoteObjectClient objectClient = new RemoteObjectClient();
				objectClient.objectId = tcpServiceObjects.serviceInSessionId;
				objectClient.className = class1.getName();
				infoClient.params[i] = objectClient;
			}
		}
		{
			Class<?>[] parameterTypes = infoClient.method.getParameterTypes();
			List<String> paramTypes = new ArrayList();
			for (Class class1 : parameterTypes) {
				paramTypes.add(class1.getName());
			}

			infoClient.requestBean.classesNameParams = paramTypes;
		}
		infoClient.requestBean.params = JrrUtils.serialize(infoClient.params);
		infoClient.tcpSession.requets.put(infoClient.requestBean.requestId, infoClient);
		infoClient.requestBean.stackTrace = JrrUtils.serialize(new Exception("stack trace"));
		infoClient.requestBean.waitResult = infoClient.clientParams.waitResult;
		if (infoClient.clientParams.waitResult) {
			synchronized (infoClient.lock) {
				writeRequest(infoClient);
				while (true) {
					infoClient.lock.wait(defaultWaitTime);
					if (infoClient.tcpSession.isClosed()) {
						// requestInfoCleint.reponseBean = new ReponseBean();
						throw new RuntimeException("Session is closed");
					}
					if (infoClient.reponseBean != null) {
						// log.info("123");
						break;
					}
				}
			}
			if (sessionTrackerBean != null) {
				sessionTrackerBean.responseDate = new Date();
			}
			if (infoClient.reponseBean.resultNull) {
				return null;
			}
			if (infoClient.reponseBean.response != null) {
				ClassLoader deserClassLoader =infoClient.deserClassLoader;
				if(deserClassLoader ==null){
					deserClassLoader = ClientSendRequest.class.getClassLoader();
				}
				Object result = JrrUtils.deserialize(infoClient.reponseBean.response, deserClassLoader);
				if (sessionTrackerBean != null) {
					sessionTrackerBean.result = result;
				}
				return result;
			} else if (infoClient.reponseBean.serviceInSessionId != null) {
				// server return non serialable object, so server return id of
				// remote object.
				// Making wrapper around object
				Object result = ClientSendRequest.putObjectToClientSession(infoClient.method.getReturnType(),
						infoClient.tcpSession.sessionBuilder, infoClient.reponseBean.serviceInSessionId);
				Assert.assertFalse(ServiceSupport.class.isInstance(result));
				return result;
			}
			if (infoClient.reponseBean.exception == null) {
				throw new IllegalStateException("" + infoClient.reponseBean.resultNull);
			}
			Throwable exception = (Throwable) JrrUtils.deserialize(infoClient.reponseBean.exception,
					infoClient.deserClassLoader);
			if (sessionTrackerBean != null) {
				sessionTrackerBean.exception = exception;
			}
			Throwable throwable2 = newExceptionSameType(exception);
			JrrUtils.throwThrowable(throwable2);
			throw new Error();
		} else {
			synchronized (infoClient.lock) {
				writeRequest(infoClient);
			}
			return null;
		}
	}

	public static Throwable newExceptionSameType(Throwable throwable) {
		try {
			Throwable deserialize = (Throwable) JrrUtilities.serializeDesirialize(throwable,
					throwable.getClass().getClassLoader());
			deserialize.setStackTrace(new Exception().getStackTrace());
			deserialize.initCause(throwable);
			return deserialize;
		} catch (Exception e) {
			log.warn(throwable, e);
			throw new RuntimeException(throwable);
		}
		// return throwable;
	}

	// public static Object putObjectToCLientSession(RemoteObjectClient
	// remoteObjectClient) {
	// putObjectToCLientSession(remoteObjectServer., sessionBuilder, servisId)
	// }

	public static Object putObjectToClientSession(Class class1, TimmosonSessionStore sessionBuilder,
			ServiceId serviceId) {
		Object result = makeProxy(class1, sessionBuilder, serviceId);
		ProxyCall serviceObject = (ProxyCall) result;
		try {
			if (serviceId instanceof SessionServiceId) {
				SessionServiceId new_name = (SessionServiceId) serviceId;
				InvokcationAccessor serviceObject2 = (InvokcationAccessor) serviceObject;
				JavassistProxyFactory javassistProxyFactory = serviceObject2._getJavassistProxyFactory();
				ProxyCallInvocation methodHandler = (ProxyCallInvocation) javassistProxyFactory.getMethodHandler();
				sessionBuilder.getTcpSession().serviceObjectsClient.put(new_name.id,
						new WeakReference<TcpServiceObject>(methodHandler.serviceObject22));
			} else {
				log.info("serviceId is not instance SessionServiceId, serviceId class = " + serviceId.getClass());
				Assert.fail("serviceId is not instance SessionServiceId, serviceId class = " + serviceId.getClass());
			}
		} catch (Exception e) {
			log.info(null, e);
		}
		return result;
	}

	/**
	 * this method not use thread local
	 */
	public void writeRequest(RequestInfoCleint requestInfoCleint) throws Exception {
		requestInfoCleint.firstCallInSession = requestInfoCleint.tcpSession.reqCount == 2;
		requestInfoCleint.tryInvokactionCount++;
		try {
			writeRequest(requestInfoCleint, requestInfoCleint.tcpSession);
		} catch (Exception e) {
			if (requestInfoCleint.firstCallInSession || !requestInfoCleint.clientParams.retryIfIOException
					|| requestInfoCleint.tryInvokactionCount > 2) {
				throw e;
			} else if (requestInfoCleint.method.getDeclaringClass() == TestService.class
					&& "testCall".equals(requestInfoCleint.method.getName())) {
				log.warn("test call");
				throw e;
			} else {
				TcpSession tcpSession = requestInfoCleint.tcpSession.sessionBuilder.getTcpSession();
				requestInfoCleint.tcpSession = tcpSession;
				log.info(requestInfoCleint.tryInvokactionCount, e);
				writeRequest(requestInfoCleint, tcpSession);
			}
		}
	}

	/**
	 * this method not use thread local
	 */
	public void writeRequest(RequestInfoCleint requestInfoCleint, TcpSession session) throws Exception {
		if (session.isClosed()) {
			throw new IOException("session is closed");
		}
		byte[] paramsB = JrrUtils.serialize(requestInfoCleint.requestBean);
		try {
			synchronized (session.sendLock) {
				writeRequestInSyncBlock(requestInfoCleint, paramsB, session);
			}
		} catch (IOException e) {
			log.info(null, e);
			session.closeSession();
			throw e;
		}
	}

	public void writeRequestInSyncBlock(RequestInfoCleint requestInfoCleint, byte[] paramsB, TcpSession session)
			throws Exception {
		session.updateLastAccessWrite();
		// session.outputStream.write(" ".getBytes());
		session.outputStream.write(Consts.beginCallDesc.name().getBytes());
		session.outputStream.write(paramsB);
		session.outputStream.write(Consts.endCallDesc.name().getBytes());
		session.outputStream.flush();
	}

}
