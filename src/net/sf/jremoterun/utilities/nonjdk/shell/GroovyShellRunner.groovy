package net.sf.jremoterun.utilities.nonjdk.shell

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.st.GroovyMethodRunnerParams2
import net.sf.jremoterun.utilities.nonjdk.shell.telnet.GroovyShellTelnetService
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.tools.shell.Groovysh
import org.codehaus.groovy.tools.shell.IO

import java.util.logging.Logger

@CompileStatic
class GroovyShellRunner implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    @Override
    void run() {
        GroovyShellConsole3 runner2 = new GroovyShellConsole3()
        runner2.runConsole()
    }

    static void run1(List<String> args) {
        org.codehaus.groovy.tools.shell.Main.main(args.toArray(new String[0]))
    }

    static void runConsole(String firstComand,Binding binding ) {
        GroovyShellConsole3 runner2 = new GroovyShellConsole3()
        runner2.runConsole()
    }

    static GroovyShellTelnetService runServer2(int port) {
        GroovyShellTelnetService service = new GroovyShellTelnetService(port);
        service.start()
        return service;
    }

    /**
     * use me.bazhenov.groovysh.GroovyShellService
     * @param port
     */
    @Deprecated
    static void runServer(int port) {
        ServerSocket serverSocket = new ServerSocket(port)
        log.info "server started on ${port}"
        while (true) {
            log.info "waiting connection"
            Socket socket = serverSocket.accept()
            log.info "got connection"
            OutputStream out = socket.outputStream
            IO io = new IO(socket.inputStream, out, out)
            Groovysh groovysh = new Groovysh(io)
            groovysh.run(null)
        }

    }

    static void runClient(String host, int port) {
        Socket socket = new Socket(host, port)
        InputStream inputStream = socket.inputStream
        OutputStream outputStream = socket.outputStream
        Runnable rIn = { IOUtils.copy(inputStream, System.out) }
        Runnable rOut = { IOUtils.copy(System.in, outputStream) }
        new Thread(rIn).start()
        new Thread(rOut).start()
        log.info "connected"
        Thread.sleep(Long.MAX_VALUE)
    }

//    public static Thread consumeProcessOutputStream(InputStream inn, OutputStream output) {
//        Thread thread = new Thread(new ProcessGroovyMethods.ByteDumper(inn, output));
//        thread.start();
//        return thread;
//    }


}
