package nik.git.forcepush;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.internal.core.search.indexing.IndexManager;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.util.logging.Logger;



public class RebuildIndex {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public static void updateIndex(IndexManager indexManager) throws Exception {
		log.info("about to reindex ..");
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		indexManager.cleanUpIndexes();
		for (IProject project : projects) {
			indexManager.indexAll(project);
		}
		for (IProject project : projects) {
			indexManager.recreateIndex(project.getFullPath());
		}
		indexManager.saveIndexes();
		log.info("reindex done");

	}

}
