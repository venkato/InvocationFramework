package net.sf.jremoterun.utilities.nonjdk.eclipse.proxy;

import java.net.URI;
import java.util.logging.Logger;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.net.ProxyTrackerI;

public class NoProxyEclipseProxyData implements EclipseProxyData{

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
	
	public ProxyTrackerI proxyTracker; 

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUser() {
		return null;
	}

	@Override
	public String getProxyHost() {
		return null;
	}

	@Override
	public int getProxyPort() {
		return 0;
	}

	@Override
	public boolean useProxyForHost(String host) {
		proxyTracker.accessRequested(host, false);
		return false;
	}

	@Override
	public boolean useProxy(URI uri) {
		proxyTracker.accessRequested(uri, false);
		return false;
	}
}
