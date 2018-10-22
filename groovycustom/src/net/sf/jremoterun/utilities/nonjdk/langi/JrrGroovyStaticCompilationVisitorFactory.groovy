package net.sf.jremoterun.utilities.nonjdk.langi

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.sc.StaticCompilationVisitor
import org.codehaus.groovy.transform.sc.StaticCompileTransformation
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor

import java.util.logging.Logger

@CompileStatic
class JrrGroovyStaticCompilationVisitorFactory {

    private static final Logger log = Logger.getLogger(JrrGroovyStaticCompilationVisitorFactory.name);

    public static boolean useImprovedStaticCompiler = true;
    public static String useImprovedCompilerProp = "groovy.useImprovedCompiler"

    public static boolean loadJrrClassUtilsTryied = false
    public static ClassLoader cl = JrrGroovyStaticCompilationVisitorFactory.getClassLoader()


    static boolean getPropValueSetting() {
        String propsValue = System.getProperty(useImprovedCompilerProp);
        if (propsValue == null) {
            return true
        }
        return "true".equalsIgnoreCase(propsValue)
    }

    static {
        useImprovedStaticCompiler = getPropValueSetting()
    }


    StaticTypeCheckingVisitor create(StaticCompileTransformation transformation,
                                     final SourceUnit unit, final ClassNode node) {
        if (useImprovedStaticCompiler) {
            if (!loadJrrClassUtilsTryied) {
                tryLoadJrrClassUtils();
                tryLoadJrrCastRuntime();
                tryLoadJrrCastASTTransformation();
            }
            return new JrrStaticCompilationVisitor(unit, node);
        }
        return new StaticCompilationVisitor(unit, node);
    }

    static void tryLoadJrrClassUtils() {
        loadJrrClassUtilsTryied = true
        try {
            Class clazz = cl.loadClass('net.sf.jremoterun.utilities.JrrFieldAccessorSetter')
            Runnable r = clazz.newInstance() as Runnable;
            r.run();
        } catch (ClassNotFoundException e) {
            log.fine(e.getMessage())
        }

    }

    static void tryLoadJrrCastRuntime() {
        try {
            Class clazz = cl.loadClass('net.sf.jremoterun.utilities.groovystrans.JrrCastRuntime')
            Runnable r = clazz.newInstance() as Runnable;
            r.run();
        } catch (ClassNotFoundException e) {
            log.fine(e.getMessage())
        }

    }

    static void tryLoadJrrCastASTTransformation() {
        try {
            Class clazz = cl.loadClass('net.sf.jremoterun.utilities.groovystrans.JrrCastASTTransformation')
            Runnable r = clazz.newInstance() as Runnable;
            r.run();
        } catch (ClassNotFoundException e) {
            log.fine(e.getMessage())
        }

    }

}
