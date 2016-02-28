package nik.git.forcepush;


import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.tigris.subversion.subclipse.ui.operations.UpdateOperation;
import org.tigris.subversion.svnclientadapter.SVNRevision;

import net.sf.jremoterun.utilities.JrrClassUtils;


public class SvnUpdater {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	
	
	// here project can be used as resource
	public static void updateSvn2(IResource resource) throws Exception {
		log.info("about to update for "+resource);
		UpdateOperation updateOperation = new UpdateOperation(null, resource, SVNRevision.HEAD);
		updateOperation.setDepth(0);
		updateOperation.setSetDepth(true);
		updateOperation.setForce(false);
		updateOperation.setIgnoreExternals(true);
		updateOperation.setCanRunAsJob(true);
//		updateOperation.run(new SysOutProgressMonitor());
		log.info("starting update for "+resource);
		updateOperation.run();
		log.info("updated started for "+resource);
	}
	
	
}
