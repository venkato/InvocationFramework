package net.sf.jremoterun.utilities.nonjdk.decompiler

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import org.jboss.windup.decompiler.fernflower.FernflowerJDKLogger
import org.jetbrains.java.decompiler.main.ClassesProcessor
import org.jetbrains.java.decompiler.main.DecompilerContext
import org.jetbrains.java.decompiler.main.Fernflower
import org.jetbrains.java.decompiler.struct.StructClass
import org.jetbrains.java.decompiler.struct.StructContext

import java.util.logging.Logger

@CompileStatic
class FernflowerDecompiler2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Fernflower fernflower
    DecompierHelper decompierHelper = new DecompierHelper()
    FernflowerJDKLogger logger = new FernflowerJDKLogger()
    StructContext structContext
    ClassesProcessor processor
    DecompilerAddFiles addFiles

    FernflowerDecompiler2() {
        this([:])
    }

    /**
     *
     * @see org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences#DEFAULTS
     */
    FernflowerDecompiler2(Map options) {
        assert options!=null
        fernflower = new Fernflower(decompierHelper, decompierHelper, options, logger)
        structContext = fernflower.getStructContext()

        addFiles = new DecompilerAddFiles(structContext)
        init()
    }

    void init(){
        processor = new ClassesProcessor(structContext);
        fernflower.@classesProcessor = processor;
        DecompilerContext.setClassProcessor(processor);
        DecompilerContext.setStructContext(structContext);
    }

    String decompile(File jar, ClRef className) {
        return decompile(jar, className.className)
    }

    String decompile(File source, String className) {
        try {
            decompileImpl(source, className)
        }finally {
            fernflower.clearContext()
        }
    }

    String decompileImpl(File source, String className) {
        assert source.exists()
//        structContext.addSpace(source, false)
        structContext.addSpace(source, true)
        init()
        className = normalizeClassName(className)
        StructClass clazz = structContext.getClass(className)
        if (clazz == null) {
            throw new ClassNotFoundException("${className}")
        }
        log.info "starting decompiling ..."
        String content = fernflower.getClassContent(clazz)
//        log.info "${content}"
        return content
    }


    static String normalizeClassName(String className) {
        if (className.contains('\\') && className.endsWith('.class')) {
            className = className.replace('\\', '/')
        } else if (!className.contains('/') && className.contains('.')) {
            className = className.replace('.', '/')
        }
        if (className.endsWith('.class')) {
            className = className.replace('.class', '')
        }
        return className
    }


}
