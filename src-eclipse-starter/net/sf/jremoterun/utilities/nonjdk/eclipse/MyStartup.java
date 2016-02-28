package net.sf.jremoterun.utilities.nonjdk.eclipse;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.nonjdk.eclipse.EclipseLunaClasspathAdd;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import net.sf.jremoterun.utilities.JrrUtilities;
import net.sf.jremoterun.utilities.JrrUtilities3;
import sun.reflect.Reflection;

@CompileStatic
public class MyStartup extends AbstractUIPlugin implements IStartup {

	private static final Logger log = Logger.getLogger(MyStartup.class.getName());

	static boolean inited = EclipseLunaClasspathAdd.init();

	public void earlyStartup() {
		log.info("if starter");
	}

}
