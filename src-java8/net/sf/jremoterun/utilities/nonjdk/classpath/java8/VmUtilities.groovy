package net.sf.jremoterun.utilities.nonjdk.classpath.java8;

import net.sf.jremoterun.utilities.JrrClassUtils
import sun.misc.VM;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class VmUtilities {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    // not exit for java9+
    static void setAllowLoadArray(){
        JrrClassUtils.setFieldValue(VM,"allowArraySyntax",true);
        JrrClassUtils.setFieldValue(VM,"defaultAllowArraySyntax",true);
        assert VM.allowArraySyntax();
    }

}
