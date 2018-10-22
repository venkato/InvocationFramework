package net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompiler
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompilerParams
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.messages.SimpleMessage
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.tools.javac.JavaCompiler
import org.eclipse.jdt.internal.compiler.batch.Main

import java.util.logging.Logger

@CompileStatic
public class EclipseJavaCompiler2C implements JavaCompiler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    private CompilerConfiguration config;
//    List<String> additionalFlags = []
    GroovyCompiler groovyCompiler;
    GroovyCompilerParams params;
    public boolean rememberOutput = false

    public EclipseJavaCompiler2C(CompilerConfiguration config, GroovyCompiler groovyCompiler,GroovyCompilerParams params) {
        this.config = config;
        this.groovyCompiler = groovyCompiler
        this.params = params;
        if(params==null){
            throw new NullPointerException('params is null');
        }
    }

    void printDebug(List<String> files){
        if(params.printPathNotContains.size()>0){
            List<String> file3 = files.findAll { isFileNotMatched(it) }
            if(file3.size()==0){
                log.info "tmpr filtered skip compiling files has zero files"
            }else {
                List<String> file4 = file3;
                if (file4.size() > params.logJavacInputFilesMaxSize) {
                    file4 = file4.subList(0, params.logJavacInputFilesMaxSize)
                }
                log.info "tmpr filtered skip compiling files ${file3.size()} : ${file4}"
            }
        }
        if(params.printPathContains.size()>0){
            List<String> file3 = files.findAll { isFileMatched(it) }
            if(file3.size()==0){
                log.info "filtered compiling files has zero files"
            }else {
                List<String> file4 = file3;
                if (file4.size() > params.logJavacInputFilesMaxSize) {
                    file4 = file4.subList(0, params.logJavacInputFilesMaxSize)
                }
                log.info "filtered compiling files ${file3.size()} : ${file4}"
            }
        }
    }

    boolean isFileNotMatched(String path1){
        String find1 = params.printPathNotContains.find { path1.contains(it) };
        return find1==null
    }

    boolean isFileMatched(String path1){
        String find1 = params.printPathContains.find { path1.contains(it) };
        return find1!=null
    }

    @Override
    public void compile(List<String> files, CompilationUnit cu) {
        log.info "compiling stubs ..."
        printDebug(files)
        String[] javacParameters = makeParameters(files, cu.getClassLoader());
        StringWriter javacOutput = new StringWriter();
        PrintWriter writer = new PrintWriter(javacOutput);
        boolean result = doCompile(writer, javacParameters)
//        log.info "result : ${result}"
        if (result) {
            String trim2 = javacOutput.toString().trim()
            if (trim2.length() > 0) {
                log.info "${trim2}";
            }
            log.info "compiling stubs finished fine"
        } else {
            String header = "Compile error \n${javacOutput}"
            cu.getErrorCollector().addFatalError(new SimpleMessage(header, cu));
//                addJavacError("Compile error during compilation with javac.", cu, javacOutput);
        }
    }

    boolean doCompile(PrintWriter writer, String[] javacParameters) {
        //new JavaCompiler3("javac", writer).compile3(javacParameters)
        URL compilerClassLocaton = JrrUtils.getClassLocation(Main)
//        log.info "compilerClassLocaton = ${compilerClassLocaton}"
//        log.info "compilerClassLocaton = ${JrrUtils.getClassLocation(org.eclipse.jdt.internal.compiler.apt.util.EclipseFileManager)}"

        EclipseCompiler3 compiler3 = new EclipseCompiler3(writer, writer, false, null, null,rememberOutput)
//        javacParameters = ['-help']
//        log.info "${Arrays.toString javacParameters}"
        boolean compile = compiler3.compile(javacParameters)
        return compile
    }



    private String[] makeParameters(List<String> files, GroovyClassLoader parentClassLoader) {
        Map options = config.getJointCompilationOptions();
        LinkedList<String> params = new LinkedList<String>();

        File target = config.getTargetDirectory();
        if (target == null) target = new File(".");

        params.add("-d");
        params.add(target.getAbsolutePath());
        params.add("-sourcepath");
        params.add(((File) options.get("stubDir")).getAbsolutePath());

        // add flags
        String[] flags = (String[]) options.get("flags");
        if (flags != null) {
            for (String flag : flags) {
                params.add('-' + flag);
            }
                log.info("has flags ${flags.toList()}")
//                throw new Exception("has flags ${flags.toList()}")
        }
        params.addAll(groovyCompiler.additionalFlags)

        boolean hadClasspath = false;
        // add namedValues
        String[] namedValues = (String[]) options.get("namedValues");
        if (namedValues != null) {
            for (int i = 0; i < namedValues.length; i += 2) {
                String name = namedValues[i];
                if (name.equals("classpath")) hadClasspath = true;
                params.add('-' + name);
                params.add(namedValues[i + 1]);
            }
        }

        // append classpath if not already defined
        if (!hadClasspath) {
            // add all classpaths that compilation unit sees
            StringBuilder resultPath = new StringBuilder(DefaultGroovyMethods.join((Iterable) config.getClasspath(), File.pathSeparator));
            ClassLoader cl = parentClassLoader;
            while (cl != null) {
                if (cl instanceof URLClassLoader) {
                    for (URL u : ((URLClassLoader) cl).getURLs()) {
                        try {
                            resultPath.append(File.pathSeparator);
                            resultPath.append(new File(u.toURI()).getPath());
                        } catch (URISyntaxException e) {
                            // ignore it
                        }
                    }
                }
                cl = cl.getParent();
            }

            params.add("-classpath");
            params.add(resultPath.toString());
        }else{
            log.info "has custom classpath"
//            throw new Exception("has classpath")
        }

        // files to compile
        params.addAll(files);
        if(this.params.printJavacArgs) {
            log.info "javacArgs : ${params}"
        }
        return params.toArray(new String[params.size()]);
    }


}
