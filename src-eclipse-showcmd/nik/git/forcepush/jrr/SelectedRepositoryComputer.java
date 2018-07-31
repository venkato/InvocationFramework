// Code copied from https://github.com/rherrmann/gonsole project under Eclipse Public License, version 1.0.

package nik.git.forcepush.jrr;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.ResourceUtil;

import nik.git.forcepush.GetConfiguredRepositories;

class SelectedRepositoryComputer {

	private final IStructuredSelection selection;
	private final IWorkbenchPart activePart;
	private final List<String> configuredRepositoryLocations;

	SelectedRepositoryComputer(ISelection selection, IWorkbenchPart activePart) {
		this.selection = selection instanceof IStructuredSelection ? (IStructuredSelection) selection
				: StructuredSelection.EMPTY;
		this.activePart = activePart;
		// sometimes this plugin can't see Activator class from git pluging, so
		// adding abstraction to redefining implementation
		this.configuredRepositoryLocations = GetConfiguredRepositories.getConfiguredRepositories
				.getConfiguredRepositories();
	}

	File compute() {
		File result = null;
		IResource[] selectedResources = getSelectedResources();
		for (int i = 0; result == null && i < selectedResources.length; i++) {
			result = getRepositoryLocation(selectedResources[i]);
		}
		return result;
	}

	private File getRepositoryLocation(IResource resource) {
		File result = null;
		IPath bestMatch = null;
		IPath resourceLocation = resource.getLocation();
		if (resourceLocation != null) {
			for (String repositoryLocation2 : configuredRepositoryLocations) {
				File repositoryLocation = new File(repositoryLocation2);
				IPath workDir = Repositories.getWorkDir(repositoryLocation);
				if (workDir != null && workDir.isPrefixOf(resourceLocation)) {
					if (bestMatch == null || workDir.segmentCount() > bestMatch.segmentCount()) {
						result = repositoryLocation;
						bestMatch = workDir;
					}
				}
			}
		}
		return result;
	}

	private IResource[] getSelectedResources() {
		Set<IResource> result = new HashSet<IResource>();
		IEditorInput editorInput = getActiveEditorInput();
		if (editorInput != null) {
			IResource resource = ResourceUtil.getResource(editorInput);
			if (resource != null) {
				result.add(resource);
			}
		} else {
			for (Object element : selection.toList()) {
				IResource resource = ResourceUtil.getResource(element);
				if (resource != null) {
					result.add(resource);
				}
			}
		}
		return result.toArray(new IResource[result.size()]);
	}

	private IEditorInput getActiveEditorInput() {
		IEditorInput result = null;
		if (activePart instanceof IEditorPart) {
			IEditorPart editorPart = (IEditorPart) activePart;
			result = editorPart.getEditorInput();
		}
		return result;
	}

}
