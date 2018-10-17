package net.sf.jremoterun.utilities.nonjdk.classpath.tester

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class UndesiredClassLocationException extends Exception{

    UndesiredClassLocationException(String var1) {
        super(var1)
    }
}
