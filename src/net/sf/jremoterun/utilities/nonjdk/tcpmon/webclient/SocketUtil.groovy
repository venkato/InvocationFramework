package net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient;

import groovy.transform.CompileStatic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A shorthand way to create BufferedReaders and PrintWriters associated with a
 * Socket.
 * <P>
 * Taken from Core Servlets and JavaServer Pages from Prentice Hall and Sun
 * Microsystems Press, http://www.coreservlets.com/. &copy; 2000 Marty Hall; may
 * be freely used or adapted.
 */

@CompileStatic
public class SocketUtil {

    /** Make a BufferedReader to get incoming data. */

    public static BufferedReader getReader(final Socket s) throws IOException {
        return (new BufferedReader(new InputStreamReader(s.getInputStream())));
    }

    /**
     * Make a PrintWriter to send outgoing data. This PrintWriter will
     * automatically flush stream when println is called.
     */

    public static PrintWriter getWriter(final Socket s) throws IOException {
        // 2nd argument of true means autoflush
        return (new PrintWriter(s.getOutputStream(), true));
    }
}
