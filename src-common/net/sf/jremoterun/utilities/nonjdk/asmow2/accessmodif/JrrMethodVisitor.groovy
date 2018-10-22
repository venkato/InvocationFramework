package net.sf.jremoterun.utilities.nonjdk.asmow2.accessmodif

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.objectweb.asm.MethodVisitor;

import java.util.logging.Logger;

@CompileStatic
class JrrMethodVisitor extends MethodVisitor{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AccessModifController accessModifController

    JrrMethodVisitor(int api, MethodVisitor methodVisitor,AccessModifController accessModifController) {
        super(api, methodVisitor)
        this.accessModifController = accessModifController
    }


    int modifyOpsCodeIfNeeded(int opcode, String owner, String name, String descriptor, boolean isInterface){
        return accessModifController.modifyOpsCodeIfNeeded(opcode, owner, name, descriptor, isInterface)
    }

    @Override
    void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        opcode = modifyOpsCodeIfNeeded(opcode, owner, name, descriptor, isInterface)
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }
}
