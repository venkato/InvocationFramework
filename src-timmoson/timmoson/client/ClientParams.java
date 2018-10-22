package timmoson.client;


import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import timmoson.common.sertcp.TcpSession;

public class ClientParams implements Cloneable{
	private static final Log log = LogFactory.getLog(JrrClassUtils.getCurrentClass());

	public boolean retryIfIOException=true;
	public Boolean waitResult;

	public volatile TcpSession overrideTcpSession;

	public static ClientSendRequest clientSendRequestDefault=new ClientSendRequest();

	public ClientSendRequest clientSendRequest=clientSendRequestDefault;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
