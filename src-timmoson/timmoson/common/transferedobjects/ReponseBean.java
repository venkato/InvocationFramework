package timmoson.common.transferedobjects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

public class ReponseBean implements Serializable {
	private static final long serialVersionUID = 5493220940056404018L;
	public String requestId;
	public byte[] response;
	public boolean resultNull=false;
	public volatile byte[] exception;
	public ServiceId serviceInSessionId;
//	public String serviceInSessionResponseClass;

}
