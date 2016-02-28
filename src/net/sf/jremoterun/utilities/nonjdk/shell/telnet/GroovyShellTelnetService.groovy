// Initial version : https://github.com/ivnik/telnet-groovysh

package net.sf.jremoterun.utilities.nonjdk.shell.telnet

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.shell.core.GroovyShellRunner2

import java.util.logging.Level
import java.util.logging.Logger

/**
 * use me.bazhenov.groovysh.GroovyShellService as this can't handle backspace ??
 * seems there can't handle too
 */

@CompileStatic
public class GroovyShellTelnetService implements Runnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    int listenPort;
    boolean listnerAllIntrfaces = true

    ServerSocket ss;
    volatile boolean isRunning = true;

    Thread socketAcceptThread
    Binding binding2 = new Binding()

    GroovyShellTelnetService(int listenPort) {
        this.listenPort = listenPort
    }

    ServerSocket createServerSocket() {
        if (listnerAllIntrfaces) {
            return new ServerSocket(listenPort, 0);
        } else {
            InetAddress inetAddress = InetAddress.getByName("127.0.0.1")
            return new ServerSocket(listenPort, 0, inetAddress);
        }
    }

    void start() throws IOException {
        ss = createServerSocket()
        socketAcceptThread = new Thread(this, "Groovy server telnet shell")
        socketAcceptThread.start()
        log.info "server started on port ${listenPort}"
    }

    void stop() throws IOException {
        isRunning = false;
        ss.close();
    }


    @Override
    void run() {
        while (isRunning) {
            try {
                final Socket s = ss.accept();
                handleNewConnection(s)
            } catch (Throwable t) {
                log.log(Level.SEVERE,"Error: ${t}", t);
            }
        }
        log.info "stopped"
    }

    void handleNewConnection(final Socket s ){
        log.info "received new client : ${s.inetAddress}"
        Thread thread1 = new Thread(new GroovyClientTelnetHandler(s, binding2), "Groovy client handler");
        thread1.start()
    }


    void varPutAll(Map<String,Object>  vars){
        binding2.getVariables().putAll(vars)
    }


    void varPut(String varName, Object varValue){
        binding2.setVariable(varName,varValue)
    }

    static int debugPort = 8872


    static void main(String[] args) throws IOException {
        GroovyShellRunner2.customCreateListPackages()
        GroovyShellTelnetService groovyShellService = new GroovyShellTelnetService(debugPort)
        groovyShellService.start();
    }
}