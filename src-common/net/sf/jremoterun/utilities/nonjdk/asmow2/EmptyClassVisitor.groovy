package net.sf.jremoterun.utilities.nonjdk.asmow2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes;

import java.util.logging.Logger;

@CompileStatic
class EmptyClassVisitor extends ClassVisitor{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    EmptyClassVisitor() {
        super(AsmConsoleDecompiler.asmCode)
    }

    EmptyClassVisitor(int api) {
        super(api)
    }

    EmptyClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }
}
