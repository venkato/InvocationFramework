package net.sf.jremoterun.utilities.nonjdk.shell.telnet;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic

import static org.apache.commons.net.telnet.TelnetCommand.DO
import static org.apache.commons.net.telnet.TelnetCommand.DONT
import static org.apache.commons.net.telnet.TelnetCommand.IAC
import static org.apache.commons.net.telnet.TelnetCommand.IAC
import static org.apache.commons.net.telnet.TelnetCommand.IAC
import static org.apache.commons.net.telnet.TelnetCommand.IAC
import static org.apache.commons.net.telnet.TelnetCommand.WILL
import static org.apache.commons.net.telnet.TelnetCommand.WONT;


@CompileStatic
public class TelnetOutputStream extends OutputStream {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private final OutputStream os;
    private int prev = -1;
    public volatile int errorWriteCount = 0
    public GroovyClientTelnetHandler groovyClientTelnetHandler;

    TelnetOutputStream(final OutputStream os,GroovyClientTelnetHandler groovyClientTelnetHandler) {
        this.os = os;
        this.groovyClientTelnetHandler = groovyClientTelnetHandler
    }



    public void writeDO(final int opt) throws IOException {
        byte[] bs = [IAC,DO,opt]
        os.write(bs);
    }

    public void writeDONT(final int opt) throws IOException {
        byte[] bs = [IAC,DONT,opt]
        os.write(bs);
    }

    public void writeWILL(final int opt) throws IOException {
        byte[] bs = [IAC,WILL,opt]
        os.write(bs);
    }

    public void writeWONT(final int opt) throws IOException {
        byte[] bs = [IAC,WONT,opt]
        os.write(bs);
    }


    @Override
    public void write(final int b) throws IOException {
        try {
            switch (b) {
                case 0x0a:
                    if (prev != 0x0d) {
                        os.write(0x0d);
                    }
                default:
                    prev = b;
                    os.write(b);
            }
        }catch (java.net.SocketException se){
            errorWriteCount++;
            log.info "got ${se} errorWriteCount :${errorWriteCount}, closing "
            groovyClientTelnetHandler.sh.getRunner().running = false
            throw se
        }
    }

}
