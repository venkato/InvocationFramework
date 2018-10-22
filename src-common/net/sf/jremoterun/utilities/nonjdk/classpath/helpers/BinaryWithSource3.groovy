package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource2
import net.sf.jremoterun.utilities.classpath.ToFileRef2;

import java.util.logging.Logger;

@CompileStatic
class BinaryWithSource3 extends BinaryWithSource2{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    BinaryWithSource3(File binary, File source) {
        this(new FileToFileRef(binary),new FileToFileRef(source));
    }

    BinaryWithSource3(File binary, ToFileRef2 source) {
        this(new FileToFileRef(binary),source);
    }

    BinaryWithSource3(ToFileRef2 binary, ToFileRef2 source) {
        super(binary, source)
    }

    BinaryWithSource3(ToFileRef2 binary, List<ToFileRef2> source) {
        super(binary, source)
    }
}
