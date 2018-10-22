package net.sf.jremoterun.utilities.nonjdk.asmow2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.MethodRemapper
import org.objectweb.asm.commons.Remapper
import org.objectweb.asm.commons.SimpleRemapper;

import java.util.logging.Logger;

@CompileStatic
class MethodRemapperConst extends MethodRemapper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Remapper remapper2

//    public boolean used = false

    MethodRemapperConst(MethodVisitor methodVisitor, Remapper remapper2) {
        super(methodVisitor, new SimpleRemapper([:]))
        this.remapper2 = remapper2
    }


    @Override
    void visitTypeInsn(int opcode, String type) {
        String res = remapper2.mapType(type)
//        if (type != res) {
//            used = true
//        }
        super.visitTypeInsn(opcode, res);
    }


    @Override
    void visitMethodInsn(final int opcodeAndSource, final String owner, final String name, final String descriptor, final boolean isInterface) {
        visitMethodInsnImpl(opcodeAndSource, owner, name, descriptor, isInterface);
    }

    void visitMethodInsnSuper(final int opcodeAndSource, final String owner, final String name, final String descriptor, final boolean isInterface) {
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
    }

    void visitMethodInsnImpl(final int opcodeAndSource, String owner, final String name, final String descriptor, final boolean isInterface) {
        boolean b = name == '<init>'
        if (b) {
            owner = remapper2.mapType(owner)
//            if (owner != res) {
//                used = true
//            }
//            owner = res;
        }
        visitMethodInsnSuper(opcodeAndSource, owner, name, descriptor, isInterface);
    }
}
