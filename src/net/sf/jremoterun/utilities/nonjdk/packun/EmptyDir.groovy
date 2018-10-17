package net.sf.jremoterun.utilities.nonjdk.packun

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean2
import net.sf.jremoterun.utilities.nonjdk.store.OneNestedField;

import java.util.logging.Logger;

@CompileStatic
class EmptyDir implements OneNestedField{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File dir

    EmptyDir(File dir) {
        this.dir = dir
    }

    @Override
    String toString() {
        return "EmptyDir : ${dir}"
    }

    @Override
    Object getNestedField() {
        return dir
    }
}
