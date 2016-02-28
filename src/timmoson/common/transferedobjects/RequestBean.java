package timmoson.common.transferedobjects;

import net.sf.jremoterun.JrrUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class RequestBean implements Serializable {
	private static final long serialVersionUID = -7416210782302849223L;

	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public byte[] stackTrace;
	public boolean waitResult = true;
	public ServiceId serviceId;
	public String methodName;
	public List<String> classesNameParams;
	// int patamsCount;
	public byte[] params;
	public String requestId;

	public String readRemoteStackToString() {
		Exception exception = readRemoteStack();
		if(exception==null) {
			return null;
		}
		return JrrUtils.exceptionToString(exception);
	}

	public Exception readRemoteStack() {
		if (stackTrace == null) {
			return null;
		}
		Exception exception=null;
		try {
			exception = (Exception) JrrUtils
					.deserialize(stackTrace, null);
		} catch (IOException e) {
			log.info(null,e);
			
		} catch (ClassNotFoundException e) {
			log.info(null,e);
			
		}
		return exception;
	}
}
