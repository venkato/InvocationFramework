package net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * A starting point for network clients. You'll need to override
 * handleConnection, but in many cases connect can remain unchanged. It uses
 * SocketUtil to simplify the creation of the PrintWriter and BufferedReader.
 * <P>
 * Taken from Core Servlets and JavaServer Pages from Prentice Hall and Sun
 * Microsystems Press, http://www.coreservlets.com/. &copy; 2000 Marty Hall; may
 * be freely used or adapted.
 */

@CompileStatic
public class NetworkClient {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    protected String host;

    protected int port;

    /**
     * Register host and port. The connection won't actually be established
     * until you call connect.
     */

    public NetworkClient(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Establishes the connection, then passes the socket to handleConnection.
     */

    public void connect() throws UnknownHostException, IOException {
//        try {
            final Socket client = new Socket(host, port);
            handleConnection(client);
//        } catch (final UnknownHostException uhe) {
//            System.out.println("Unknown host: " + host);
//            uhe.printStackTrace();
//        } catch (final IOException ioe) {
//            System.out.println("IOException: " + ioe);
//            ioe.printStackTrace();
//        }
    }

    /**
     * This is the method you will override when making a network client for
     * your task. The default version sends a single line
     * ("Generic Network Client") to the server, reads one line of response,
     * prints it, then exits.
     */

    protected void handleConnection(final Socket client) throws IOException {
        final PrintWriter out = SocketUtil.getWriter(client);
        final BufferedReader in = SocketUtil.getReader(client);
        out.println("Generic Network Client");
        log.info("Generic Network Client:\n" + "Made connection to "
                + host + " and got '" + in.readLine() + "' in response");
        client.close();
    }

    /** The hostname of the server we're contacting. */

    public String getHost() {
        return (host);
    }

    /** The port connection will be made on. */

    public int getPort() {
        return (port);
    }
}
