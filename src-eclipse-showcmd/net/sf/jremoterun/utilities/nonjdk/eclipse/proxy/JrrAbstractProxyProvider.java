package net.sf.jremoterun.utilities.nonjdk.eclipse.proxy;

import java.net.URI;
import java.util.logging.Logger;

import org.eclipse.core.internal.net.AbstractProxyProvider;
import org.eclipse.core.net.proxy.IProxyData;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class JrrAbstractProxyProvider extends AbstractProxyProvider {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public AbstractProxyProvider nested;
	public EclipseProxyAdopt proxyAdopt;
	public IProxyData[] emptyProxyData = new IProxyData[0];
	public String[] emptyStringArray = new String[0];

	public JrrAbstractProxyProvider(AbstractProxyProvider nested, EclipseProxyAdopt proxyAdopt) {
		super();
		this.nested = nested;
		this.proxyAdopt = proxyAdopt;
	}

	@Override
	public IProxyData[] select(URI uri) {
		log.info("checking proxy for " + uri);
		if (proxyAdopt.useJrrProxy) {
			return proxyAdopt.selectImpl(uri);
		}
		return nested.select(uri);
	}

	@Override
	protected IProxyData[] getProxyData() {
		Thread.dumpStack();
		return emptyProxyData;
	}

	@Override
	protected String[] getNonProxiedHosts() {
		Thread.dumpStack();
		return emptyStringArray;
	}

}
