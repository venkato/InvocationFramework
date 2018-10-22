package timmoson.common.transferedobjects;

import timmoson.common.sertcp.TcpSession;
import timmoson.server.ServiceSupport;

import java.io.Serializable;


public interface ServiceId extends Serializable{

	ServiceSupport find(TcpSession tcpSession) throws Exception;

}
