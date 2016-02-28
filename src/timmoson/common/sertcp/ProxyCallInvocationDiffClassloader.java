package timmoson.common.sertcp;

import junit.framework.Assert;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.javassist.BaseMethodHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ProxyCallInvocationDiffClassloader extends BaseMethodHandler {
	private static final Log log = LogFactory.getLog(Reflection.getCallerClass(1));

	public final Object object;

	HashMap<Method, Method> methodMap = new HashMap<Method, Method>();

	public ProxyCallInvocationDiffClassloader(Object object) {
		super();
		this.object = object;
		Assert.assertNotNull(object);
		handleHashCode = true;
	}

	@Override
	public Object invoke2(Object beanObject, Method methodToInvoke, Method superMethod, Object[] args)
			throws Throwable {
		String methodName = methodToInvoke.getName();
		if ("equals".equals(methodName) && args.length == 1) {
			if (beanObject == args[0]) {
				return true;
			}
			return false;

		} else if (args.length == 0) {
			if ("toString".equals(methodName)) {
				return object.getClass().getName();
			}
		}
		Method method2 = methodMap.get(methodToInvoke);
		if (method2 == null) {
			method2 = JrrClassUtils.findMethodByCount(object.getClass(), methodName, args.length);
			methodMap.put(methodToInvoke, method2);
		}
		Object result = method2.invoke(object, args);
		return result;
	}

}
