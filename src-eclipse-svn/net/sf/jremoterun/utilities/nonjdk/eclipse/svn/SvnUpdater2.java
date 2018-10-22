package net.sf.jremoterun.utilities.nonjdk.eclipse.svn;


import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.tigris.subversion.subclipse.ui.operations.UpdateOperation;
import org.tigris.subversion.svnclientadapter.SVNRevision;

import net.sf.jremoterun.utilities.JrrClassUtils;


public class SvnUpdater2 {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	
	public UpdateOperation updateOperation;
	
	
	
	public SvnUpdater2(IResource resource) {
		updateOperation = new UpdateOperation(null, resource, SVNRevision.HEAD);		
	}
	
	public SvnUpdater2(UpdateOperation updateOperation) {
		this.updateOperation = updateOperation;
	}

	public void updateSvn2() throws Exception {
		prepare();	
		updateOperation.run();
	}
	
	public void prepare() throws Exception {
		updateOperation.setDepth(0);
		updateOperation.setSetDepth(true);
		updateOperation.setForce(false);
		updateOperation.setIgnoreExternals(true);
		updateOperation.setCanRunAsJob(true);
	}
	
	
}
