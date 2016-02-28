package timmoson.server.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.client.ClientSendRequest;
import timmoson.common.CallInfoServer;
import timmoson.common.sertcp.RemoteService;
import timmoson.common.sertcp.TcpSession;

public class TcpService {
	public static String serviceId=TcpService.class.getName();

	public static TcpService tcpServiceLocal=new TcpService();
	// set ClientSendRequest.getClientParams().overrideTcpSession before invokaction
	public static TcpService tcpServiceRemote=ClientSendRequest.makeProxy(TcpService.class,null,serviceId);

	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public void removeLocalSessionObject(String objectId) {
		CallInfoServer callInfoServer=RemoteService.callsInfos.get();
		TcpSession tcpSession=(TcpSession) callInfoServer.getSession();
		Object obj=tcpSession.serviceObjectsServer.remove(objectId);
		if(obj==null) {
			log.warn("object with id not found "+objectId);
		}else {
			log.info("object is removed "+objectId);
		}
	}
}
