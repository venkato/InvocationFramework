package net.sf.jremoterun.utilities.nonjdk.tcpmon;


import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrUtilities;
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea;
import net.sf.jremoterun.utilities.nonjdk.swing.NameAndTextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;

@CompileStatic
public class TcpServer extends JPanel {

private static final Logger log = LogManager.getLogger();

    private NameAndTextField portTextField = new NameAndTextField("Port", "800");


    private NameAndTextField encoding = new NameAndTextField("Encoding",
            Charset.defaultCharset().name());

    ServerSocket serverSocket;

    ArrayList<Socket> sockets=new ArrayList();

    private MyTextArea textArea = new MyTextArea();

    private JButton clearButton = new JButton("Clear");

    private JButton closeSocketButton = new JButton("Close socket");

    private JButton restartButton = new JButton("Restart");

    private RTextScrollPane scrollPane =MyTextArea.buildRTextScrollPane(textArea);

    private Object lock = new Object();

    public TcpServer(int port) throws IOException {
        super(new BorderLayout());
        serverSocket = new ServerSocket(port);
        textArea.append("Listening " + port + "\n");
        textArea.setEditable(false);
        portTextField.setText(port + "");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(portTextField);
        // portTextField.setMinimumSize(new Dimension());
        panel.add(encoding);
        panel.add(clearButton);
        panel.add(closeSocketButton);
        panel.add(restartButton);
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
//        textArea.setSyntaxScheme(SyntaxConstants.)
        clearButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                clearText();
            }
        });
        restartButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                try {
                    restart();
                } catch (Exception e1) {
                    JrrUtilities.showException(null, e1);
                    log.warn("", e1);
                }
            }

        });
        closeSocketButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {

                try {
                    closeCurrentSocket();
                } catch (IOException e1) {
                    JrrUtilities.showException(null
                            , e1);
                    log.warn(e1);
                }

            }

        });
    }

    public void clearText() {
        textArea.setText("Listening " + portTextField.getText() + "\n");
    }

    public void restart() throws IOException {
        int port2 = new Integer(portTextField.getText());
        synchronized (lock) {
            if (serverSocket.getLocalPort() == port2) {
                closeCurrentSocket();
                clearText();
            } else {
                ServerSocket serverSocket2 = new ServerSocket(port2);
                serverSocket.close();
                closeCurrentSocket();
                serverSocket = serverSocket2;
                clearText();
            }
        }
    }

    public void closeCurrentSocket() throws IOException {
    	for (Socket socket : sockets) {
    		synchronized (lock) {
    			socket.close();
    		}

		}
    }

    public void run2() {
        Thread thread = new Thread(new Runnable() {

            @Override
			public void run() {
            	Thread.currentThread().setName("Tcp server");
                try {
                    run3();
                } catch (Exception e) {
                	JrrUtilities.showException(null
                            , e);
                    log.warn("", e);
                }
            }

        });
        thread.start();
    }

    public void run4(Socket socket2) throws Exception {
    	textArea.appendInSwingThread("new client from "+socket2.getInetAddress()+"\n");
        // log.info(new Date());
        final InputStream in = socket2.getInputStream();
        final byte[] bs = new byte[8000];
        try {
            while (true) {
                final int readed = in.read(bs);
                if (readed == -1) {
                    // log.info("a");
                    break;
                }
                textArea.appendInSwingThread(new String(bs, 0, readed,
                        encoding.getText()));
            }
        } catch (final java.net.SocketException e) {
            System.out.println();
            textArea.appendInSwingThread(e + "\n");
        } finally {
            try {
                in.close();
                socket2.close();
            } catch (Exception e) {
                log.info("", e);
            }
            textArea.appendInSwingThread("\nConnection closed\n");
        }
    }
    public void run3() throws Exception {
        // log.info(new Date());
        while (true) {
            synchronized (lock) {
                textArea.appendInSwingThread("Waiting next Request\n");
            }
            try {
            	final Socket socket2 = serverSocket.accept();
                Thread thread = new Thread(new Runnable() {

                    @Override
					public void run() {
                    	Thread.currentThread().setName("Tcp server");
                        try {
                            run4(socket2);
                        } catch (Exception e) {
                            JrrUtilities.showException(null
                                    , e);
                            log.warn("", e);
                        }
                    }

                },socket2.getInetAddress()+"");
                thread.start();
            } catch (SocketException e) {
                log.info(e);
                continue;
            }
                    }
    }
}
