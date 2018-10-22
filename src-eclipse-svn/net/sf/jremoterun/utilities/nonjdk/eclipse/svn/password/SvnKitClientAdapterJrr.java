package net.sf.jremoterun.utilities.nonjdk.eclipse.svn.password;

import java.io.File;
import java.util.logging.Logger;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.tigris.subversion.svnclientadapter.ISVNPromptUserPassword;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNStatusUnversioned;
import org.tigris.subversion.svnclientadapter.javahl.AbstractJhlClientAdapter;
import org.tigris.subversion.svnclientadapter.javahl.JhlNotificationHandler;
import org.tigris.subversion.svnclientadapter.javahl.JhlProgressListener;
import org.tigris.subversion.svnclientadapter.javahl.AbstractJhlClientAdapter.DefaultPromptUserPassword;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

// org.tigris.subversion.clientadapter.svnkit.Activator
public class SvnKitClientAdapterJrr  extends AbstractJhlClientAdapter{

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
	
	public SvnKitClientAdapterJrr() {
		this.svnClient = new SvnClientJrr();
		this.notificationHandler = new JhlNotificationHandler();
		this.progressListener = new JhlProgressListener();
		this.svnClient.notification2(this.notificationHandler);
		this.svnClient.setPrompt(new DefaultPromptUserPassword());
		this.svnClient.setProgressCallback(this.progressListener);
	}

	public boolean isThreadsafe() {
		return false;
	}

	public void createRepository(File path, String repositoryType) throws SVNClientException {
		if ("bdb".equalsIgnoreCase(repositoryType)) {
			throw new SVNClientException("SVNKit only supports fsfs repository type.");
		} else {
			try {
				boolean force = false;
				boolean enableRevisionProperties = false;
				SVNRepositoryFactory.createLocalRepository(path, enableRevisionProperties, force);
			} catch (SVNException var5) {
				this.notificationHandler.logException(var5);
				throw new SVNClientException(var5);
			}
		}
	}

	public void addPasswordCallback(ISVNPromptUserPassword callback) {
//		if (callback != null) {
//			SvnKitPromptUserPassword prompt = new SvnKitPromptUserPassword(callback);
//			this.setPromptUserPassword(prompt);
//		}

	}

	public boolean statusReturnsRemoteInfo() {
		return true;
	}

	public boolean canCommitAcrossWC() {
		return false;
	}

	public ISVNStatus[] getStatus(File path, boolean descend, boolean getAll, boolean contactServer,
			boolean ignoreExternals) throws SVNClientException {
		ISVNStatus[] statuses = super.getStatus(path, descend, getAll, contactServer, ignoreExternals);
		if (statuses.length == 0) {
			if (getAll) {
				return new ISVNStatus[]{new SVNStatusUnversioned(path)};
			} else {
				ISVNStatus[] reCheckStatuses = super.getStatus(path, false, true, false, true);
				return reCheckStatuses.length == 0
						? new ISVNStatus[]{new SVNStatusUnversioned(path)}
						: new ISVNStatus[0];
			}
		} else {
			return statuses;
		}
	}

}
