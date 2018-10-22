package timmoson.server;

import junit.framework.Assert;
import net.sf.jremoterun.JrrUtils;
import org.apache.log4j.Logger;
import org.commonutils.CommonToString2;
import timmoson.client.ClientSendRequest;
import timmoson.common.sertcp.Consts;
import timmoson.common.sertcp.RemoteService;
import timmoson.common.sertcp.TcpSession;
import timmoson.common.transferedobjects.RemoteObjectClient;
import timmoson.common.transferedobjects.RemoteObjectServer;
import timmoson.common.transferedobjects.ReponseBean;
import timmoson.common.transferedobjects.RequestBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ServerUtilsStatic {
	private static final Logger log = Logger.getLogger(ServerUtilsStatic.class);

	public static Map<Class, Class> primitiveClassToWrapperMapping = new HashMap();
	public static Map<String, Class> primitiveClassToStringMapping = new HashMap();


	private static void mapping2() throws SecurityException,
			NoSuchMethodException {
		ResultSet rss = null;
		if (primitiveClassToWrapperMapping.size() == 0) {
			primitiveClassToWrapperMapping.put(int.class, Integer.class);
			primitiveClassToWrapperMapping.put(char.class, Character.class);
			primitiveClassToWrapperMapping.put(byte.class, Byte.class);
			primitiveClassToWrapperMapping.put(short.class, Short.class);
			primitiveClassToWrapperMapping.put(long.class, Long.class);
			primitiveClassToWrapperMapping.put(boolean.class, Boolean.class);
			primitiveClassToWrapperMapping.put(float.class, float.class);
			primitiveClassToWrapperMapping.put(double.class, Double.class);
			for (Class class1 : primitiveClassToWrapperMapping.keySet()) {
				primitiveClassToStringMapping.put(class1.getName(), class1);
			}
			log.debug(primitiveClassToStringMapping);
		}
	}
	static {
		try {
			mapping2();
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	public static byte[] handleInvokation(TcpSession tcpSession,
			ByteArrayOutputStream out, int i, String s) throws Exception {
		String s2 = s
				.substring(Consts.beginCallDesc.name().length() + 1, i - 1);
		log.debug(s2);
		// Properties properties = new Properties();
		// properties.load(new ByteArrayInputStream(s2.getBytes()));
		// String methodName = properties.getProperty(Consts.methodName.name());
		// String requestId = properties.getProperty(Consts.requestId.name());
		// int parsmNumber = Integer.parseInt(properties
		// .getProperty(Consts.paramsNumber.name()));
		// Object serviceId = properties.getProperty(Consts.serviceId.name());
		tcpSession.updateLastAccessRead();
		byte[][] bb23 = RemoteService.defaultRemoteService.findBetween(Consts.beginCallDesc.name(),
				Consts.endCallDesc.name(), tcpSession.inputStream, out);
		byte[] bb2 = bb23[0];
		RequestBean requestBean = (RequestBean) JrrUtils.deserialize(bb2,
				RemoteService.class.getClassLoader());
		try {
			ServiceSupport seriveObject = tcpSession.getServiceObject(
					requestBean.serviceId);
			TcpCallInfoServer tcpCallInfo = new TcpCallInfoServer();
			tcpCallInfo.serviceSupport = seriveObject;
			// tcpCallInfo.service = seriveObject;
			tcpCallInfo.requestBean = requestBean;
			tcpCallInfo.tcpSession = tcpSession;
			ServerUtilsStatic.handleInvokationNewThread(tcpCallInfo);
		} catch (Exception e) {
			log.info(
					requestBean.methodName + " "
							+ CommonToString2.toString(requestBean.params), e);
			ReponseBean reponseBean = new ReponseBean();
			reponseBean.requestId = requestBean.requestId;
			reponseBean.exception = JrrUtils.serialize(e);
			ServerUtilsStatic.writeResponse(reponseBean, tcpSession);
		}
		return bb23[1];
	}

	public static void handleInvokation(TcpCallInfoServer callInfo)
			throws Exception {
		RequestBean requestBean = callInfo.requestBean;
		Object[] params = (Object[]) JrrUtils.deserialize(requestBean.params,
				callInfo.serviceSupport.service.getClass().getClassLoader());
		for (int i = 0; i < params.length; i++) {
			Object object = params[i];
			if (object instanceof RemoteObjectServer) {
				RemoteObjectServer remoteObject = (RemoteObjectServer) object;
				ServiceSupport service = callInfo.tcpSession.getServiceObject(
						remoteObject.objectId);
				params[i] = service.service;
			}
			if (object instanceof RemoteObjectClient) {
				RemoteObjectClient remoteObjectClient = (RemoteObjectClient) object;
				Class paramClass = Class.forName(remoteObjectClient.className,
						true, callInfo.serviceSupport.service.getClass()
								.getClassLoader());
				Object param = ClientSendRequest.putObjectToClientSession(
						paramClass, callInfo.tcpSession.sessionBuilder,
						remoteObjectClient.objectId);
				Assert.assertFalse(ServiceSupport.class.isInstance(param));
				params[i] = param;
			}
		}
		// log.info(Arrays.asList(params));
		// log.info(callInfo.service.getClass())
		// log.info(callInfo.serviceObject.getClass()+" "+requestBean.methodName+" "+params.length);
		Class[] paramType = new Class[callInfo.requestBean.classesNameParams
				.size()];
		{
			int rr = 0;
			for (String className : callInfo.requestBean.classesNameParams) {
				Class loadClass = primitiveClassToStringMapping.get(className);
				if (loadClass == null) {
					loadClass=callInfo.serviceSupport.service.getClass().getClassLoader()
							.loadClass(className);
				}
				paramType[rr] = loadClass;
				rr++;
			}
		}
		Method method = callInfo.serviceSupport.service.getClass().getMethod(
				requestBean.methodName, paramType);
		method.setAccessible(true);
		// tcpCallInfo.requestId=requestInfo.requestId;
		callInfo.params = params;
		callInfo.method = method;
		GetReponseHandler.reponseHandler.getServiceCallServerInvoker(callInfo)
				.makeCall(callInfo);
	}

	public static void handleInvokationNewThread(
			final TcpCallInfoServer callInfo) throws Exception {
		log.debug(callInfo.tcpSession);
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					handleInvokation(callInfo);
				} catch (Exception e) {
					String addString = callInfo.requestBean.methodName
							+ " "
							+ callInfo.serviceSupport.service.getClass()
									.getName() + " "
							+ callInfo.requestBean.serviceId + " ";
					addString += callInfo.requestBean.readRemoteStackToString();
					log.info(addString, e);
				}
			}
		};
		thread.start();
	}

	public static void writeResponse(ReponseBean reponseBean,
			TcpSession tcpSession) throws IOException {
		byte[] res = JrrUtils.serialize(reponseBean);
		try {
			tcpSession.updateLastAccessWrite();
			synchronized (tcpSession.sendLock) {
				tcpSession.outputStream.write(Consts.resultBegin.name()
						.getBytes());
				tcpSession.outputStream.write(res);
				tcpSession.outputStream.write(Consts.resultEnd.name()
						.getBytes());
				tcpSession.outputStream.flush();
			}
		} catch (IOException e) {
			log.info(null, e);
			tcpSession.closeSession();
		}
	}
}
