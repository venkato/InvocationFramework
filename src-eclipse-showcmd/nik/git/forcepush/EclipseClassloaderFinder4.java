package nik.git.forcepush;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jremoterun.ClassLoaderNotFoundException;
import net.sf.jremoterun.FindParentClassLoader;
import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.SimpleFindParentClassLoader;
import net.sf.jremoterun.utilities.JrrClassUtils;


import org.osgi.framework.Bundle;

public class EclipseClassloaderFinder4 implements FindParentClassLoader {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();	

	public static Object bundles;

	public static Map bundlesBySymbolicName;

	public static EclipseClassloaderFinder4 cc;

	public Object findParentClassLoaderBefore;
	public FindParentClassLoader findParentClassLoader;

	public static void findBundles() throws Exception {
		ClassLoader classLoader = org.eclipse.ui.plugin.AbstractUIPlugin.class.getClassLoader();
		Object bunldeLoader = JrrClassUtils.getFieldValue(classLoader, "delegate");
		org.osgi.framework.Bundle bundleHost = (Bundle) JrrClassUtils.getFieldValue(bunldeLoader, "bundle");
		Object framework = JrrClassUtils.getFieldValue(bundleHost, "framework");
		Object bundles = JrrClassUtils.getFieldValue(framework, "bundles");
		bundlesBySymbolicName = (Map) JrrClassUtils.getFieldValue(bundles, "bundlesBySymbolicName");
		if (bundlesBySymbolicName == null) {
			throw new Exception("bundlesBySymbolicName is null");
		}
	}

	public static void registerClassloaderFinder() {
		EclipseClassloaderFinder4.cc = new EclipseClassloaderFinder4();
		EclipseClassloaderFinder4.cc.findParentClassLoader = new SimpleFindParentClassLoader();
		EclipseClassloaderFinder4.cc.findParentClassLoaderBefore = SharedObjectsUtils.getGlobalMap()
				.get(SharedObjectsUtils.findParentClassLoaderS);
		SharedObjectsUtils.setFindParentClassLoader(EclipseClassloaderFinder4.cc);
	}

	@Override
	public ClassLoader findClassLoader(Serializable arg0) throws ClassLoaderNotFoundException {
		System.out.println("arg " + arg0);
		log.info("args " + arg0);
		if (arg0 instanceof String) {
			String bundelId = (String) arg0;
			if (bundelId.equals(SimpleFindParentClassLoader.bootStrapClassLoaderIdS)
					|| bundelId.equals(SimpleFindParentClassLoader.extClassLoaderIdS)
					|| bundelId.equals(SimpleFindParentClassLoader.defaultClassLoaderId)
					|| bundelId.equals(SimpleFindParentClassLoader.systemClassLoaderIdS)) {
			} else {
				try {
					if (bundlesBySymbolicName == null) {
						findBundles();
					}
					Object object = bundlesBySymbolicName.get(bundelId);
					if (object == null) {
						log.warning("bundle is null");
					} else {
						org.osgi.framework.Bundle bundle = (Bundle) Array.get(object, 0);
						Method getClassLoaderMethod = JrrClassUtils.findMethod(bundle.getClass(), "getClassLoader", 0);
						ClassLoader classLoader = (ClassLoader) getClassLoaderMethod.invoke(bundle);
						if (classLoader == null) {
							log.warning("classloader is null");
						} else {
							log.info("found classloader " + arg0);
							return classLoader;
						}
					}
				} catch (Exception e) {
					// e.printStackTrace();
					log.log(Level.WARNING, bundelId, e);
				}

			}
		}
		log.info("not found classloader " + arg0);
		return findParentClassLoader.findClassLoader(arg0);
	}
}
