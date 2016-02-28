package net.sf.jremoterun.utilities.nonjdk.tcpmon;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea;
import org.apache.logging.log4j.LogManager;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * a connection listens to a single current connection
 */
@CompileStatic
class Connection extends Thread {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();

    public Listener listener;

    public boolean active;

    public String fromHost;

    public String time;

    public MyTextArea inputText = new MyTextArea(null, null, 20, 80);

    public RTextScrollPane inputScroll = null;

    public MyTextArea outputText = new MyTextArea(null, null, 20, 80);

    public RTextScrollPane outputScroll = null;

    public Socket inSocket = null;

    public Socket outSocket = null;

    public Thread clientThread = null;

    public Thread serverThread = null;

    public SocketRR rr1 = null;

    public SocketRR rr2 = null;

    public InputStream inputStream = null;

    public String HTTPProxyHost = null;

    public int HTTPProxyPort = 80;

    public JTextField requestEncoding;

    public JTextField responseEncoding;

    public SlowLinkSimulator slowLink;

    public JComboBox styleIn;

    public JComboBox styleOut;

    // not used
    private void init(final Listener l) {
        Tcpmon.setTextArea(inputText);
        Tcpmon.setTextArea(outputText);
        listener = l;
        HTTPProxyHost = l.HTTPProxyHost;
        HTTPProxyPort = l.HTTPProxyPort;
        slowLink = l.slowLink;
        styleIn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                inputText.setSyntaxEditingStyle(Tcpmon.syntaxisFields
                        .get(styleIn.getSelectedItem()));

            }

        });
        styleOut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                outputText.setSyntaxEditingStyle(Tcpmon.syntaxisFields
                        .get(styleOut.getSelectedItem()));

            }

        });
        inputText.setSyntaxEditingStyle(Tcpmon.syntaxisFields
                .get(styleIn.getSelectedItem()));
        outputText.setSyntaxEditingStyle(Tcpmon.syntaxisFields
                .get(styleOut.getSelectedItem()));
    }

    public void setInputTextStyle(final String style) {
        inputText.setSyntaxEditingStyle(style);
    }

    public void setOutputTextStyle(final String style) {
        outputText.setSyntaxEditingStyle(style);
    }

    public Connection(final Listener l, final Socket s,
                      final JTextField requestEncoding,
                      final JTextField responseEncoding, final JComboBox styleIn,
                      final JComboBox styleOut) {
        inSocket = s;
        this.requestEncoding = requestEncoding;
        this.responseEncoding = responseEncoding;
        this.styleIn = styleIn;
        this.styleOut = styleOut;
        init(l);
        start();
    }

    public Connection(final Listener l, final InputStream in,
                      final JTextField requestEncoding,
                      final JTextField responseEncoding, final JComboBox styleIn,
                      final JComboBox styleOut) {
        inputStream = in;
        init(l);
        this.requestEncoding = requestEncoding;
        this.responseEncoding = responseEncoding;
        this.styleIn = styleIn;
        this.styleOut = styleOut;
        start();
    }

    @Override
    public void run() {
        // setName("Connection listener "+lo);
        inputText.setSyntaxEditingStyle(Tcpmon.syntaxisFields
                .get(styleIn.getSelectedItem()));
        outputText.setSyntaxEditingStyle(Tcpmon.syntaxisFields
                .get(styleOut.getSelectedItem()));
        try {
            active = true;

            HTTPProxyHost = System.getProperty("http.proxyHost");
            if (HTTPProxyHost != null && HTTPProxyHost.equals("")) {
                HTTPProxyHost = null;
            }

            if (HTTPProxyHost != null) {
                String tmp = System.getProperty("http.proxyPort");

                if (tmp != null && tmp.equals("")) {
                    tmp = null;
                }
                if (tmp == null) {
                    HTTPProxyPort = 80;
                } else {
                    HTTPProxyPort = Integer.parseInt(tmp);
                }
            }

            if (inSocket != null) {
                fromHost = (inSocket.getInetAddress()).getHostName();
            } else {
                fromHost = "resend";
            }

            final String dateformat = Tcpmon.getMessage("dateformat00",
                    "yyyy-MM-dd HH:mm:ss");
            final DateFormat df = new SimpleDateFormat(dateformat);

            time = df.format(new Date());

            final int count = listener.connections.size();

            listener.tableModel.insertRow(count + 1, new Object[] {
                    Tcpmon.getMessage("active00", "Active"), time, fromHost,
                    listener.hostField.getText(), "" });
            listener.connections.add(this);
            inputScroll = MyTextArea.buildRTextScrollPane(inputText);
            // Below fix bug for Windows L&F
            // inputScroll.getTextArea().addKeyListener(new KeyAdapter() {
            // @Override
            // public void keyReleased(KeyEvent e) {
            // if(e.getKeyChar()=='b') {
            // dd
            // }
            // }
            // });
            outputScroll = MyTextArea.buildRTextScrollPane(outputText);
            final ListSelectionModel lsm = listener.connectionTable
                    .getSelectionModel();

            if (count == 0 || lsm.getLeadSelectionIndex() == 0) {
                // listener.outPane.setVisible(false);
                // final int divLoc = listener.outPane.getDividerLocation();

                listener.setLeft(inputScroll);
                listener.setRight(outputScroll);

                listener.removeButton.setEnabled(false);
                listener.removeAllButton.setEnabled(true);
                listener.saveButton.setEnabled(true);
                listener.resendButton.setEnabled(true);
                // listener.outPane.setDividerLocation(divLoc);
                // listener.outPane.setVisible(true);
            }

            String targetHost = listener.hostField.getText();
            log.info("host "+targetHost);
            int targetPort = Integer
                    .parseInt(listener.tPortField.getText());

            InputStream tmpIn1 = inputStream;
            OutputStream tmpOut1 = null;

            if (tmpIn1 == null) {
                tmpIn1 = inSocket.getInputStream();
            }

            if (inSocket != null) {
                tmpOut1 = inSocket.getOutputStream();
            }

            String bufferedData = null;
            StringBuffer buf = null;

            final int index = listener.connections.indexOf(this);

            if (listener.isProxyBox.isSelected() || HTTPProxyHost != null) {
                // Check if we're a proxy
                final byte[] b = new byte[1];

                buf = new StringBuffer();
                String s;

                for (;;) {
                    int len;
                    if (tmpIn1.available() == 0) {
                        log.info("avalibale is null");
                        break;
                    }
                    len = tmpIn1.read(b, 0, 1);
                    if (len == -1) {
                        break;
                    }
                    final String reqEnc = requestEncoding.getText();
                    s = new String(b, reqEnc);
                    buf.append(s);
                    if (b[0] != '\n') {
                        // continue;
                    } else {
                        break;
                    }
                }

                bufferedData = buf.toString();
                inputText.appendInSwingThread(bufferedData);

                if (bufferedData.startsWith("GET ")
                        || bufferedData.startsWith("POST ")) {
                    int start, end;
                    URL url;

                    start = bufferedData.indexOf(' ') + 1;
                    while (bufferedData.charAt(start) == ' ') {
                        start++;
                    }
                    end = bufferedData.indexOf(' ', start);
                    String urlString = bufferedData.substring(start, end);

                    if (urlString.charAt(0) == '/') {
                        urlString = urlString.substring(1);
                    }
                    if (listener.isProxyBox.isSelected()) {
                        url = new URL(urlString);
                        targetHost = url.getHost();
                        log.info("url host "+targetHost);
                        targetPort = url.getPort();
                        if (targetPort == -1) {
                            targetPort = 80;
                        }

                        listener.tableModel.setValueAt(targetHost,
                                index + 1, Tcpmon.OUTHOST_COLUMN);
                        bufferedData = bufferedData.substring(0, start)
                                + url.getFile()
                                + bufferedData.substring(end);
                    } else {
                        url = new URL("http://" + targetHost + ":"
                                + targetPort + "/" + urlString);

                        listener.tableModel.setValueAt(targetHost,
                                index + 1, Tcpmon.OUTHOST_COLUMN);
                        bufferedData = bufferedData.substring(0, start)
                                + url.toExternalForm()
                                + bufferedData.substring(end);
//							targetHost = HTTPProxyHost;
//							targetPort = HTTPProxyPort;
//							log.info("new proxy host "+targetHost);
                    }

                }
            } else {
                //
                // Change Host: header to point to correct host
                //
                final byte[] b1 = new byte[1];
                buf = new StringBuffer();
                String lastLine = null;
                if (listener.changeHostcheckBox.isSelected()) {
                    for (;;) {
                        final int len = tmpIn1.read(b1, 0, 1);
                        if (len == -1) {
                            break;
                        }
                        final String s1 = new String(b1,
                                requestEncoding.getText());
                        buf.append(s1);
                        if (b1[0] != '\n') {
                            continue;
                        }
                        // we have a complete line
                        final String line = buf.toString();

                        buf.setLength(0);
                        // check to see if we have found Host: header
                        if (line.startsWith("Host: ")) {
                            // we need to update the hostname to target host
                            final String newHost = "Host: " + targetHost
                                    + ":" + targetPort + "\r\n";

                            bufferedData = bufferedData.concat(newHost);
                            break;
                        }
                        // add it to our headers so far
                        if (bufferedData == null) {
                            bufferedData = line;
                        } else {
                            bufferedData = bufferedData.concat(line);
                        }

                        // failsafe
                        if (line.equals("\r\n")) {
                            break;
                        }
                        if ("\n".equals(lastLine) && line.equals("\n")) {
                            break;
                        }
                        lastLine = line;
                    }
                }
                if (bufferedData != null) {

                    inputText.appendInSwingThread(bufferedData);
                    final int idx = bufferedData.length() < 50 ? bufferedData
                            .length() : 50;
                    String s1 = bufferedData.substring(0, idx);
                    final int i = s1.indexOf('\n');

                    if (i > 0) {
                        s1 = s1.substring(0, i - 1);
                    }
                    s1 = s1 + "                           "
                            + "                       ";
                    s1 = s1.substring(0, 51);
                    listener.tableModel.setValueAt(s1, index + 1,
                            Tcpmon.REQ_COLUMN);
                }
            }

            if (targetPort == -1) {
                targetPort = 80;
            }
            log.info(targetHost);
            outSocket = new Socket(targetHost, targetPort);
            InputStream tmpIn2 = null;
            OutputStream tmpOut2 = null;
            // String respEnc = responseEncoding.getText();
            tmpIn2 = outSocket.getInputStream();
            tmpOut2 = outSocket.getOutputStream();

            if (bufferedData != null) {
                final byte[] b = bufferedData.getBytes();
                tmpOut2.write(b);
                slowLink.pump(b.length);
            }

            // final boolean format = listener.xmlFormatBox.isSelected();

            // this is the channel to the endpoint
            rr1 = new SocketRR(this, inSocket, tmpIn1, outSocket, tmpOut2,
                    inputText, listener.tableModel, index + 1, "request:",
                    slowLink, requestEncoding,
                    listener.changeHostcheckBox.isSelected() ? targetHost
                            + ":" + targetPort : null, listener.showMsgData);
            rr1.setName("Tcpmon forward to: " + targetHost);
            // create the response slow link from the inbound slow link
            final SlowLinkSimulator responseLink = new SlowLinkSimulator(
                    slowLink);
            // this is the channel from the endpoint
            rr2 = new SocketRR(this, outSocket, tmpIn2, inSocket, tmpOut1,
                    outputText, null, 0, "response:", responseLink,
                    requestEncoding, null, listener.showMsgData);
            rr2.setName("Tcpmon forward from: " + targetHost);
            while (rr1 != null || rr2 != null) {
                // Only loop as long as the connection to the target
                // machine is available - once that's gone we can stop.
                // The old way, loop until both are closed, left us
                // looping forever since no one closed the 1st one.
                // while( !rr2.isDone() )
                if (null != rr1 && rr1.isDone()) {
                    if (index >= 0 && rr2 != null) {
                        listener.tableModel.setValueAt(
                                Tcpmon.getMessage("resp00", "Resp"), 1 + index,
                                Tcpmon.STATE_COLUMN);
                    }
                    rr1 = null;
                }
                if (null != rr2 && rr2.isDone()) {
                    if (index >= 0 && rr1 != null) {
                        listener.tableModel.setValueAt(
                                Tcpmon.getMessage("req00", "Req"), 1 + index,
                                Tcpmon.STATE_COLUMN);
                    }
                    rr2 = null;
                }

                // Thread.sleep( 10 );
                synchronized (this) {
                    this.wait(1000); // Safety just incase we're not told
                    // to wake up.
                }
            }

            // System.out.println("Done ");
            // rr1.halt();
            // rr2.halt();

            active = false;

            /*
             * if ( inSocket != null ) { inSocket.close(); inSocket = null ;
             * } outSocket.close(); outSocket = null ;
             */

            if (index >= 0) {
                listener.tableModel.setValueAt(
                        Tcpmon.getMessage("done00", "Done"), 1 + index,
                        Tcpmon.STATE_COLUMN);

            }
        } catch (final Exception e) {
            final StringWriter st = new StringWriter();
            final PrintWriter wr = new PrintWriter(st);
            final int index = listener.connections.indexOf(this);

            if (index >= 0) {
                listener.tableModel.setValueAt(
                        Tcpmon.getMessage("error00", "Error"), 1 + index,
                        Tcpmon.STATE_COLUMN);
            }
            e.printStackTrace(wr);
            wr.close();
            if (outputText != null) {
                outputText.appendInSwingThread(st.toString());
            } else {
                // something went wrong before we had the output area
                System.out.println(st.toString());
            }
            halt();
        }
    }

    synchronized void wakeUp() {
        this.notifyAll();
    }

    public void halt() {
        try {
            if (rr1 != null) {
                rr1.halt();
            }
            if (rr2 != null) {
                rr2.halt();
            }
            if (inSocket != null) {
                inSocket.close();
            }
            inSocket = null;
            if (outSocket != null) {
                outSocket.close();
            }
            outSocket = null;
        } catch (final Exception e) {
            log.info("", e);
        }
    }

    public void remove() {
        int index = -1;

        try {
            halt();
            index = listener.connections.indexOf(this);
            listener.tableModel.removeRow(index + 1);
            listener.connections.remove(index);
        } catch (final Exception e) {
            log.info("index:=" + index + this, e);
        }
    }
}
