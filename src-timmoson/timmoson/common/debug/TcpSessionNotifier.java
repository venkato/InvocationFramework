package timmoson.common.debug;


public interface TcpSessionNotifier {
	void receiveRequest(TcpSessionTrackerBean requestBean);
	void makeRequest(TcpSessionTrackerBean requestBean);
	
}
