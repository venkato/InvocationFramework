package net.sf.jremoterun.utilities.nonjdk.tcpmon;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * this class handles the pumping of data from the incoming socket to the
 * outgoing socket
 */
@CompileStatic
public class SocketRR extends Thread {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();

    public Socket inSocket = null;

    public Socket outSocket = null;

    public MyTextArea textArea;

    public InputStream in = null;

    public ShowMsgData showMsgData;

    public OutputStream out = null;

    // boolean xmlFormat;

    public volatile boolean done = false;

    public TableModel tmodel = null;

    public int tableIndex = 0;

    // String type = null;

    public JTextField encoding;

    public Connection myConnection = null;

    public SlowLinkSimulator slowLink;
    public final String changeToHost;

    public SocketRR(final Connection c, final Socket inputSocket,
                    final InputStream inputStream, final Socket outputSocket,
                    final OutputStream outputStream,
                    final MyTextArea _textArea,
                    // final boolean format,
                    final TableModel tModel, final int index, final String type,
                    final SlowLinkSimulator slowLink, final JTextField encoding,
                    final String changeToHost, final ShowMsgData showMsgData) {
        // setName("Tcp monitor forward: "+
        // ""+inputSocket.getInetAddress()+"");
        inSocket = inputSocket;
        this.showMsgData = showMsgData;
        in = inputStream;
        outSocket = outputSocket;
        out = outputStream;
        textArea = _textArea;
        // xmlFormat = format;
        tmodel = tModel;
        this.changeToHost = changeToHost;
        tableIndex = index;
        // this.type = type;
        myConnection = c;
        this.slowLink = slowLink;
        this.encoding = encoding;
        start();
    }

    public boolean isDone() {
        return (done);
    }

    @Override
    public void run() {
        try {
            final byte[] buffer = new byte[4096];
            final int saved = 0;
            int len;
            int i;
            int reqSaved = 0;
            // if ( inSocket != null ) inSocket.setSoTimeout( 10 );
            // if ( outSocket != null ) outSocket.setSoTimeout( 10 );

            if (tmodel != null) {
                final String tmpStr = (String) tmodel.getValueAt(
                        tableIndex, Tcpmon.REQ_COLUMN);

                if (!"".equals(tmpStr)) {
                    reqSaved = tmpStr.length();
                }
            }

            a: for (;;) {
                if (done) {
                    break;
                }
                // try{
                // len = in.available();
                // }catch(Exception e){len=0;}
                len = buffer.length;
                // Used to be 1, but if we block it doesn't matter
                // however 1 will break with some servers, including apache
                if (len == 0) {
                    len = buffer.length;
                }
                if (saved + len > buffer.length) {
                    len = buffer.length - saved;
                }
                int len1 = 0;

                while (len1 == 0) {
                    try {
                        len1 = in.read(buffer, saved, len);
                    } catch (final Exception ex) {
                        log.info(ex);
                        if (done && saved == 0) {
                            break a;
                        }
                        len1 = -1;
                        break;
                    }
                }
                len = len1;

                if (len == -1 && saved == 0) {
                    break;
                }
                if (len == -1) {
                    done = true;
                }

                // No matter how we may (or may not) format it, send it
                // on unformatted - we don't want to mess with how its
                // sent to the other side, just how its displayed
                String newData = new String(buffer, saved, len,
                        encoding.getText());
                boolean needRendFromString = false;
                if (changeToHost != null) {
                    final int kk = newData.indexOf("Host: ");
                    if (kk != -1) {
                        final int kk2 = newData.indexOf('\n', kk);
                        if (kk2 == -1) {
                            log.warn("stange data not host end: " + newData);
                        } else {
                            final StringBuffer newData1 = new StringBuffer();
                            // if (kk > 0) {
                            newData1.append(newData.substring(0, kk));
                            // }
                            newData1.append("Host: " + changeToHost);
                            newData1.append(newData.substring(kk2));
                            newData = newData1.toString();
                            // log.info(newData);
                            needRendFromString = true;
                        }
                    }

                }
                if (out != null && len > 0) {
                    slowLink.pump(len);
                    if (needRendFromString) {
                        out.write(newData.getBytes(encoding.getText()));
                    } else {
                        out.write(buffer, saved, len);
                    }
                }

                if (tmodel != null && reqSaved < 50) {
                    String old = (String) tmodel.getValueAt(tableIndex,
                            Tcpmon.REQ_COLUMN);

                    old = old + newData;
                    if (old.length() > 50) {
                        old = old.substring(0, 50);
                    }

                    reqSaved = old.length();

                    if ((i = old.indexOf('\n')) > 0) {
                        old = old.substring(0, i - 1);
                        reqSaved = 50;
                    }

                    tmodel.setValueAt(old, tableIndex, Tcpmon.REQ_COLUMN);
                }
                // if (showMsgData == null) {
                {
                    boolean usesss;
                    // usesss = showMsgData == null;
                    // if (!usesss) {
                    if (myConnection.listener.filterCheckBox.isSelected()) {
                        if (showMsgData == null) {
                            usesss = false;
                        } else {
                            usesss = showMsgData.isShowMsg(buffer, saved,
                                    len, newData);
                        }
                    } else {
                        usesss = true;
                    }
                    // log.info(usesss);
                    // }
                    // log.info("usees " + usesss);
                    if (usesss) {
                        if (textArea.getText().length() > Tcpmon.maxLength) {
                            final String newData2 = newData;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    log.warn("clearing text for");
                                    textArea.setText(" ... clearing text for ...\n"
                                            + newData2);
                                }
                            });
                        }
                        textArea.appendInSwingThread(newData);
                    }
                }
                // }
                // }
                // this.sleep(3); // Let other threads have a chance to run
            }
            // this.sleep(3); // Let other threads have a chance to run
            // halt();
            // Only set the 'done' flag if we were reading from a
            // Socket - if we were reading from an input stream then
            // we'll let the other side control when we're done
            // if ( inSocket != null ) done = true ;
        } catch (final Exception e) {
            log.info("", e);
        } finally {
            done = true;
            try {
                if (out != null) {
                    out.flush();
                    if (null != outSocket) {
                        outSocket.shutdownOutput();
                    } else {
                        out.close();
                    }
                    out = null;
                }
            } catch (final Exception e) {
                log.info("", e);
            }
            try {
                if (in != null) {
                    if (inSocket != null) {
                        inSocket.shutdownInput();
                    } else {
                        in.close();
                    }
                    in = null;
                }
            } catch (final Exception e) {
                log.info("", e);
                ;
            }
            myConnection.wakeUp();
        }
    }

    public void halt() {
        try {
            if (inSocket != null) {
                inSocket.close();
            }
            if (outSocket != null) {
                outSocket.close();
            }
            inSocket = null;
            outSocket = null;
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            in = null;
            out = null;
            done = true;
        } catch (final Exception e) {
            log.info("", e);
        }
    }
}
