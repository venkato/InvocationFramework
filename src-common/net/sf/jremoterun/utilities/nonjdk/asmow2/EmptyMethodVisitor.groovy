package net.sf.jremoterun.utilities.nonjdk.asmow2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.objectweb.asm.MethodVisitor;

import java.util.logging.Logger;

@CompileStatic
class EmptyMethodVisitor extends MethodVisitor{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    EmptyMethodVisitor(int api) {
        super(api)
    }

    EmptyMethodVisitor(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor)
    }



    @Override
    void visitCode() {
    //    Thread.dumpStack()
        mv = null
//        super.visitCode()
    }
}
