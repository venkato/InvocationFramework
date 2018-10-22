package net.sf.jremoterun.utilities.nonjdk.eclipse.svn;

import java.util.Map;
import java.util.logging.Logger;

import org.tigris.subversion.clientadapter.Activator;
import org.tigris.subversion.clientadapter.AdapterManager;

import net.sf.jremoterun.utilities.JrrClassUtils;

// Remove native impl from Preferences -> team -> SVN -> svn interface 
public class SvnAdapterManagerJrr extends AdapterManager{

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
	
	public static String javahlS = "javahl";
	public static String svnkit = "svnkit";
	
	public static volatile boolean inited = false;
	public static AdapterManager adapterManagerOrigin;
	
	public static void removeNativeSvnImpl() throws Exception {
		if(inited) {
			log.info("already inited");
		}else {
			inited = true;
			removeNativeSvnImpl2();
		}
	}
	
	public static void removeNativeSvnImpl2() throws Exception {
		Activator activator = Activator.getDefault();
		activator.getAllClientWrappers();
		adapterManagerOrigin = (AdapterManager) JrrClassUtils.getFieldValue(activator, "adapterManager");
		JrrClassUtils.setFieldValue(activator, "adapterManager",new SvnAdapterManagerJrr());
		activator.getAllClientWrappers();
		Map javahl2 = adapterManagerOrigin.getClientWrappers();
		log.info("current svn adaptors before remove : "+javahl2.keySet());
		javahl2.remove(javahlS);
		log.info("current svn adaptors after remove : "+javahl2.keySet());
	}
	
	
	@Override
	public synchronized Map getClientWrappers() {
		Map javahl2 = super.getClientWrappers();
		
		log.info("current svn adaptors : "+javahl2.keySet());
		javahl2.remove( SvnAdapterManagerJrr.javahlS);
		return javahl2;

	}
}
