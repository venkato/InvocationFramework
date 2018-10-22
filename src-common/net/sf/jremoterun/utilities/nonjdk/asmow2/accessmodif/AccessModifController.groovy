package net.sf.jremoterun.utilities.nonjdk.asmow2.accessmodif

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.asmow2.AsmUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.verifier.AsmByteCodeVerifier
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.logging.Logger
import java.util.zip.ZipEntry

@CompileStatic
abstract class AccessModifController {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    AsmUtils asmUtils = new AsmUtils()
    AsmByteCodeVerifier asmByteCodeVerifier = new AsmByteCodeVerifier(getClass().getClassLoader())
    Set<String> makeClassPublic = new HashSet<>()
    Set<String> ignoreModifyClasses = new HashSet<>()
    Set<String> modifiedClasses = new HashSet<>()


    void addMakeClassPublic(ClRef clRef){
        makeClassPublic.add(clRef.getClassName().replace('.','/'))
    }


    void addIgnoreModifyClasses(ClRef clRef){
        ignoreModifyClasses.add(clRef.getClassName().replace('.','/'))
    }

    boolean needEntry(ZipEntry zipEntry) {
        return true
    }


    boolean needModifyClass(ClassReader className) {
        String find1 = ignoreModifyClasses.find { className.getClassName().startsWith(it) }
        if(find1==null){
            return true
        }
        return false
    }

    byte[] removeFinalModifier(String classNameWIthShash, byte[] bs) {
        final ClassReader reader = new ClassReader(bs);
        if (needModifyClass(reader)) {
            modifiedClasses.add(reader.getClassName().replace('/','.'))
            return removeFinalModifier2(reader, bs)
        }
        return bs
    }


    void onFinish() {

    }

    byte[] removeFinalModifier2(ClassReader reader, byte[] bs) {
        boolean isInterface = (reader.getAccess() & Opcodes.ACC_INTERFACE) > 0
        if (isInterface) {
            return bs;
        }
        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        reader.accept(createClassVisitor(reader, writer), 0);
        byte[] array = writer.toByteArray();
        if (needVerifyClass(reader)) {
            asmByteCodeVerifier.verifyByteCode(array)
        }
        return array
    }

    boolean needVerifyClass(ClassReader reader) {
        return false
    }

    String convertClassNameToShash(String className) {
        return className.replace('.', '/')
    }

    String convertClassNameToDot(String className) {
        return className.replace('/', '.')
    }

    ClassVisitor createClassVisitor(ClassReader reader, ClassWriter writer) {
        return new AccessClassVisitor(writer, this, reader)
    }

    boolean needMakeAllPublic(ClassReader className) {
        int access = className.getAccess()
        if (className.className.contains('$')) {
            boolean isFinal = (access & Opcodes.ACC_FINAL) > 0
            if (isFinal) {
                return true
            }
//            boolean isStatic = (access & Opcodes.ACC_STATIC) > 0
//            if (!isStatic) {
//                return true
//            }
            return false
        }
        boolean needMakeAllPublic = (access & Opcodes.ACC_FINAL) > 0
        return needMakeAllPublic
    }


    boolean decideAccessForInnderClass(ClassReader classReader, String name, int access) {
        String classFound = makeClassPublic.find {classReader.getClassName().startsWith(it)}
        if(classFound==null){
            return false
        }
        boolean isFinal = (access & Opcodes.ACC_FINAL) > 0
        if (isFinal) {
            return true
        }
//        boolean isStatic = (access & Opcodes.ACC_STATIC) > 0
//        if (!isStatic) {
//            return true
//        }
        return false
    }

    int modifyOpsCodeIfNeeded(int opcode, String owner, String methodName, String descriptor, boolean isInterface) {
        if (opcode == Opcodes.INVOKESPECIAL) {
            boolean need = isModifyOpsFromInvokeSpecialToVirtual(opcode,owner,methodName,descriptor,isInterface)
            if(need) {
                return Opcodes.INVOKEVIRTUAL
            }
        }
        return opcode
    }

    boolean isModifyOpsFromInvokeSpecialToVirtual(int opcode, String owner, String methodName, String descriptor, boolean isInterface) {
        boolean needpublic = needMakeMethodPublic(methodName, owner)
        return needpublic;
    }

    /**
     * @param className with separator: /
     */
    boolean isNeedMakeClassPublic(String className){
        className =  className.replace('.','/')
        String classFound = makeClassPublic.find {className.startsWith(it)}
        if(classFound==null){
            return false
        }
        return true

    }

    boolean needMakeMethodPublic(String methodName, String className) {
        isNeedMakeClassPublic(className)
    }

    boolean needMakeMethodPublic(String methodName, ClassReader classReader) {
        return needMakeMethodPublic(methodName, classReader.getClassName())
    }
}
