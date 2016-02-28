package net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse

import groovy.transform.CompileStatic
import org.eclipse.jdt.core.compiler.CompilationProgress

import java.util.logging.Logger

@CompileStatic
class EclipseCompiler3 extends org.eclipse.jdt.internal.compiler.batch.Main{

//     private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    EclipseCompiler3(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map<String, String> customDefaultOptions, CompilationProgress compilationProgress) {
        super(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress)
    }



}
