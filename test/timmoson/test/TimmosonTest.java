package timmoson.test;

import java.io.InputStream;
import java.lang.management.MemoryMXBean;
import java.net.URLClassLoader;

import javax.management.ObjectName;

import junit.framework.Assert;
import junit.framework.TestCase;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.URLClassLoaderExt;
import net.sf.jremoterun.utilities.Java5VM;
import net.sf.jremoterun.utilities.JrrClassUtils;

import org.apache.log4j.Logger;

import timmoson.client.ClientSendRequest;
import timmoson.client.TimmosonSessionStoreAndBuilder;
import timmoson.common.sertcp.IfSharedObjects;
import timmoson.common.sertcp.MakeProxyDiffClassLoader;
import timmoson.common.transferedobjects.JmxServiceId;
import timmoson.server.ServiceLocator;
import timmoson.server.TcpSocketLlistener;
import timmoson.server.service.DefaultService;
import timmoson.testservice.CallbackService;
import timmoson.testservice.SampleService;

public class TimmosonTest extends TestCase {
	private static final Logger logger = Logger.getLogger(TimmosonTest.class);
	private static final Logger log = logger;

	public void testServiceDiffClassloader() throws Exception {
		log.info("stariing");
		URLClassLoader urlClassLoader = JrrClassUtils.getCurrentClassLoaderUrl();
		URLClassLoaderExt classLoaderExt = new URLClassLoaderExt(urlClassLoader.getURLs(), null);
		Class classServiceRegister = classLoaderExt.loadClass(DiffClassloaderServiceRegister.class.getName());
		Assert.assertNotSame(classLoaderExt, DiffClassloaderServiceRegister.class);
		JrrClassUtils.invokeJavaMethod(classServiceRegister.newInstance(), "run", null, null);
		Object service = IfSharedObjects.services.get(SampleService.class.getName());
		Assert.assertFalse(service.getClass() == SampleService.class);
		Assert.assertNotSame(service.getClass(), SampleService.class);
		int tcpPort = 22123;
		ServiceLocator.regNewService(new CallbackService());
		TcpSocketLlistener socketLlistener = new TcpSocketLlistener(tcpPort);
		socketLlistener.startListenerInNewThread();
		TimmosonSessionStoreAndBuilder findAppSessionBuilder2 = new TimmosonSessionStoreAndBuilder("127.0.0.1", tcpPort);
		SampleService sampleService = ClientSendRequest.makeProxyForService(SampleService.class,
				findAppSessionBuilder2);
		String test1 = sampleService.test2();
		assertEquals(test1, SampleService.strResturn);
		try {
			sampleService.testThrowExc();
		} catch (Exception e) {
			// log.info(null,e);
		}
		assertEquals(sampleService.add2(3), 5);
		assertEquals(sampleService.add2Array(new int[] { 3 }), 5);
		assertEquals(sampleService.add3Array(new Integer[] { 3 }), 5);
		assertEquals(sampleService.testCallbackDiffClassloader(), SampleService.strResturn);
		log.info("finish");
	}

	public void testClassLoaderProxy() throws Exception {
		URLClassLoader urlClassLoader = JrrClassUtils.getCurrentClassLoaderUrl();
		URLClassLoaderExt classLoaderExt = new URLClassLoaderExt(urlClassLoader.getURLs(), null);
		Class classServiceRegister = classLoaderExt.loadClass(DiffClassloaderServiceRegister.class.getName());
		Assert.assertNotSame(classLoaderExt, DiffClassloaderServiceRegister.class);
		JrrClassUtils.invokeJavaMethod(classServiceRegister.newInstance(), "run", null, null);
		Object service = IfSharedObjects.services.get(SampleService.class.getName());
		Assert.assertFalse(service.getClass() == SampleService.class);
		Assert.assertNotSame(service.getClass(), SampleService.class);
		SampleService sampleService2 = MakeProxyDiffClassLoader.makeProxyForFromDiffClassLoader(service,
				SampleService.class);
		assertEquals(sampleService2.add2(3), 5);
		assertEquals(sampleService2.add2Array(new int[] { 3 }), 5);
		assertEquals(sampleService2.add3Array(new Integer[] { 3 }), 5);
	}

	public void testService() throws Exception {
		log.info("stariing");
		int tcpPort = 32123;
		ServiceLocator.regNewService(new SampleService());
		ServiceLocator.regNewService(new CallbackService());
		TcpSocketLlistener socketLlistener = new TcpSocketLlistener(tcpPort);
		socketLlistener.startListenerInNewThread();
		TimmosonSessionStoreAndBuilder findAppSessionBuilder2 = new TimmosonSessionStoreAndBuilder("127.0.0.1", tcpPort);
		SampleService sampleService = ClientSendRequest.makeProxyForService(SampleService.class,
				findAppSessionBuilder2);
		String test1 = sampleService.test2();
		assertEquals(test1, SampleService.strResturn);
		try {
			sampleService.testThrowExc();
			fail("exception was not thrown");
		} catch (Exception e) {
			// log.info(null,e);
		}
		assertEquals(sampleService.add2(3), 5);
		assertEquals(sampleService.add2Array(new int[] { 3 }), 5);
		assertEquals(sampleService.add3Array(new Integer[] { 3 }), 5);
		assertEquals(sampleService.testCallback(), SampleService.strResturn);
		assertEquals(sampleService.testCallbackDiffClassloader(), SampleService.strResturn);
		socketLlistener.stop();
		log.info("finish");
	}

	public void testService2() throws Exception {
		log.info("stariing");
		int tcpPort = 32123;
		ServiceLocator.regNewService(new CallbackService());
		ServiceLocator.regNewService(DefaultService.class, DefaultService.tcpServiceLocal);
		TcpSocketLlistener socketLlistener = new TcpSocketLlistener(tcpPort);
		socketLlistener.startListenerInNewThread();
		TimmosonSessionStoreAndBuilder findAppSessionBuilder2 = new TimmosonSessionStoreAndBuilder("127.0.0.1",
				tcpPort);
		// SampleService sampleService = ClientSendRequest.makeProxyForService(
		// SampleService.class, findAppSessionBuilder2);
		ClientSendRequest.getClientParams().overrideTcpSession = findAppSessionBuilder2.getTcpSession();
		Process test1 = DefaultService.tcpServiceRemote.runCommand(new String[] { "ipconfig" }, null, null);
		log.info("process object received");
		int waitFor = test1.waitFor();
		log.info(waitFor);
		InputStream inputStream = test1.getInputStream();
		log.info(inputStream.getClass());
		ClientSendRequest.getClientParams().overrideTcpSession = findAppSessionBuilder2.getTcpSession();
		byte[] readAllBytesFromInpustStream2 = DefaultService.tcpServiceRemote
				.readAllBytesFromInpustStream(inputStream);
		String out = new String(readAllBytesFromInpustStream2);
		log.info(out);
		ClientSendRequest.getClientParams().overrideTcpSession = findAppSessionBuilder2.getTcpSession();
		InputStream errorStream = test1.getErrorStream();
		ClientSendRequest.getClientParams().overrideTcpSession = findAppSessionBuilder2.getTcpSession();
		byte[] readAllBytesFromInpustStream3 = DefaultService.tcpServiceRemote
				.readAllBytesFromInpustStream(errorStream);
		String err = new String(readAllBytesFromInpustStream3);
		log.info(err);
		Assert.assertTrue(out.contains("Ethernet"));
		socketLlistener.stop();
	}

	public void testServiceJmxBeanLookup() throws Exception {
		log.info("starting");
		JmxServiceId jmxServiceId = new JmxServiceId(new ObjectName("java.lang:type=Memory"));
		Java5VM.getMBeanObject(jmxServiceId.objectName, JrrUtils.findLocalMBeanServer());
		int tcpPort = 32123;
		ServiceLocator.regNewService(SampleService.class, new SampleService());
		TcpSocketLlistener socketLlistener = new TcpSocketLlistener(tcpPort);
		socketLlistener.startListenerInNewThread();
		TimmosonSessionStoreAndBuilder findAppSessionBuilder2 = new TimmosonSessionStoreAndBuilder("127.0.0.1", tcpPort);

		MemoryMXBean sampleService = ClientSendRequest.makeProxy(MemoryMXBean.class, findAppSessionBuilder2,
				jmxServiceId);
		boolean verbose = sampleService.isVerbose();
		log.info(verbose);
		socketLlistener.stop();
		// sampleService.getHeapMemoryUsage();
		log.info("finish");
	}

	@Override
	protected void tearDown() throws Exception {
		// sleep to free port
		log.info("sleep");
		Thread.sleep(500);
	}
}
