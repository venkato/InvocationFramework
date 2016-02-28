package net.sf.jremoterun.utilities.nonjdk.gi2

import groovy.transform.CompileStatic;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.NoRemoteRepositoryException;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;


@CompileStatic
public class GitRepoUpdater {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


	
	static  void fetchOneRepo(File repo) throws Exception {
		log.info("start fetch ${repo}");
		Git git = Git.open(repo);
		File file2 = new File(repo,"refs\\remotes");
		File[] listFiles = file2.listFiles();
		for (File file : listFiles) {
			try {
				log.info("fetching "+file.getAbsolutePath());
				FetchCommand fetchCommand = git.fetch();
				fetchCommand.setRemote(file.getName());
				fetchCommand.call();
			}catch (Exception e) {
				Throwable e2 = JrrUtils.getRootException(e);
				if (e2 instanceof NoRemoteRepositoryException) {
					NoRemoteRepositoryException new_name = (NoRemoteRepositoryException) e2;
					log.info(file.getAbsolutePath()+" "+e);					
				}else {
					throw e;
				}
			}
		}
	}
	
}
