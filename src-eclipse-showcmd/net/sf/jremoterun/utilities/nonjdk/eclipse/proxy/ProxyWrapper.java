package net.sf.jremoterun.utilities.nonjdk.eclipse.proxy;

import java.net.URI;
import java.util.logging.Logger;

import org.eclipse.core.internal.net.ProxyManager;
import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class ProxyWrapper implements IProxyService {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public ProxyManager nested;
	public EclipseProxyAdopt proxyAdopt;

	public ProxyWrapper(ProxyManager nested, EclipseProxyAdopt proxyAdopt) {
		super();
		this.nested = nested;
		this.proxyAdopt = proxyAdopt;
	}

	public void addProxyChangeListener(IProxyChangeListener listener) {
		nested.addProxyChangeListener(listener);
	}

	public void removeProxyChangeListener(IProxyChangeListener listener) {
		nested.removeProxyChangeListener(listener);
	}

	public String[] getNonProxiedHosts() {
		Thread.dumpStack();
		return nested.getNonProxiedHosts();
	}

	public String[] getNativeNonProxiedHosts() {
		return nested.getNativeNonProxiedHosts();
	}

	public void setNonProxiedHosts(String[] hosts) {
		nested.setNonProxiedHosts(hosts);
	}

	public IProxyData[] getProxyData() {
		Thread.dumpStack();
		if (proxyAdopt.useJrrProxy) {
			return proxyAdopt.proxyBoth;
		}
		return nested.getProxyData();
	}

	public IProxyData[] getNativeProxyData() {
		return nested.getNativeProxyData();
	}

	public void setProxyData(IProxyData[] proxies) {
		nested.setProxyData(proxies);
	}

	public boolean isProxiesEnabled() {
		Thread.dumpStack();
		return nested.isProxiesEnabled();
	}

	public void setProxiesEnabled(boolean enabled) {
		nested.setProxiesEnabled(enabled);
	}

	public void initialize() {
		nested.initialize();
	}

	public IProxyData getProxyData(String type) {
		log.info("proxy type: " + type);
		return nested.getProxyData(type);
	}

	public IProxyData[] getProxyDataForHost(String host) {
		return nested.getProxyDataForHost(host);
	}

	public String toString() {
		return nested.toString();
	}

	public IProxyData getProxyDataForHost(String host, String type) {
		log.info("proxy type: " + type + " , host : " + host);
		if (proxyAdopt.useJrrProxy) {
			return proxyAdopt.getProxyDataForHost(host, type);
		}
		return nested.getProxyDataForHost(host, type);
	}

	public void preferenceChange(PreferenceChangeEvent event) {
		nested.preferenceChange(event);
	}

	public boolean hasSystemProxies() {
		Thread.dumpStack();
		return nested.hasSystemProxies();
	}

	public boolean isSystemProxiesEnabled() {
		Thread.dumpStack();
		return nested.isSystemProxiesEnabled();
	}

	public void setSystemProxiesEnabled(boolean enabled) {
		nested.setSystemProxiesEnabled(enabled);
	}

	public IProxyData[] select(URI uri) {
		log.info("selecting proxy for : " + uri);
		if (proxyAdopt.useJrrProxy) {
			return proxyAdopt.selectImpl(uri);
		}
		return nested.select(uri);
	}

	public IProxyData resolveType(IProxyData data) {
		return nested.resolveType(data);
	}

	public IProxyData[] resolveType(IProxyData[] data) {
		return nested.resolveType(data);
	}

}
