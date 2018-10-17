package net.sf.jremoterun.utilities.nonjdk.eclipse.proxy;

import java.net.URI;
import java.util.logging.Logger;

import org.eclipse.core.internal.net.AbstractProxyProvider;
import org.eclipse.core.internal.net.ProxyData;
import org.eclipse.core.internal.net.ProxyManager;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class EclipseProxyAdopt {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public static EclipseProxyAdopt proxyAdopt;

	public ProxyData proxyDataHttp;
	public ProxyData proxyDataHttpS;

	public AbstractProxyProvider nativeProxyProvider;
	public JrrAbstractProxyProvider jrrAbstractProxyProvider;
	public ProxyManager defaultProxyService;
	public ProxyWrapper proxyWrapper;

	public IProxyData[] noProxy = new IProxyData[0];
	public IProxyData[] proxyHttp = new IProxyData[1];
	public IProxyData[] proxyHttps = new IProxyData[1];
	public IProxyData[] proxyBoth = new IProxyData[2];
	public EclipseProxyData eclipseProxyData;

	public boolean useJrrProxy = true;

	public EclipseProxyAdopt(EclipseProxyData eclipseProxyData) throws Exception {
		this.eclipseProxyData = eclipseProxyData;
		proxyAdopt = this;
		init();
	}

	void init() throws Exception {
		proxyDataHttp = new ProxyData("HTTP", eclipseProxyData.getProxyHost(), eclipseProxyData.getProxyPort(), true,
				"jrrHttp");
		proxyDataHttpS = new ProxyData("HTTPS", eclipseProxyData.getProxyHost(), eclipseProxyData.getProxyPort(), true,
				"jrrHttps");
		defaultProxyService = (ProxyManager) ProxyManager.getProxyManager();
		proxyWrapper = new ProxyWrapper(defaultProxyService, this);
		JrrClassUtils.setFieldValue(ProxyManager.class, "proxyManager", proxyWrapper);
		proxyDataHttp.setUserid(eclipseProxyData.getUser());
		proxyDataHttpS.setUserid(eclipseProxyData.getUser());
		proxyDataHttp.setPassword(eclipseProxyData.getPassword());
		proxyDataHttpS.setPassword(eclipseProxyData.getPassword());

		proxyHttp[0] = proxyDataHttp;
		proxyHttps[0] = proxyDataHttpS;
		proxyBoth[0] = proxyDataHttp;
		proxyBoth[1] = proxyDataHttpS;
		nativeProxyProvider = (AbstractProxyProvider) JrrClassUtils.getFieldValue(defaultProxyService,
				"nativeProxyProvider");
		jrrAbstractProxyProvider = new JrrAbstractProxyProvider(nativeProxyProvider, this);
		JrrClassUtils.setFieldValue(defaultProxyService, "nativeProxyProvider", jrrAbstractProxyProvider);

	}

	public IProxyData getProxyDataForHost(String host, String type) {
		if (type == null) {
			return null;
		}
		boolean useProxy = eclipseProxyData.useProxyForHost(host);
		if (!useProxy) {
			return null;
		}
		type = type.toLowerCase();
		if ("http".equals(type)) {
			return proxyDataHttp;
		}
		if ("https".equals(type)) {
			return proxyDataHttpS;
		}
		log.info("strange type " + type + " " + host);
		return null;

	}

	public IProxyData[] selectImpl(URI uri) {
		boolean useProxy = eclipseProxyData.useProxy(uri);
		if (!useProxy) {
			return noProxy;
		}
		String schema = uri.getScheme().toLowerCase();
		if ("http".equals(schema)) {
			return proxyHttp;
		}
		if ("https".equals(schema)) {
			return proxyHttps;
		}
		log.info("strange schema for " + uri);
		return proxyHttp;
	}

}
