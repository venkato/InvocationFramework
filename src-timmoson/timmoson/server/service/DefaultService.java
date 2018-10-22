package timmoson.server.service;

import net.sf.jremoterun.JrrUtils;
import org.apache.log4j.Logger;
import timmoson.client.ClientSendRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class DefaultService {
	private static final Logger logger = Logger.getLogger(DefaultService.class);
	private static final Logger log = logger;

	public static DefaultService tcpServiceLocal = new DefaultService();
	public static DefaultService tcpServiceRemote = ClientSendRequest
			.makeProxy(DefaultService.class, null,
					DefaultService.class.getName());

	public Process runCommand(String[] command, String[] envp, File dir)
			throws IOException {
		log.info("running " + Arrays.toString(command));
		Process exec = Runtime.getRuntime().exec(command);
		log.info("done");
		return exec;
	}

	public static final byte[] emptyByteBuf = new byte[0];

	public byte[] readAllBytesFromInpustStream(InputStream in)
			throws IOException {
		byte[] readAllBytesFromInpustStream = JrrUtils
				.readAllBytesFromInpustStream(in);
		return readAllBytesFromInpustStream;
		// byte[] bb=new byte[byteBufLength];
		// int read = in.read(bb, 0, byteBufLength);
		// if(read==-1) {
		// return emptyByteBuf;
		// }
		// if(read==0) {
		// return emptyByteBuf;
		//
		// }
		// if(read<byteBufLength) {
		// byte[] bb3=new byte[byteBufLength];
		// }
		// return bb;
	}

}
