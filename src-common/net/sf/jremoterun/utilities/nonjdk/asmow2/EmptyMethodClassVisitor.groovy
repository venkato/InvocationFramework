package net.sf.jremoterun.utilities.nonjdk.asmow2

import groovy.transform.CompileStatic
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

@CompileStatic
class EmptyMethodClassVisitor extends ClassVisitor {

    String skipMethodName;
    int argCount;
    boolean found = false

    EmptyMethodClassVisitor(String skipMethodName, int argCount,final ClassVisitor classVisitor) {
        super(AsmConsoleDecompiler.asmCode,classVisitor)
        this.skipMethodName = skipMethodName
        this.argCount = argCount
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        Type[] types = Type.getArgumentTypes(descriptor)
        if (needVisitMethod(access, name, descriptor, signature, exceptions, types)) {
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }
        MethodVisitor visitMethodP = super.visitMethod(access, name, descriptor, signature, exceptions)
        visitMethodP.visitCode()
        visitMethodP.visitInsn(Opcodes.RETURN);
        visitMethodP.visitMaxs(0,0)
        visitMethodP.visitEnd()
//        return new EmptyMethodVisitor(api,visitMethodP)
        return null;
    }


    boolean needVisitMethod(int access, String name, String descriptor, String signature, String[] exceptions, Type[] types) {
        if (name == skipMethodName && types.length == argCount) {
            if (found) {
                throw new Exception("Found 2 or more methods : ${name} with args count : ${types.length}")
            }
            found = true
            return false
        }
        return true
    }
}
