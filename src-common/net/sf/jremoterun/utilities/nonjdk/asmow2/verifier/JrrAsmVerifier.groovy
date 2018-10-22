package net.sf.jremoterun.utilities.nonjdk.asmow2.verifier

import groovy.transform.CompileStatic;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.SimpleVerifier;

/**
 * This verifier load classes during verification in method  getClass(Type type)
 */
@CompileStatic
public class JrrAsmVerifier extends SimpleVerifier {

    ClassLoader classLoader1;

    JrrAsmVerifier(int api, Type currentClass, Type currentSuperClass, List<Type> currentClassInterfaces, boolean isInterface, ClassLoader classLoader1) {
        super(api, currentClass, currentSuperClass, currentClassInterfaces, isInterface)
        setClassLoader(classLoader1);
    }

    public JrrAsmVerifier(ClassLoader classLoader1) {
        this(org.objectweb.asm.Opcodes.ASM7, null, null, null, false, classLoader1);

    }

    @Override
    void setClassLoader(ClassLoader loader) {
        super.setClassLoader(loader)
        this.classLoader1 = loader
    }

    Class<?> getClass(final Type type) {
        Thread currentThread = Thread.currentThread()
        ClassLoader classLoaderBefore = currentThread.getContextClassLoader()
        try {
            currentThread.setContextClassLoader(classLoader1)
            return super.getClass(type)
        } finally {
            currentThread.setContextClassLoader(classLoaderBefore)
        }
    }


}
