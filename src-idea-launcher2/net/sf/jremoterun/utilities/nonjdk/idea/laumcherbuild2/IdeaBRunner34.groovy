package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.PidDetector
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.LauncherImpl

import java.lang.management.ManagementFactory
import java.util.logging.Logger

@CompileStatic
class IdeaBRunner34 implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void f1() {
        try {
            f2()
        } catch (Throwable e) {
            log.info("exc during compile : ${e}")
            throw e;
        }
    }

    static void f2() {
        String[] args = LauncherImpl.argsP
        int pid = PidDetector.detectPid()
        log.info "running, pid =  ${pid} , ${new Date()}, "
        log.info "running ${args} "
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments()
        log.info "inputArguments = ${inputArguments}"
        final String jpsClasspath = args[0];
        final String mainClassName = args[1];
        final String[] jpsArgs = new String[args.length - 2];
        System.arraycopy(args, 2, jpsArgs, 0, jpsArgs.length);

        final StringTokenizer tokenizer = new StringTokenizer(jpsClasspath, File.pathSeparator, false);
        final List<URL> urls = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            final String path = tokenizer.nextToken();
            urls.add(new File(path).toURI().toURL());
        }
        final URLClassLoader jpsLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), LauncherImpl.getClassLoader());
//        final GroovyClassLoader jpsLoader = JrrClassUtils.currentClassLoader as GroovyClassLoader;
//        urls.each {
//            jpsLoader.addURL(it)
//        }

        // IDEA-120811; speeding up DefaultChannelIDd calculation for netty
        //if (Boolean.parseBoolean(System.getProperty("io.netty.random.id"))) {
        System.setProperty("io.netty.machineId", "28:f0:76:ff:fe:16:65:0e");
        System.setProperty("io.netty.processId", Integer.toString(new Random().nextInt(65535)));
        System.setProperty("io.netty.serviceThreadPrefix", "Netty");
        //}

        final Class<?> mainClass = jpsLoader.loadClass(mainClassName);
        Thread.currentThread().setContextClassLoader(jpsLoader);
        JrrClassUtils.runMainMethod(mainClass, jpsArgs)

//        mainMethod.invoke(null, new Object[] {jpsArgs});
    }

    @Override
    void run() {
        f1();
    }
}
