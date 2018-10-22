package net.sf.jremoterun.utilities.nonjdk.asmow2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.util.Printer
import org.objectweb.asm.util.TraceMethodVisitor;

import java.util.logging.Logger;

@CompileStatic
class AsmConsoleDecompilerVisitor extends ClassVisitor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String decompileMethod
    Printer textifier;

    public boolean found = false

    AsmConsoleDecompilerVisitor(int api, ClassVisitor classVisitor, String decompileMethod, Printer textifier) {
        super(api, classVisitor)
        this.decompileMethod = decompileMethod
        this.textifier = textifier
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == decompileMethod) {
            found = true
//            String sig = signature == null ? "" : signature
            textifier.text.add("Begin ${name} ${descriptor} \n".toString())
            return new TraceMethodVisitor(methodVisitor, textifier)
        }
        return methodVisitor
    }
}
