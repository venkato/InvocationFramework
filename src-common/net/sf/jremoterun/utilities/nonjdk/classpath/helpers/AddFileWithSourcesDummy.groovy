package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class AddFileWithSourcesDummy extends AddFileWithSources{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AddFileWithSourcesDummy() {
        downloadSources = true
    }

    @Override
    void addLibraryWithSource(File binary, List<File> source) throws Exception {

    }

    @Override
    void addSourceFImpl(File source) throws Exception {

    }

    @Override
    void addSourceS(String source) throws Exception {

    }
}
