package timmoson.test;

import java.util.List;
import java.util.Map;

import net.sf.jremoterun.ICodeForExecuting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.reflect.Reflection;
import timmoson.server.ServiceLocator;
import timmoson.testservice.SampleService;

public class DiffClassloaderServiceRegister implements ICodeForExecuting {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	@Override
	public Object run(List params, Map previousCode) throws Exception {
		ServiceLocator.regNewService(new SampleService());
		log.info("service registered");
		return null;
	}

}
