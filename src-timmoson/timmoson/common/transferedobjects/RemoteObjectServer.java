package timmoson.common.transferedobjects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * Object stored in server session store
 *
 */
public class RemoteObjectServer implements Serializable{
	private static final long serialVersionUID = 2664186260569165749L;

	public ServiceId objectId;
//	public boolean inSession;
	
}
