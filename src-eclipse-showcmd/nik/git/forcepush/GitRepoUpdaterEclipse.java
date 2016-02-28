package nik.git.forcepush;

import org.eclipse.egit.core.Activator;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.NoRemoteRepositoryException;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;



public class GitRepoUpdaterEclipse {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	
	public static List<String> getCOnfiguredRepos(){
		List<String> configuredRepositories = Activator.getDefault().getRepositoryUtil().getConfiguredRepositories();
		return configuredRepositories;
	}
	
}
