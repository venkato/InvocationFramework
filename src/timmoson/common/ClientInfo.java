package timmoson.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;

import java.io.Serializable;

public class ClientInfo implements Serializable{
	
	private static final long serialVersionUID = -5665470851988138362L;


	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));
	
	
	public String userName;
	
}
