package timmoson.common.remoterun;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.RemoteRunner;
import net.sf.jremoterun.jrrlauncher.*;
import net.sf.jremoterun.mbeans.Runner;
import net.sf.jremoterun.mbeans.RunnerMBean;
import net.sf.jremoterun.utilities.MBeanClient;
import net.sf.jremoterun.utilities.MbeanConnectionCreator;
import net.sf.jremoterun.utilities.nonjdk.log.threadfilter.IfAppender;
import net.sf.jremoterun.utilities.nonjdk.log.threadfilter.Log4jAppenderListener;
import org.apache.logging.log4j.LogManager;
import timmoson.client.TimmosonSessionStore;
import timmoson.client.TimmosonSessionStoreAndBuilder;
import timmoson.client.TimmosonSessionStoreSimple;
import timmoson.common.sertcp.MakeProxyDiffClassLoader;
import timmoson.common.sertcp.RemoteService;
import timmoson.common.sertcp.TcpSession;
import timmoson.server.ServiceLocator;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.rmi.ConnectException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class InvokactionRemoreRun extends JrrCommonLauncher {

	private static final Logger log = Logger.getLogger(JrrJmxRmiLauncher.class.getName());

	// valid on client and server side
	public volatile TcpSession session;

	protected String localHostIp;
	protected Boolean jmxPort;

	protected IfAppender appenderListener;

	/**
	 * Store params and configuration which transfer from local to remote
	 * system.
	 */
	protected JmxRmiParamsForRemoteSystem params = new JmxRmiParamsForRemoteSystem();

	@Override
	protected void jrrClean() {
		super.jrrClean();
		// may clear user bean and result bean
		log.fine("clean");
		params = new JmxRmiParamsForRemoteSystem();
		session = null;
		// sessionBuilder = null;
	}

	@Override
	protected void jrrBuildSetting() throws MalformedURLException {
		getParams().jrrLibraryPath = jrrTranslateURL(JrrUtils.getClassLocation(RunnerMBean.class));
		super.jrrBuildSetting();
	}

	@Override
	public JmxRmiParamsForRemoteSystem getParams() {
		return params;
	}

	@Override
	protected void jrrRunBefore() throws Exception {
		super.jrrRunBefore();
		if (session == null) {
			jrrFindSession();
		}
		jrrInitLoggerTransferer();

	}

	protected void jrrInitLoggerTransferer() throws Exception {
		appenderListener = new IfAppender(session);
		appenderListener.loggingLogger.add(getClass().getName());
		org.apache.logging.log4j.core.Logger rootLogger = (org.apache.logging.log4j.core.Logger) LogManager
				.getRootLogger();
		rootLogger.addAppender(appenderListener);
	}

	protected void jrrFindSession() throws Exception {
		Object session22 = MakeProxyDiffClassLoader.findSession();
		if (session22 instanceof TcpSession) {
			session = (TcpSession) session22;
		} else {
			session = MakeProxyDiffClassLoader.makeProxyForFromDiffClassLoader(session22, TcpSession.class);
		}
	}

	@Override
	public void setParams(final ParamsForRemoteSystem3 params) {
		this.params = (JmxRmiParamsForRemoteSystem) params;
	}

	@Override
	protected boolean jrrValidate() {
		if (getParams().host == null) {
			System.out.println("jmx jmxHost is not defined");
			return false;
		} else if (getParams().port == -1) {
			System.out.println("jmx jmxPort is not defined");
			return false;
		}
		return true;
	}

	/**
	 * run code from client side
	 */
	@Override
	protected void jrrRunRemoteCode2(final List params2) throws Exception {
		if (getParams().host == null) {
			getParams().host = "127.0.0.1";
		}
		if (jmxPort == null) {
			throw new Exception("specify port type");
		}
		log.fine(getParams().host + ":" + getParams().port);

		if (jmxPort) {
			if (localHostIp == null) {
				throw new Exception("specify localHostIp");
			}
			MbeanConnectionCreator connectionCreator = new MbeanConnectionCreator(
					getParams().host + ":" + getParams().port);
			if (connectionCreator.getMBeanServerConnection().isRegistered(IfRegister.objectName)) {

			} else {
				// public static Serializable remoteRun(final String address,
				// final String user, final String password,
				// final String codeForExecuting, final URL jremoteRunLib,
				// final Collection<URL> classpath,
				// final List<? extends Serializable> params,
				// final Serializable parentClassLoaderId) throws Exception {
				log.info("registering mbean");
				log.info(getParams().jrrLibraryPath + "");
				// ArrayList classPath22=new ArrayList<URL>(jrrClasspath);
				// log.info(getParams().urls+"");
				RemoteRunner.remoteRun(getParams().host + ":" + getParams().port, null, null,
						IfRegister.class.getName(), getParams().jrrLibraryPath, getParams().urls, null, null);
			}
			final Object lock = new Object();
			log.info("invoke start");
			IfRegister ifRunner = MBeanClient.buildMbeanClient(IfRegister.class, connectionCreator,
					IfRegister.objectName);
			final ServerSocket serverSocket = findFreeTcpPort(21321);
			Thread thread = new Thread() {

				public void run() {
					try {
						Socket socket;
						socket = serverSocket.accept();
						log.info("new client" + socket);
						session = TcpSession.buildTcpSession(socket);
						// if (newSessionBuilderServer == null) {
						session.sessionBuilder = new TimmosonSessionStoreSimple(session);
						// } else {
						// tcpSession.sessionBuilder = newSessionBuilderServer;
						// }
						RemoteService.handleSocketNewThread(session);
						synchronized (lock) {
							lock.notify();
						}
					} catch (Exception e) {
						log.log(Level.SEVERE, null, e);
					}
				}
			};
			thread.start();
			ifRunner.connectToHost(localHostIp, serverSocket.getLocalPort());
			synchronized (lock) {
				if (session == null) {
					log.info("waiting session");
					lock.wait();
				}
			}
			log.info("session found " + session);
			// TimmosonSessionStoreSimple sessionBuilder = new
			// TimmosonSessionStoreSimple(session);
		} else {
			TimmosonSessionStore sessionBuilder;
			sessionBuilder = new TimmosonSessionStoreAndBuilder(getParams().host, getParams().port);
			session = sessionBuilder.getTcpSession();
		}
		Runner runner = session.makeClient(Runner.class, Runner.class.getName());
		// log.info(getParams().jrrLibraryPath + "");
		final URL[] arrayClassPath = new URL[] { getParams().jrrLibraryPath };
		// if (jrrClasspath == null) {
		// arrayClassPath = JrrUtils.emptyUrlArray;
		// } else {
		// arrayClassPath = jrrClasspath.toArray(new URL[0]);
		// }
		registerServices();
		jrrResult = (Serializable) runner.runCodeFromJar(JrrLauncherClassLoader.class.getName(), params2,
				arrayClassPath, getParams().classLoaderId);
		log.finest("result " + jrrResult);
	}

	public static void registerServices() {
		ServiceLocator.regNewService(Log4jAppenderListener.class, new Log4jAppenderListener());
	}

	/**
	 * @return free tcp port starting searching from initialPort.
	 */
	public static ServerSocket findFreeTcpPort(final int initialPort) throws IOException {
		try {
			return new ServerSocket(initialPort);
		} catch (final BindException e) {
			return findFreeTcpPort(initialPort + 1);
		}
	}

	@Override
	protected void jrrPrintException(final Throwable exc) {
		jrrPrintNavigation();
		if (exc instanceof ConnectException) {
			System.out.println("jmx address " + getParams().host + ":" + getParams().port);
			System.out.println(exc);
		} else {
			final String excS = JrrUtils.exceptionRevertStackTrace(exc);
			System.out.println(excS);
		}
	}

	@Override
	protected void jrrRunAfterAlways() {
		super.jrrRunAfterAlways();
		jrrDeInitLoggerTransferer();
	}

	protected void jrrDeInitLoggerTransferer() {
		if (appenderListener != null) {
			org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) LogManager
					.getRootLogger();
			logger.removeAppender(appenderListener);
		}
	}

	protected void jrrRunRemoteCode() {
		super.jrrRunRemoteCode();
		// log.info("exiting");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.severe(e + "");
		}
		// session.closeSession();
		System.exit(0);
	}

	protected static void jrrRunRemoteCodeStaticWithExit() {
		JrrCommonLauncher.jrrRunRemoteCodeStatic();
		log.info("exiting");
		System.exit(0);
	}

	public void log(Object msg) {
		StringBuilder sb = new StringBuilder();
		Exception exception = new Exception();
		StackTraceElement stackTraceElement = exception.getStackTrace()[1];
		sb.append(stackTraceElement);
		sb.append(" - ");
		sb.append(msg);
		appenderListener.appenderListener.add(sb.toString());
	}

}
