package net.sf.jremoterun.utilities.nonjdk.shell.telnet

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.shell.core.GroovyShellRunner2
import org.codehaus.groovy.tools.shell.IO

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class GroovyClientTelnetHandler extends GroovyShellRunner2 implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Socket s

    OutputStream nativeOutStream;
    InputStream nativeInStream;
    TelnetOutputStream telnetOutputStream
    TelnetInputStream telnetInputStream;

//    private static final List<String> DEFAULT_IMPORTS = Arrays.asList(
//            "java.util.*",
//            "static java.util.concurrent.TimeUnit.*"
//    );

    GroovyClientTelnetHandler(Socket s,Binding binding ) {
        super(binding)
        this.s = s
    }

    void run() {
        try {
            runConsole()
        } catch (IOException e) {
            log.info("ignore : ${e}")
        } catch (Throwable e) {
            Throwable e2 = JrrUtils.getRootException(e)
            log.log(Level.SEVERE, "${e}", e2)
        } finally {
            try {
                s.close();
            } catch (IOException e) {
                log.info("ignore : ${e}")
            }
        }
        log.info "stopped"
    }


    void runConsole() {
        customCreateListPackages();
        nativeOutStream = s.getOutputStream()
        nativeInStream = s.getInputStream()
        telnetInputStream = new TelnetInputStream(nativeInStream);
        telnetOutputStream = new TelnetOutputStream(nativeOutStream,this);
        telnetOutputStream.writeWONT(34); // linemode
        telnetOutputStream.writeWILL(1); // echo
        telnetOutputStream.writeWILL(3); // supress go ahead
        log.info "starting telnet service"
        writeStarting()
        nativeOutStream.flush()
        telnetOutputStream.flush()
        io = new IO(telnetInputStream, telnetOutputStream, telnetOutputStream)
        setDebug()
        createGroovyShell();
        sh.run((String) null);
        flushHistory()
        log.info "finished"

    }

    void writeStarting(){
        nativeOutStream.write("Starting groovy shell ...\r\n".getBytes())
    }

    @Override
    void displayWelcomeBanner2() {
        super.displayWelcomeBanner2()
        log.info "shell started"
    }
}
