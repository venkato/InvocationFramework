package timmoson.server.telnet;

import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.log4j.Logger;
import org.commonutils.CommonToString2;
import timmoson.common.sertcp.TimmosonSettings;
import timmoson.common.telnet.TelnetSession;
import timmoson.server.ServerUtilsStatic;
import timmoson.server.ServiceLocator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.StringTokenizer;

public class TelnetServerUtilsStatic {
	private static final Logger log = Logger
			.getLogger(TelnetServerUtilsStatic.class);

	public static void handleInvokation(TelnetSession tcpSession, String s)
			throws Exception {
		// TelnetConsts tc=
		log.debug(s);
		// Properties properties = new Properties();
		// properties.load(new ByteArrayInputStream(s2.getBytes()));
		// String methodName = properties.getProperty(Consts.methodName.name());
		// String requestId = properties.getProperty(Consts.requestId.name());
		// int parsmNumber = Integer.parseInt(properties
		// .getProperty(Consts.paramsNumber.name()));
		// Object serviceId = properties.getProperty(Consts.serviceId.name());
		TelnetRequestBean requestBean = new TelnetRequestBean();
		StringTokenizer st = new StringTokenizer(s, " ");
		if (!st.hasMoreElements()) {
			writeResponse("usage [serviceid] [methodName] [param1] [param2]",
					tcpSession);
			return;
		}
		requestBean.serviceId = st.nextToken();
		if (!st.hasMoreElements()) {
			writeResponse("specify method name, usage: [serviceid] [methodName] [param1] [param2]", tcpSession);
			return;
		}
		requestBean.methodName = st.nextToken();
		while (st.hasMoreElements()) {
			requestBean.params.add(st.nextToken());
		}
		try {
			requestBean.seriveObject = ServiceLocator
					.getService(requestBean.serviceId);
			requestBean.telnetSession = tcpSession;
			try {
				requestBean.method = JrrClassUtils.findMethodByCount(
						requestBean.seriveObject.service.getClass(),
						requestBean.methodName, requestBean.params.size());
			} catch (NoSuchMethodException e) {
				log.info(e);
				Field detailMessageField = JrrClassUtils.findField(
						e.getClass(), "detailMessage");
				String msg = (String) detailMessageField.get(e);
				msg += " class="
						+ requestBean.seriveObject.service.getClass().getName();
				detailMessageField.set(e, msg);
				throw e;
			}
			requestBean.paramsAsObject = new Object[requestBean.params.size()];
			int i = 0;
			for (String paramString : requestBean.params) {
				Class class1 = requestBean.method.getParameterTypes()[i];
				if (class1.isPrimitive()) {
					class1 = ServerUtilsStatic.primitiveClassToWrapperMapping
							.get(class1);
				}
				requestBean.paramsAsObject[i] = class1.getConstructor(
						String.class).newInstance(paramString);
				i++;
			}
			TelnetServerUtilsStatic.handleInvokationNewThread(requestBean);
		} catch (Exception e) {
			log.info(
					requestBean.methodName + " "
							+ CommonToString2.toString(requestBean.params), e);
			TelnetServerUtilsStatic.writeResponse(e, tcpSession);
		}
	}

	public static void handleInvokationNewThread(
			final TelnetRequestBean requestBean) throws Exception {
		log.debug(requestBean.telnetSession);
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Object result = requestBean.method.invoke(
							requestBean.seriveObject.service,
							requestBean.paramsAsObject);
					if (requestBean.telnetSession.isClosed()) {
						log.info("session is closed");
					} else {
						writeResponse(result, requestBean.telnetSession);
					}
				} catch (Exception e) {
					StringBuilder sb = new StringBuilder();
					sb.append(requestBean.methodName);
					sb.append(" ");
					sb.append(requestBean.serviceId);
					sb.append(" ");
					log.info(sb, e);
					sb.append(e);
					try {
						writeResponse(sb, requestBean.telnetSession);
					} catch (IOException e1) {
						log.warn(sb, e1);
					}
				}
			}
		};
		thread.start();
	}

	public static String sep = "\r\n";

	public static void writeResponse(Object reponse, TelnetSession tcpSession)
			throws IOException {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("result ");
			sb.append(reponse);
			sb.append(sep);
			synchronized (tcpSession.sendLock) {
				tcpSession.outputStream.write(sb.toString().getBytes(TimmosonSettings.defaultEncoding));
				tcpSession.outputStream.flush();
			}
		} catch (IOException e) {
			if (TelnetServerUtilsStatic.isBreakSocket(e)) {
				log.info(e);
			} else {
				log.error(null, e);
			}
			tcpSession.closeSession();
		}
	}

	public static boolean isBreakSocket(Exception e) {
		return "Software caused connection abort: socket write error".equals(e
				.getMessage()) || "Connection reset".equals(e.getMessage());
	}
}
