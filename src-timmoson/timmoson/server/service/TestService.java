package timmoson.server.service;

import org.apache.log4j.Logger;

public class TestService {
	private static final Logger logger = Logger.getLogger(TestService.class);

	public void testCall(String param) {
		logger.info(param);
//		return 2;
	}
}
