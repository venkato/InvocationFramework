package nik.git.forcepush;


import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.tigris.subversion.subclipse.ui.operations.UpdateOperation;
import org.tigris.subversion.svnclientadapter.SVNRevision;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.eclipse.svn.SvnUpdater2;


public class SvnUpdater {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	
	
	// here project can be used as resource
	public static void updateSvn2(IResource resource) throws Exception {
		log.info("about to update for "+resource);
		SvnUpdater2 updater2 = new SvnUpdater2(resource);
		updater2.updateSvn2();
		log.info("updated started for "+resource);
	}
	
	
}
