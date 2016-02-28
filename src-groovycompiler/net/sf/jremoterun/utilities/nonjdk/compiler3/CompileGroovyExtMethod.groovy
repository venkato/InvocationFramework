package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class CompileGroovyExtMethod extends CompileRequestClient {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File outputDir = new File(ifDir, "build/logger-ext-methods");
    GroovyCompilerParams params = new GroovyCompilerParams()

    CompileGroovyExtMethod(File ifDir) {
        super(ifDir)
    }

    @Override
    void addExtMethodsDir() {
    }


    void compileExtMethods() {
        params.addInDir new File(ifDir, "src-logger-ext-methods");
        params.outputDir = outputDir
        params.outputDir.mkdirs()
        params.javaVersion = '1.6'
        compile(params)
    }

    static File createGroovyExtMethodDir(File ifDir2) {
        return new File(ifDir2, 'resources-groovy')
    }

}
