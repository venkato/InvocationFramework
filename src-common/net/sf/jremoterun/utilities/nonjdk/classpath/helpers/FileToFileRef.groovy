package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2

import java.util.logging.Logger

@CompileStatic
class FileToFileRef implements ToFileRef2,ZeroOverheadFileRef {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File file

    FileToFileRef(File file) {
        this.file = file
    }

    @Override
    File resolveToFile() {
        return file
    }

    @Override
    String toString() {
        return "${file}";
    }
}
