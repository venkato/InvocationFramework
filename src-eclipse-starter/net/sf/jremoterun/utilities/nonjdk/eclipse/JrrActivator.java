package net.sf.jremoterun.utilities.nonjdk.eclipse;


import net.sf.jremoterun.utilities.JrrClassUtils;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import java.util.logging.Logger;


public class JrrActivator extends AbstractUIPlugin{


	private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

	public static boolean inited = EclipseLunaClasspathAdd.init();



	static {
		log.info("JrrActivator invoked");
	}

}
