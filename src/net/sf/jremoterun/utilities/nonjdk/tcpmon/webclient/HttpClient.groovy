package net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The underlying network client used by WebClient.
 * <P>
 * Taken from Core Servlets and JavaServer Pages from Prentice Hall and Sun
 * Microsystems Press, http://www.coreservlets.com/. &copy; 2000 Marty Hall; may
 * be freely used or adapted.
 */

@CompileStatic
public class HttpClient extends NetworkClient {

private static final Logger log = LogManager.getLogger();

    // private String requestLine;
    private final String[] requestHeaders;

    private final MyTextArea outputArea;
    private Interruptible interruptible;
    private boolean stopDownload = false;

    public HttpClient(final String host, final int port,
            final String[] requestHeaders, final MyTextArea outputArea,Interruptible interruptible) throws UnknownHostException, IOException {
        super(host, port);
        // this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.outputArea = outputArea;
        this.interruptible=interruptible;
        if (checkHost(host)) {
            connect();
        }

    }

    @Override
    protected void handleConnection(final Socket uriSocket) throws IOException {
        try {
            final PrintWriter out = SocketUtil.getWriter(uriSocket);
            final InputStream inn = uriSocket.getInputStream();
            SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					outputArea.setText('');
				}
			});
            // out.println(requestLine);
            for (int i = 0; i < requestHeaders.length; i++) {
                if (requestHeaders[i] == null) {
                    break;
                } else {
                    out.println(requestHeaders[i]);
                }
            }
            if (WebClient.doNewLineCheckbox.getState()) {
                out.println();
            }

//            log.info('start reading');
            byte[] buffer = new byte[8000];
            long count = 0;
            int n = 0;
            while (-1 != (n = inn.read(buffer)) && !stopDownload) {
                String s = new String(buffer, 0, n);
//                log.info(s);
                outputArea.appendInSwingThread(s);
                count += n;
            }
            if (stopDownload) {
                outputArea
                        .appendInSwingThread('---- Download Interrupted ----');
            }
//            log.info('finish connecting');
        } catch (final Exception e) {
            outputArea.setText('Error: ' + e);
        }finally {
            interruptible.downloadFinish();
        }
    }

    private boolean checkHost(final String host) {
        try {
            InetAddress.getByName(host);
            return (true);
        } catch (final UnknownHostException uhe) {
            outputArea.setText('Bogus host: ' + host);
            return (false);
        }
    }

    public void stopDownload() {
        this.stopDownload = true;
    }


    public boolean isStopDownload() {
        return stopDownload;
    }
}
