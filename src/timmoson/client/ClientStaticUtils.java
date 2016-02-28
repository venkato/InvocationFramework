		package timmoson.client;


        import net.sf.jremoterun.JrrUtils;
        import org.apache.log4j.Logger;
        import timmoson.common.sertcp.Consts;
        import timmoson.common.sertcp.RemoteService;
        import timmoson.common.sertcp.TcpSession;
        import timmoson.common.transferedobjects.ReponseBean;

        import java.io.ByteArrayOutputStream;

		public class ClientStaticUtils {
	private static final Logger log = Logger
			.getLogger(ClientStaticUtils.class);
	
	public static byte[] handleClientReponse(TcpSession tcpSession,
			ByteArrayOutputStream out, int i, String s) throws Exception {
		String s2 = s.substring(Consts.resultBegin.name().length() + 1, i - 1);
		log.debug(s2);
		byte[][] bb23 =RemoteService.  findBetween(Consts.resultBegin.name(),
				Consts.resultEnd.name(), tcpSession.inputStream, out);
		byte[] bb2 = bb23[0];
		ReponseBean reponseBean = (ReponseBean) JrrUtils.deserialize(bb2,
				RemoteService.class.getClassLoader());
		RequestInfoCleint cleint = tcpSession.requets
				.get(reponseBean.requestId);
		if (cleint == null) {
			log.debug("requst id not found " + reponseBean.requestId);
		} else {
			cleint.reponseBean = reponseBean;
			synchronized (cleint.lock) {
				cleint.lock.notify();
			}
		}
		return bb23[1];
	}

}
