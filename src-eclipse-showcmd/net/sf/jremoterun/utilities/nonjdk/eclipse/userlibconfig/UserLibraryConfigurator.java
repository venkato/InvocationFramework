package net.sf.jremoterun.utilities.nonjdk.eclipse.userlibconfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.UserLibraryManager;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileWithSources;

public class UserLibraryConfigurator extends AddFileWithSources {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public List<IClasspathEntry> binWithSources = new ArrayList();

	public void saveLibraryToEclipseConfig(String libName) {
		UserLibraryManager lm = JavaModelManager.getJavaModelManager().getUserLibraryManager();		
		IClasspathEntry[] classpathEntries = binWithSources.toArray(new IClasspathEntry[0]);
		lm.setUserLibrary(libName, classpathEntries, false);
	}

	@Override
	public void addLibraryWithSource(File bin, List<File> src) throws Exception {
		ClasspathEntry classpathEntry = createClasspathEntry(bin, src);
		binWithSources.add(classpathEntry);
	}

	protected ClasspathEntry createClasspathEntry(File bin1, List<File> src1) {
		Path binP = new Path(bin1.getAbsolutePath());
		Path srcP = null;
		if (src1 != null && src1.size() > 0) {
			File srcfile = src1.get(0);
			if (srcfile != null) {
				srcP = new Path(srcfile.getAbsolutePath());
			}
		}
		ClasspathEntry ce = new ClasspathEntry(IClasspathEntry.CPE_PROJECT, IClasspathEntry.CPE_LIBRARY, binP,
				new IPath[0], new IPath[0], srcP, null, null, true, null, false, new IClasspathAttribute[0]);
		return ce;
	}

	@Override
	public void addSourceFImpl(File arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSourceS(String arg0) throws Exception {
		// TODO Auto-generated method stub

	}
}
