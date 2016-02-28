package net.sf.jremoterun.utilities.nonjdk.groovyruntime

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.classgen.asm.InvocationWriter
import org.codehaus.groovy.classgen.asm.MethodCaller
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandlingClassCast

import java.util.logging.Logger

@CompileStatic
class ImproveGroovyRuntime implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        f1()
    }

    static void f1() {
        InvocationWriter.@castToClassMethod = MethodCaller.newStatic(ShortTypeHandlingClassCast, "castToClass2");

    }


}
