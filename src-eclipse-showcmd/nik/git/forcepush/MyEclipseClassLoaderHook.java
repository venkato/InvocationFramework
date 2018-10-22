package nik.git.forcepush;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.osgi.internal.framework.EquinoxContainer;
import org.eclipse.osgi.internal.hookregistry.ClassLoaderHook;
import org.eclipse.osgi.internal.hookregistry.HookRegistry;
import org.eclipse.osgi.internal.loader.BundleLoader;
import org.eclipse.osgi.internal.loader.ModuleClassLoader;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class MyEclipseClassLoaderHook extends ClassLoaderHook {

	private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

	public static MyEclipseClassLoaderHook myEclipseClassLoaderHook2;

	public HashMap<String, BundleLoader> classLoaderMapping = new HashMap();

	public HashSet<String> myModules = new HashSet<String>();

	public boolean isMyModule(ModuleClassLoader classLoader) {
		String symbolicName = classLoader.getBundle().getSymbolicName();
		if (myModules.contains(symbolicName)) {
			// log.info("my module search class");
			return true;
		}
		return false;
	}

	@Override
	public Class<?> postFindClass(String name, ModuleClassLoader classLoader) throws ClassNotFoundException {
		if (!isMyModule(classLoader)) {
			return null;
		}
		Set<Entry<String, BundleLoader>> entrySet = classLoaderMapping.entrySet();
		for (Entry<String, BundleLoader> entry : entrySet) {
			if (name.startsWith(entry.getKey())) {
				BundleLoader bundleLoader = entry.getValue();
				Class<?> findClass = bundleLoader.findClass(name);
				log.info("class found : "+name);
				return findClass;
			}
		}
		return null;
	}

	public static void addClassloaderHook() throws Exception {
		ModuleClassLoader classLoader = (ModuleClassLoader) JrrClassUtils.getCurrentClassLoader();
		BundleLoader bundleLoader = classLoader.getBundleLoader();
		EquinoxContainer container = (EquinoxContainer) JrrClassUtils.getFieldValue(bundleLoader, "container");
		HookRegistry hookRegistry = container.getConfiguration().getHookRegistry();
		// .getClassLoaderHooks();
		List<ClassLoaderHook> classLoaderHooks = (List<ClassLoaderHook>) JrrClassUtils.getFieldValue(hookRegistry,
				"classLoaderHooks");
		MyEclipseClassLoaderHook hook = new MyEclipseClassLoaderHook();
		myEclipseClassLoaderHook2 = hook;
		classLoaderHooks.add(hook);
		log.info("class loader hook added");
	}
}
