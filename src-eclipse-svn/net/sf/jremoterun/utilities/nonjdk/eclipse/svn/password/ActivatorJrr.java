package net.sf.jremoterun.utilities.nonjdk.eclipse.svn.password;

import org.tigris.subversion.clientadapter.ISVNClientWrapper;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tmatesoft.svn.core.javahl17.SVNClientImpl;

// org.tigris.subversion.clientadapter.svnkit.Activator
public class ActivatorJrr implements ISVNClientWrapper {
	public static final String PLUGIN_ID = "org.tigris.subversion.clientadapter.svnkit";
	public static final String PT_SVNCONNECTORFACTORY = "svnconnectorfactory";
//	private static ActivatorJrr plugin;
	private String displayName;
	private String version;

	public ISVNClientAdapter getAdapter() {
		return this.isAvailable() ? new SvnKitClientAdapterJrr() : null;
	}

	public String getAdapterID() {
		return "svnkit";
	}

	public String getVersionString() {
		return this.getVersionSynchronized();
	}

	private synchronized String getVersionSynchronized() {
		if (this.version == null) {
			if (this.isAvailable()) {
				SVNClientImpl adapter =  new SvnClientJrr();
				this.version = adapter.getVersion().toString();
			} else {
				this.version = "Not Available";
			}
		}

		return this.version;
	}

	public boolean isAvailable() {
		return true;
	}

	public void setDisplayName(String string) {
		this.displayName = string;
	}

	public String getDisplayName() {
		return this.displayName + " " + this.getVersionString();
	}

	public String getLoadErrors() {
		return this.isAvailable()
				? ""
				: "Class org.tmatesoft.svn.core.javahl17.SVNClientImpl not found.\nInstall the SVNKit plug-in from http://www.svnkit.com/";
	}


}