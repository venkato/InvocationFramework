package net.sf.jremoterun.utilities.nonjdk.eclipse.proxy;

import java.net.URI;
import java.util.logging.Logger;
import net.sf.jremoterun.utilities.JrrClassUtils;

public interface EclipseProxyData {

	String getPassword();
	String getUser();
	String getProxyHost();
	int getProxyPort();
	

	
	boolean useProxyForHost(String host);
	boolean useProxy(URI uri);
	
}
