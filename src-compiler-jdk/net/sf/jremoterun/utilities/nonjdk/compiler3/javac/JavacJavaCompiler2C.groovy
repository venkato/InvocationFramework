package net.sf.jremoterun.utilities.nonjdk.compiler3.javac

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompiler
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompilerParams
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.messages.SimpleMessage
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.tools.javac.JavaCompiler

import java.util.logging.Logger

@CompileStatic
public class JavacJavaCompiler2C implements JavaCompiler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    private CompilerConfiguration config;
//    List<String> additionalFlags = []
    GroovyCompiler groovyCompiler;
    GroovyCompilerParams params;

    public JavacJavaCompiler2C(CompilerConfiguration config, GroovyCompiler groovyCompiler,GroovyCompilerParams params) {
        this.config = config;
        this.groovyCompiler = groovyCompiler
        this.params = params;
        if(params==null){
            throw new NullPointerException('params is null')
        }
    }



    public void compile(List<String> files, CompilationUnit cu) {
        String[] javacParameters = makeParameters(files, cu.getClassLoader());
        int result;

        StringWriter javacOutput = new StringWriter();
        PrintWriter writer = new PrintWriter(javacOutput);
        result = doCompile( writer,javacParameters)
//        log.info "result : ${result}"
        switch (result) {
            case 0:
                String trim2 = javacOutput.toString().trim()
                if(trim2.length()>0) {
                    log.info "${trim2}";
                }
                break;
            case 1:
                addJavacError("Compile error during compilation with javac.", cu, javacOutput);
                break;
            case 2:
                addJavacError("Invalid commandline usage for javac.", cu, javacOutput);
                break;
            case 3:
                addJavacError("System error during compilation with javac.", cu, javacOutput);
                break;
            case 4:
                addJavacError("Abnormal termination of javac.", cu, javacOutput);
                break;
            default:
                addJavacError("unexpected return value by javac.", cu, javacOutput);
                break;
        }
    }

    int doCompile(  PrintWriter writer,String[] javacParameters ){
        return new JavaCompiler3("javac", writer).compile3(javacParameters)
    }


    private static void addJavacError(String header, CompilationUnit cu, StringWriter msg) {
        if (msg != null) {
            header = header + "\n" + msg.getBuffer().toString();
        } else {
            header = header +
                    "\nThis javac version does not support compile(String[],PrintWriter), " +
                    "so no further details of the error are available. The message error text " +
                    "should be found on System.err.\n";
        }
        cu.getErrorCollector().addFatalError(new SimpleMessage(header, cu));
    }

    private String[] makeParameters(List<String> files, GroovyClassLoader parentClassLoader) {
        Map options = config.getJointCompilationOptions();
        LinkedList<String> paras = new LinkedList<String>();

        File target = config.getTargetDirectory();
        if (target == null) target = new File(".");

        paras.add("-d");
        paras.add(target.getAbsolutePath());
        paras.add("-sourcepath");
        paras.add(((File) options.get("stubDir")).getAbsolutePath());

        // add flags
        String[] flags = (String[]) options.get("flags");
        if (flags != null) {
            for (String flag : flags) {
                paras.add('-' + flag);
            }
        }
        paras.addAll(groovyCompiler.additionalFlags)

        boolean hadClasspath = false;
        // add namedValues
        String[] namedValues = (String[]) options.get("namedValues");
        if (namedValues != null) {
            for (int i = 0; i < namedValues.length; i += 2) {
                String name = namedValues[i];
                if (name.equals("classpath")) hadClasspath = true;
                paras.add('-' + name);
                paras.add(namedValues[i + 1]);
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

            paras.add("-classpath");
            paras.add(resultPath.toString());
        }

        // files to compile
        paras.addAll(files);
        if(this.params.printJavacArgs) {
            log.info "javacArgs : ${params}"
        }
        return paras.toArray(new String[paras.size()]);
    }


}
