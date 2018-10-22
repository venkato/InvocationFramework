package net.sf.jremoterun.utilities.nonjdk.asmow2.accessmodif

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.AsmConsoleDecompiler
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes

import java.util.logging.Logger;

@CompileStatic
class AccessClassVisitor extends ClassVisitor {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    boolean needMakeAllPublic
    boolean needMakeAllPublicTmp
    AccessModifController accessModifController
    ClassReader classReader
//    String innerClass

    AccessClassVisitor(ClassVisitor classVisitor, AccessModifController accessModifController, ClassReader classReader) {
        super(AsmConsoleDecompiler.asmCode, classVisitor);
        this.accessModifController = accessModifController
        this.classReader = classReader
        needMakeAllPublic = accessModifController.needMakeAllPublic(classReader)
    }


    @Override
    void visit(final int version, int access, final String name, final String signature, final String superName, final String[] interfaces) {
        boolean isInnerClass = name.contains('$');
        if (isInnerClass) {
            boolean b = accessModifController.decideAccessForInnderClass(classReader, name, access)
            if (b) {
                needMakeAllPublicTmp = true
//                access = removeFinalModifierAndMakePublic(access)
            }
        }
//        log.info "${name} needMakeAllPublicTmp = ${needMakeAllPublicTmp}, needMakeAllPublic = ${needMakeAllPublic}"
        if (needMakeAllPublicTmp || needMakeAllPublic || !isInnerClass) {
            access = removeFinalModifierAndMakePublic(access);
        } else {
            access = removeFinalModifier(access);
        }

//        log.info "visit class begin ${name}"
        super.visit(version, access, name, signature, superName, interfaces);
//        log.info "visit class end ${name}"
    }

    @Override
    void visitOuterClass(String owner, String name, String descriptor) {
//        log.info "visitOuterClass ${name} begin"
        super.visitOuterClass(owner, name, descriptor)
//        log.info "visitOuterClass ${name} end"
    }

    @Override
    void visitEnd() {
//        log.info "visitEnd"
//        innerClass = null
        needMakeAllPublicTmp = false
        super.visitEnd()
    }


    @Override
    void visitInnerClass(String name, String outerName, String innerName, int access) {
//        boolean isFinal = access & ~Opcodes.ACC_FINAL;
//        if (isFinal || needMakeAllPublicTmp || needMakeAllPublic) {
        access = removeFinalModifierAndMakePublic(access)
//        } else {
//            access = removeFinalModifier(access)
//        }
//        log.info("inner ${innerName} ${needMakeAllPublicTmp}")
        super.visitInnerClass(name, outerName, innerName, access);
    }


//    @Override
//    void visitInnerClass(String name, String outerName, String innerName, int access) {
////        visitInnerClass = true
////        innerClass = innerName
//        boolean b = accessModifController.decideAccessForInnderClass(classReader, name, outerName, innerName, access)
//        if (name.contains("RSyntaxTextArea")) {
//            log.info "innter ${innerName} : ${b} begin"
//        }
//        if (b) {
//            needMakeAllPublicTmp = true
//            access = removeFinalModifierAndMakePublic(access)
//        }
//        super.visitInnerClass(name, outerName, innerName, access);
//        log.info "innter ${innerName} : ${b} end"
//    }



    @Override
    MethodVisitor visitMethod(int access, final String name, final String desc, final String signature, final String[] exceptions) {
        return createMethodVisitor(access, name, desc, signature, exceptions);
    }

    MethodVisitor createMethodVisitor(int access, final String name, final String desc, final String signature, final String[] exceptions){
        int access1 = handleMethodAccess(access, name);
        MethodVisitor methodVisitorParent = visitMethodI(access1, name, desc, signature, exceptions);
        JrrMethodVisitor jrrMethodVisitor = new JrrMethodVisitor(api,methodVisitorParent,accessModifController)
        return jrrMethodVisitor
    }


    @Override
    FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        access = handleFieldAccess(access, name)
        return super.visitField(access, name, descriptor, signature, value)
    }

    int handleFieldAccess(int access, String name) {
//        log.info "handle ${}"
        access = handleMemberAccess(access)
        return access
    }

    public static String contsructorMethodName = '<init>'


    int handleMethodAccess(int access, String name) {
        access = handleMemberAccess(access)
        boolean needPublic=name == contsructorMethodName
        if(!needPublic) {
            needPublic = accessModifController.needMakeMethodPublic(name, classReader)
        }
        if (needPublic) {

            access = makePublic(access)
        }
        return access
    }

    int handleMemberAccess(int access) {
        access = removeFinalModifier(access)
        boolean b = needMakeAllPublic || needMakeAllPublicTmp || (access & Opcodes.ACC_PROTECTED) > 0
        if (b) {
            access = makePublic(access)
        }
        return access

    }

    int makePublic(int access) {
        access = access & ~Opcodes.ACC_PRIVATE; ;
        access = access & ~Opcodes.ACC_PROTECTED; ;
        access = access | Opcodes.ACC_PUBLIC;
        return access
    }


    int removeFinalModifierAndMakePublic(int access) {
        access = removeFinalModifier(access)
        access = makePublic(access)
        return access
    }

    int removeFinalModifier(int access) {
        return access & ~Opcodes.ACC_FINAL;
    }


    void visitI(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }


    void visitInnerClassI(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }


    MethodVisitor visitMethodI(int access, final String name, final String desc, final String signature, final String[] exceptions) {
        return super.visitMethod(access, name, desc, signature, exceptions);
    }


    FieldVisitor visitFieldI(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access, name, descriptor, signature, value)
    }

}
