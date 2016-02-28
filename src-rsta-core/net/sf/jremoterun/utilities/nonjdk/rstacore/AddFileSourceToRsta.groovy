package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic

import java.util.logging.Logger;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.buildpath.DirLibraryInfo;
import org.fife.rsta.ac.java.buildpath.DirSourceLocation;
import org.fife.rsta.ac.java.buildpath.JarLibraryInfo;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.buildpath.ZipSourceLocation;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileWithSources;

@CompileStatic
public class AddFileSourceToRsta extends AddFileWithSources {
	private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

	public JarManager jarManager;

	
	public AddFileSourceToRsta(JarManager jarManager) {
		this.jarManager = jarManager;
	}


	@Override
	public void addLibraryWithSource(File binary, List<File> source)  throws Exception  {
		SourceLocation sourceLocation = null;
		if(source==null||source.size()==0) {
			sourceLocation = null;
		}else {
			File source2 = source[0]
			if(source2.isFile()) {
				sourceLocation = new ZipSourceLocation(source2);
			}else {
				sourceLocation = new DirSourceLocation(source2);
			}			
		}
		LibraryInfo libraryInfo;
		if(binary.isFile()) {
			libraryInfo = new JarLibraryInfo(binary, sourceLocation);
		}else {
			libraryInfo = new DirLibraryInfo(binary, sourceLocation);
		}
		jarManager.addClassFileSource(libraryInfo);			
	}

	@Override
	public void addSourceFImpl(File source) {
		
	}


	@Override
	public void addSourceS(String source) throws Exception {
		log.warning("unsupported : "+source);
	}


}
