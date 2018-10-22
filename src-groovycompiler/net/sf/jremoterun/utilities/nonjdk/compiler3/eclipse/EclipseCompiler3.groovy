package net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse

import groovy.transform.CompileStatic
import org.eclipse.jdt.core.compiler.CompilationProgress
import org.eclipse.jdt.internal.compiler.ClassFile
import org.eclipse.jdt.internal.compiler.CompilationResult

import java.util.logging.Logger

@CompileStatic
class EclipseCompiler3 extends org.eclipse.jdt.internal.compiler.batch.Main{

//     private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<File, List<String>> myOutputs = new HashMap<>();
    public boolean rememberOutput;

    EclipseCompiler3(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map<String, String> customDefaultOptions, CompilationProgress compilationProgress,boolean rememberOutput) {
        super(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress)
        this.rememberOutput = rememberOutput
    }

    public void outputClassFiles(CompilationResult result) {
        super.outputClassFiles(result);
        if(rememberOutput) {
            if (result == null || result.hasErrors() && !proceedOnError) {
                return;
            }

            List<String> classFiles = new ArrayList<>();
            for (ClassFile file : result.getClassFiles()) {
                classFiles.add(new String(file.fileName()) + ".class");
            }
            myOutputs.put(new File(new String(result.getFileName())), classFiles);
        }
    }

    @Override
    boolean compile(String[] argv) {
        return super.compile(argv)
    }
}
