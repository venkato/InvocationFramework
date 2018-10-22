package net.sf.jremoterun.utilities.nonjdk.asmow2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.FieldRemapper
import org.objectweb.asm.commons.Remapper
import org.objectweb.asm.commons.SimpleRemapper;

import java.util.logging.Logger;

@CompileStatic
class ClassRemapperConst extends ClassRemapper{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Remapper remapper2

    MethodRemapperConst methodRemapperConst

    ClassRemapperConst(ClassVisitor classVisitor, Remapper remapper2) {
        super(classVisitor, new SimpleRemapper([:]))
        this.remapper2 = remapper2
    }

    @Override
    protected FieldVisitor createFieldRemapper(FieldVisitor fieldVisitor) {
        return new FieldRemapper(fieldVisitor, remapper);
    }

    @Override
    protected MethodVisitor createMethodRemapper(MethodVisitor methodVisitor) {
        methodRemapperConst = new MethodRemapperConst(methodVisitor, remapper2)
        return methodRemapperConst
    }
}
