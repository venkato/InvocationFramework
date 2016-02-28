package net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompiler
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

    public EclipseJavaCompiler2C(CompilerConfiguration config, GroovyCompiler groovyCompiler) {
        this.config = config;
        this.groovyCompiler = groovyCompiler
    }

    @Override
    public void compile(List<String> files, CompilationUnit cu) {
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

        EclipseCompiler3 compiler3 = new EclipseCompiler3(writer, writer, false, null, null)
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

        return params.toArray(new String[params.size()]);
    }


}
