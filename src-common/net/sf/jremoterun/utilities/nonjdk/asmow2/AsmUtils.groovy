package net.sf.jremoterun.utilities.nonjdk.asmow2

import groovy.transform.CompileStatic
import net.sf.jremoterun.SimpleJvmTiAgent
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.asmow2.verifier.AsmByteCodeVerifier
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.SimpleVerifier

import java.lang.instrument.ClassDefinition
import java.util.logging.Logger

@CompileStatic
class AsmUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    AsmByteCodeVerifier asmByteCodeVerifier = new AsmByteCodeVerifier(getClass().getClassLoader())

    @Deprecated
    ClassNode verifyByteCode(byte[] byteCode) {
        return asmByteCodeVerifier.verifyByteCode(byteCode)
    }


    static String convertClassPath(ClRef fromClass) {
        return fromClass.getClassName().replace('.', '/')
    }

    static String convertClassPath(Class fromClass) {
        return fromClass.getName().replace('.', '/')
    }

    SimpleRemapperUsed createRemapperForClass(ClRef fromClass, ClRef toClass) {
        Map<String, String> remapperMap = [:]
        remapperMap.put(convertClassPath(fromClass), convertClassPath(toClass));
        SimpleRemapperUsed remapper = new SimpleRemapperUsed(remapperMap)
        return remapper;
    }

    static byte[] readClass(Class clazz) {
        String path = convertClassPath(clazz) + '.class'
        InputStream stream = clazz.getClassLoader().getResourceAsStream(path)
        byte[] array = IOUtils.toByteArray(stream)
        stream.close()
        return array
    }

    boolean remapConstOnly = false

    void remapClassConstructor(Class inClass, Class childClass) {
        remapConstOnly = true
        remapClass(inClass, childClass.getSuperclass(), childClass)
    }

    void remapClass(Class inClass, Class fromClass, Class toClass) {
        byte[] clazz = readClass(inClass)
        byte[] res = remapClassNoRedefine(new ClassReader(clazz), fromClass, toClass)
        redefineClass(res, inClass)
    }

    ClassRemapper createClassRemapper(ClassWriter cw, SimpleRemapper remapper) {
        if (remapConstOnly) {
            return new ClassRemapperConst(cw, remapper);
        }
        return new ClassRemapper(cw, remapper)
    }

    byte[] makeMethodEmpty(ClassReader cr, String methodName, int argsCount) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        EmptyMethodClassVisitor classVisitor = new EmptyMethodClassVisitor(methodName, argsCount, cw);
        cr.accept(classVisitor, 0);
        byte[] b2 = cw.toByteArray();
        if (!classVisitor.found) {
            throw new Exception("Method not found : ${methodName} with args count = ${argsCount} in class ${cr.className}")
        }
        asmByteCodeVerifier.verifyByteCode(b2)
        return b2
    }

    byte[] remapClassNoRedefine(ClassReader cr, Class fromClass, Class toClass) {
        return remapClassNoRedefine(cr,new ClRef(fromClass),new ClRef(toClass))
    }

    byte[] remapClassNoRedefine(ClassReader cr, ClRef fromClass, ClRef toClass) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        SimpleRemapperUsed remapper = createRemapperForClass(fromClass, toClass)
        ClassRemapper classRemapper = createClassRemapper(cw, remapper)
        cr.accept(classRemapper, 0);
        byte[] b2 = cw.toByteArray();
        if (!remapper.used) {
            throw new Exception("Remapper was not used : ${remapper.mapping2}")
        }
        if (remapConstOnly) {
            asmByteCodeVerifier.verifyByteCode(b2)
        }
        return b2
    }




    void redefineClassWithVerify(final byte[] bs, Class class1) {
        String classPath = convertClassPath(class1)
        ClassNode classNode = asmByteCodeVerifier.verifyByteCode(bs)
//        log.info "validation passed"
        if (classNode.name != classPath) {
            throw new IllegalArgumentException("classes diff : ${classPath} , ${classNode.name}")
        }
        redefineClass(bs, class1)
    }

    static void redefineClass(final byte[] bs, Class class1)
            throws Exception {
        final ClassDefinition classDefinition = new ClassDefinition(class1, bs);
        final ClassDefinition[] classDefinitions = [classDefinition];
        if (SimpleJvmTiAgent.instrumentation == null) {
            throw new NullPointerException("SimpleJvmTiAgent.instrumentation is null");
        }
        SimpleJvmTiAgent.instrumentation.redefineClasses(classDefinitions);
    }


}
