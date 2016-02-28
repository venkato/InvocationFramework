package timmoson.common.transferedobjects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;

import java.io.Serializable;

/**
 * Object stored in client session store
 *
 */
public class RemoteObjectClient implements Serializable{
	private static final long serialVersionUID = 2664186260569165749L;
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public ServiceId objectId;
	public String className;
	
}
