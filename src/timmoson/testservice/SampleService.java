package timmoson.testservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.common.sertcp.MakeProxyDiffClassLoader;
import timmoson.common.sertcp.RemoteService;

public class SampleService {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public static String strResturn = "7ffd723";

	public String test1() {
		return "ok22";
	}

	public int add2(int i) {
		return i + 2;
	}

	public int add2Array(int[] i) {
		return i[0] + 2;
	}

	public int add3Array(Integer[] i) {
		return i[0] + 2;
	}

	public String test2() {
		return strResturn;
	}

	public String testCallback() throws Exception {
		log.info("123");
		CallbackService callbackService = RemoteService.callsInfos.get()
				.getSession().makeClient(CallbackService.class);
		callbackService.callback();
		log.info("123 5");
		return strResturn;
	}

	public String testCallbackDiffClassloader() throws Exception {
		log.info("123");
		CallbackService callbackService = MakeProxyDiffClassLoader
				.makeClient(CallbackService.class);
		callbackService.callback();
		log.info("123 5");
		return strResturn;
	}

	public String testThrowExc() throws Exception {
		throw new Exception();
	}
}
