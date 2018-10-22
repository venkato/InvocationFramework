package net.sf.jremoterun.utilities.nonjdk.tcpmon;

import groovy.transform.CompileStatic;
import net.infonode.docking.*;
import net.sf.jremoterun.utilities.FileOutputStream2;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtils;
import net.sf.jremoterun.utilities.nonjdk.idwutils.MyDockingWindowTitleProvider;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this is one of the tabbed panels that acts as the actual proxy
 */
@CompileStatic
public class Listener {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public final JComboBox reqStyle = new JComboBox(new Vector(
            Tcpmon.syntaxisFields.keySet()));

    public final JComboBox respStyle = new JComboBox(new Vector(
            Tcpmon.syntaxisFields.keySet()));

    public Socket inputSocket = null;

    public Socket outputSocket = null;

    public JTextField portField = new JTextField("", 4);

    public JTextField hostField = null;

    public JTextField tPortField = null;

    public JCheckBox isProxyBox = null;

    public JCheckBox filterCheckBox = null;

    public JButton stopButton = null;

    public JButton removeButton = null;

    public JButton removeAllButton = null;

    // public JCheckBox xmlFormatBox = null;

    public JButton saveButton = null;

    public final String nameDescription;
    public ShowMsgData showMsgData;

    public JButton resendButton = new JButton(Tcpmon.resend);

    // private final View view = new View(getMessage("port01", "Port") + " "
    // + portField.getText(), null, this);

    // public JButton switchButton = null;

    // public JButton closeButton = null;

    public JTable connectionTable = null;

    public DefaultTableModel tableModel = null;

    public final JTextField requestEncoding = new JTextField(Charset
            .defaultCharset().name());

    public final JTextField responseEncoding = new JTextField(Charset
            .defaultCharset().name());

    // public JSplitPane outPane = null;

    public JPanel headerPanel = new JPanel(new BorderLayout());
    public JPanel requestPanel = new JPanel(new BorderLayout());
    public JPanel responsePanel = new JPanel(new BorderLayout());

    public View headerView = new View("123", null, headerPanel);
    public View requestView = new View("Request", null, requestPanel);
    public View responseView = new View("Response", null, responsePanel);

    public TabWindow headerTabWindow = new TabWindow(headerView);
    public TabWindow requestTabWindow = new TabWindow(requestView);
    public TabWindow responseTabWindow = new TabWindow(responseView);

    public ServerSocket sSocket = null;

    public SocketWaiter sw = null;

    // public JPanel leftPanel = null;

    // public JPanel rightPanel = null;

    public TabWindow notebook2 = null;

    public String HTTPProxyHost = null;

    public int HTTPProxyPort = 80;

    public int delayBytes = 0;

    public int delayTime = 0;

    public SlowLinkSimulator slowLink;

    public JCheckBox changeHostcheckBox = new JCheckBox("change", false);
    public final SplitWindow portSplitWindow;
    public final Vector connections = new Vector();

    public String inputDefaultStyle;
    public String outputDefaultStyle;

    public final JPanel buttonsPanel = new JPanel();

    /**
     * create a listener
     *
     * @param _notebook
     * @param name
     * @param listenPort
     * @param host
     * @param targetPort
     * @param isProxy
     * @param slowLink
     *            optional reference to a slow connection
     * @param doStart
     */
    public Listener(final TabWindow _notebook, final String name,
                    final int listenPort, final String host, final int targetPort,
                    final boolean isProxy, final SlowLinkSimulator slowLink,
                    final boolean changeHost, final ShowMsgData showMsgData,
                    final String inputStyle, final String outputStyle,
                    boolean doStart) {
        this.showMsgData = showMsgData;
        notebook2 = _notebook;
        // log.info(inputStyle);
        nameDescription = name;
        // log.info(Tcpmon.syntaxisFieldsInverse.get( inputStyle));
        reqStyle.setSelectedItem(Tcpmon.syntaxisFieldsInverse
                .get(inputStyle));
        respStyle.setSelectedItem(Tcpmon.syntaxisFieldsInverse
                .get(outputStyle));
        changeHostcheckBox.setSelected(changeHost);
        // set the slow link to the passed down link
        if (slowLink != null) {
            this.slowLink = slowLink;
        } else {
            // or make up a no-op one.
            this.slowLink = new SlowLinkSimulator(0, 0);
        }
        // this.setLayout(new BorderLayout());

        // 1st component is just a row of labels and 1-line entry fields
        // //////////////////////////////////////////////////////////////////
        // /


        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        final String start = Tcpmon.getMessage("start00", "Start");

        buttonsPanel.add(stopButton = new JButton(start));
        buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsPanel.add(new JLabel("  "
                + Tcpmon.getMessage("listenPort01", "Listen Port:") + " ",
                SwingConstants.RIGHT));
        portField.setText("" + listenPort);
        buttonsPanel.add(portField);
        buttonsPanel.add(new JLabel("  " + Tcpmon.getMessage("host00", "Host:"),
                SwingConstants.RIGHT));
        buttonsPanel.add(hostField = new JTextField(host, 40));
        buttonsPanel.add(new JLabel("  " + Tcpmon.getMessage("port02", "Port:") + " ",
                SwingConstants.RIGHT));
        buttonsPanel.add(tPortField = new JTextField("" + targetPort, 4));
        buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsPanel.add(isProxyBox = new JCheckBox(Tcpmon.getMessage("proxy00", "Proxy")));
        // if (showMsgData != null) {
        buttonsPanel.add(filterCheckBox = new JCheckBox("Filter",
                showMsgData != null));
        // }
        buttonsPanel.add(changeHostcheckBox);
        buttonsPanel.add(saveButton = new JButton(Tcpmon.save));
        buttonsPanel.add(resendButton);
        isProxyBox.addChangeListener(new BasicButtonListener(isProxyBox) {

            @Override
            public void stateChanged(final ChangeEvent event) {
                final JCheckBox box = (JCheckBox) event.getSource();
                final boolean state = box.isSelected();

                tPortField.setEnabled(!state);
                hostField.setEnabled(!state);
            }
        });

        isProxyBox.setSelected(isProxy);
        portField.setEditable(false);
        portField.setMaximumSize(new Dimension(50, Short.MAX_VALUE));
        hostField.setEditable(false);
        hostField.setMaximumSize(new Dimension(85, Short.MAX_VALUE));
        tPortField.setEditable(false);
        tPortField.setMaximumSize(new Dimension(50, Short.MAX_VALUE));

        stopButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                if (Tcpmon.getMessage("stop00", "Stop").equals(
                        event.getActionCommand())) {
                    stop();
                }
                if (start.equals(event.getActionCommand())) {
                    start();
                }
            }
        });

        headerPanel.add(buttonsPanel, BorderLayout.NORTH);

        // 2nd component is a split pane with a table on the top
        // and the request/response text areas on the bottom
        // //////////////////////////////////////////////////////////////////
        // /

        tableModel = new DefaultTableModel(new String[] {
                Tcpmon.getMessage("state00", "State"),
                Tcpmon.getMessage("time00", "Time"),
                Tcpmon.getMessage("requestHost00", "Request Host"),
                Tcpmon.getMessage("targetHost", "Target Host"),
                Tcpmon.getMessage("request00", "Request...") }, 0);

        connectionTable = new JTable(1, 2);
        connectionTable.setModel(tableModel);
        connectionTable
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // Reduce the STATE column and increase the REQ column
        TableColumn col;

        col = connectionTable.getColumnModel().getColumn(Tcpmon.STATE_COLUMN);
        col.setMaxWidth(col.getPreferredWidth() / 2);
        col = connectionTable.getColumnModel().getColumn(Tcpmon.REQ_COLUMN);
        col.setPreferredWidth(col.getPreferredWidth() * 2);

        final ListSelectionModel sel = connectionTable.getSelectionModel();

        sel.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    return;
                }
                final ListSelectionModel m = (ListSelectionModel) event
                        .getSource();
                // final int divLoc = outPane.getDividerLocation();

                if (m.isSelectionEmpty()) {
                    setLeft(new JLabel(" "
                            + Tcpmon.getMessage("wait00",
                                    "Waiting for Connection...")));
                    setRight(new JLabel(""));
                    removeButton.setEnabled(false);
                    removeAllButton.setEnabled(false);
                    saveButton.setEnabled(false);
                    resendButton.setEnabled(false);
                } else {
                    final int row = m.getLeadSelectionIndex();

                    if (row == 0) {
                        if (connections.size() == 0) {
                            setLeft(new JLabel(" "
                                    + Tcpmon.getMessage("wait00",
                                            "Waiting for connection...")));
                            setRight(new JLabel(""));
                            removeButton.setEnabled(false);
                            removeAllButton.setEnabled(false);
                            saveButton.setEnabled(false);
                            resendButton.setEnabled(false);
                        } else {
                            final Connection conn = (Connection) connections
                                    .lastElement();

                            setLeft(conn.inputScroll);
                            setRight(conn.outputScroll);
                            removeButton.setEnabled(false);
                            removeAllButton.setEnabled(true);
                            saveButton.setEnabled(true);
                            resendButton.setEnabled(true);
                        }
                    } else {
                        final Connection conn = (Connection) connections
                                .get(row - 1);

                        setLeft(conn.inputScroll);
                        setRight(conn.outputScroll);
                        removeButton.setEnabled(true);
                        removeAllButton.setEnabled(true);
                        saveButton.setEnabled(true);
                        resendButton.setEnabled(true);
                    }
                }
                // outPane.setDividerLocation(divLoc);
            }
        });
        tableModel.addRow(new Object[] { "---",
                Tcpmon.getMessage("mostRecent00", "Most Recent"), "---", "---",
                "---" });

        final JPanel tablePane = new JPanel();

        tablePane.setLayout(new BorderLayout());

        final JScrollPane tableScrollPane = new JScrollPane(connectionTable);

        tablePane.add(tableScrollPane, BorderLayout.CENTER);
        final JPanel buttons = new JPanel();

        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        final String removeSelected = Tcpmon.getMessage("removeSelected00",
                "Remove Selected");

        buttons.add(removeButton = new JButton(removeSelected));
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(requestEncoding);
        buttons.add(responseEncoding);
        final String removeAll = Tcpmon.getMessage("removeAll00", "Remove All");

        buttons.add(removeAllButton = new JButton(removeAll));
        tablePane.add(buttons, BorderLayout.SOUTH);

        // reqStyle.setSelectedItem("none");
        // respStyle.setSelectedItem("none");
        buttons.add(reqStyle);
        buttons.add(respStyle);
        removeButton.setEnabled(false);
        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                if (removeSelected.equals(event.getActionCommand())) {
                    remove();
                }
            }
        });

        removeAllButton.setEnabled(false);
        removeAllButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                if (removeAll.equals(event.getActionCommand())) {
                    removeAll();
                }
            }
        });
        final SplitWindow splitWindowRequestResp = new SplitWindow(false,
                requestTabWindow, responseTabWindow);
        portSplitWindow = new SplitWindow(false, 0.3f, headerTabWindow,
                splitWindowRequestResp);
        saveButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                if (Tcpmon.save.equals(event.getActionCommand())) {
                    save();
                }
            }
        });

        resendButton.setEnabled(false);
        resendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                if (Tcpmon.resend.equals(event.getActionCommand())) {
                    resend();
                }
            }
        });
        headerPanel.add(tablePane, BorderLayout.CENTER);

        //
        // //////////////////////////////////////////////////////////////////
        sel.setSelectionInterval(0, 0);
        headerView.getViewProperties().setTitle(
                Tcpmon.getMessage("port01", "Port") + " " + portField.getText());
        String title;
        if (name == null) {
            title=portField.getText() + "   ";
        } else {
            title=name + "  " + portField.getText();
        }

        portSplitWindow.getWindowProperties().setTitleProvider(
                new MyDockingWindowTitleProvider(title));
        notebook2.addTab(portSplitWindow);
        headerView.addListener(new DockingWindowAdapter() {

            @Override
            public void windowClosed(final DockingWindow window) {
                close();
                log.info("closed for " + portField.getText());
            }
        });
        requestView.getWindowProperties().setCloseEnabled(false);
        responseView.getWindowProperties().setCloseEnabled(false);
        IdwUtils.setLeftBar(headerTabWindow);
        IdwUtils.setLeftBar(requestTabWindow);
        IdwUtils.setLeftBar(responseTabWindow);
        // log.info(view);
        if (doStart) {
            start();
        }
    }

    public void setLeft(final Component left) {
        requestPanel.removeAll();
        requestPanel.add(left);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final RootWindow rootWindow = requestView.getRootWindow();
                if (rootWindow != null) {
                    requestView.validate();
                    requestView.repaint();
                }

            }

        });
    }

    public void setRight(final Component right) {
        responsePanel.removeAll();
        responsePanel.add(right);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final RootWindow rootWindow = responseView.getRootWindow();
                if (rootWindow != null) {
                    responseView.validate();
                    responseView.repaint();
                }
            }

        });

    }

    public void start() {
        final int port = Integer.parseInt(portField.getText());

        portField.setText("" + port);
        // final int i = notebook2.indexOfComponent(this);
        String title;
        if (nameDescription == null) {
            title= portField.getText() + "   ";
        } else {
            title= nameDescription + "  "
                    + portField.getText();
        }
        portSplitWindow.getWindowProperties().setTitleProvider(
                new MyDockingWindowTitleProvider(title));
        headerView.getViewProperties().setTitle(
                Tcpmon.getMessage("port01", "Port") + " " + port);

        final int tmp = Integer.parseInt(tPortField.getText());

        tPortField.setText("" + tmp);

        sw = new SocketWaiter(this, port, requestEncoding,
                responseEncoding, reqStyle, respStyle);
        stopButton.setText(Tcpmon.getMessage("stop00", "Stop"));

        portField.setEditable(false);
        hostField.setEditable(false);
        tPortField.setEditable(false);
        isProxyBox.setEnabled(false);
    }

    public void close() {
        stop();
        requestView.close();
        responseTabWindow.close();
        // notebook2.remove(this);
    }

    public void stop() {
        try {
            for (int i = 0; i < connections.size(); i++) {
                final Connection conn = (Connection) connections.get(i);

                conn.halt();
            }
            sw.halt();
            stopButton.setText(Tcpmon.getMessage("start00", "Start"));
            portField.setEditable(true);
            hostField.setEditable(true);
            tPortField.setEditable(true);
            isProxyBox.setEnabled(true);
        } catch (final Exception e) {
            log.log(Level.INFO,"", e);
        }
    }

    public void remove() {
        final ListSelectionModel lsm = connectionTable.getSelectionModel();
        int bot = lsm.getMinSelectionIndex();
        final int top = lsm.getMaxSelectionIndex();

        for (int i = top; i >= bot; i--) {
            ((Connection) connections.get(i - 1)).remove();
        }
        if (bot > connections.size()) {
            bot = connections.size();
        }
        lsm.setSelectionInterval(bot, bot);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                responseView.repaint();
                requestView.repaint();
            }

        });
    }

    public void removeAll() {
        final ListSelectionModel lsm = connectionTable.getSelectionModel();
        lsm.clearSelection();
        while (connections.size() > 0) {
            ((Connection) connections.get(0)).remove();
        }

        lsm.setSelectionInterval(0, 0);
    }

    public void save() {
        final JFileChooser dialog = new JFileChooser(".");
        int rc = dialog.showSaveDialog(headerPanel);

        if (rc == JFileChooser.APPROVE_OPTION) {
            try {
                final File file = dialog.getSelectedFile();
                final FileOutputStream2 out = new FileOutputStream2(file);

                final ListSelectionModel lsm = connectionTable
                        .getSelectionModel();

                rc = lsm.getLeadSelectionIndex();

                int n = 0;
                for (final Iterator i = connections.iterator(); i.hasNext(); n++) {
                    final Connection conn = (Connection) i.next();
                    if (lsm.isSelectedIndex(n + 1)
                            || (!(i.hasNext()) && lsm
                                    .getLeadSelectionIndex() == 0)) {
                        rc = Integer.parseInt(portField.getText());
                        out.write("\n==============\n".getBytes());
                        out.write((new String(Tcpmon.getMessage("listenPort01",
                                "Listen Port:") + " " + rc + "\n"))
                                .getBytes());
                        out.write((new String(Tcpmon.getMessage("targetHost01",
                                "Target Host:")
                                + " "
                                + hostField.getText()
                                + "\n")).getBytes());
                        rc = Integer.parseInt(tPortField.getText());
                        out.write((new String(Tcpmon.getMessage("targetPort01",
                                "Target Port:") + " " + rc + "\n"))
                                .getBytes());

                        out.write((new String("==== "
                                + Tcpmon.getMessage("request01", "Request")
                                + " ====\n")).getBytes());
                        out.write(conn.inputText.getText().getBytes());

                        out.write((new String("==== "
                                + Tcpmon.getMessage("response00", "Response")
                                + " ====\n")).getBytes());
                        out.write(conn.outputText.getText().getBytes());
                        out.write("\n==============\n".getBytes());
                    }
                }

                out.close();
            } catch (final Exception e) {
                log.log(Level.INFO,"", e);
            }
        }
    }

    public void resend() {
        int rc;

        try {
            final ListSelectionModel lsm = connectionTable
                    .getSelectionModel();

            rc = lsm.getLeadSelectionIndex();
            if (rc == 0) {
                rc = connections.size();
            }
            final Connection conn = (Connection) connections.get(rc - 1);

            if (rc > 0) {
                lsm.clearSelection();
                lsm.setSelectionInterval(0, 0);
            }

            InputStream in = null;
            String text = conn.inputText.getText();

            // Fix Content-Length HTTP headers
            if (text.startsWith("POST ") || text.startsWith("GET ")) {
                log.warning("IN CL");
                int pos1, pos2, pos3;
                final String body;
                String headers;
                final String headers1, header2;

                pos3 = text.indexOf("\n\n");
                if (pos3 == -1) {
                    pos3 = text.indexOf("\r\n\r\n");
                    if (pos3 != -1) {
                        pos3 = pos3 + 4;
                    }
                } else {
                    pos3 += 2;
                }

                headers = text.substring(0, pos3);

                pos1 = headers.indexOf("Content-Length:");
                System.err.println("pos1: " + pos1);
                System.err.println("pos3: " + pos3);
                if (pos1 != -1) {
                    final int newLen = text.length() - pos3;

                    pos2 = headers.indexOf("\n", pos1);

                    System.err.println("CL: " + newLen);
                    System.err.println("Hdrs: '" + headers + "'");
                    System.err.println("subTEXT: '"
                            + text.substring(pos3, pos3 + newLen) + "'");
                    text = headers.substring(0, pos1) + "Content-Length: "
                            + newLen + "\n" + headers.substring(pos2 + 1)
                            + text.substring(pos3);
                    System.err.println("\nTEXT: '" + text + "'");
                }
            }

            in = new ByteArrayInputStream(text.getBytes());
            final Connection connection = new Connection(this, in,
                    requestEncoding, responseEncoding, reqStyle, respStyle);
            connection.setName("Tcpmon port " + portField.getText());
            connection.setInputTextStyle(conn.inputText
                    .getSyntaxEditingStyle());
            connection.setOutputTextStyle(conn.outputText
                    .getSyntaxEditingStyle());
        } catch (final Exception e) {
            log.log(Level.INFO,"", e);
        }
    }
}
