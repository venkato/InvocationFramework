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
import static org.apache.commons.net.telnet.TelnetCommand.SB
import static org.apache.commons.net.telnet.TelnetCommand.SE
import static org.apache.commons.net.telnet.TelnetCommand.WILL
import static org.apache.commons.net.telnet.TelnetCommand.WONT;


@CompileStatic
class TelnetInputStream extends InputStream {
    private final InputStream is;

    public TelnetInputStream(final InputStream is) {
        this.is = is;
    }

    private void readTillIACSE() throws IOException {
        boolean gotIAC = false;

        while (true) {
            int x = is.read();

            switch (x) {
                case IAC:
                    gotIAC = true;
                    break;
                case SE:
                    if (gotIAC) {
                        return;
                    }
                default:
                    gotIAC = false;
            }
        }
    }

    private void handleWILL() throws IOException {
        int opt = is.read();
    }

    private void handleWONT() throws IOException {
        int opt = is.read();
    }

    private void handleDO() throws IOException {
        int opt = is.read();
    }

    private void handleDONT() throws IOException {
        int opt = is.read();
    }

    private void handleSB() throws IOException {
        readTillIACSE();
    }

    @Override
    public int available() throws IOException {
        return is.available();
    }

    @Override
    public int read() throws IOException {
        while (true) {
            int x = is.read();


            if (x == IAC) {
                int cmd = is.read();

                switch (cmd) {
                    case IAC:
                        return IAC;
                    case WILL:
                        handleWILL();
                        break;
                    case WONT:
                        handleWONT();
                        break;
                    case DO:
                        handleDO();
                        break;
                    case DONT:
                        handleDONT();
                        break;
                    case SB:
                        handleSB();
                        break;
                    default:
                        // skip
                        break;
                }
            } else {
                return x;
            }
        }
    }
}
