package net.sf.jremoterun.utilities.nonjdk.tcpmon;

import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrUtilities;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * this is the admin page
 */
@CompileStatic
public class AdminPage extends JPanel {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static final String DEFAULT_HOST = "127.0.0.1";

    public static final int DEFAULT_PORT = 8080;


    public JRadioButton listenerButton, proxyButton;

    public JLabel hostLabel, tportLabel;

    public NumberField port;

    public HostnameField host;

    public NumberField tport;

    // public TabWindow noteb;

    public JCheckBox HTTPProxyBox;

    public HostnameField HTTPProxyHost;

    public NumberField HTTPProxyPort;

    public JLabel HTTPProxyHostLabel, HTTPProxyPortLabel;

    public JLabel delayTimeLabel, delayBytesLabel;

    public NumberField delayTime, delayBytes;

    public JCheckBox delayBox;

    public View defaultView;

    public AdminPage(final TabWindow notebook, final String name) {
        this.defaultView = defaultView;
        JPanel mainPane = null;
        JButton addButton = null;

        this.setLayout(new BorderLayout());
        // noteb = notebook;

        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();

        mainPane = new JPanel(layout);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        mainPane.add(
                new JLabel(Tcpmon.getMessage("newTCP00",
                        "Create a new TCP/IP Monitor...") + " "), c);

        // Add some blank space
        mainPane.add(Box.createRigidArea(new Dimension(1, 5)), c);

        // The listener info
        // /////////////////////////////////////////////////////////////////
        final JPanel tmpPanel = new JPanel(new GridBagLayout());

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        tmpPanel.add(new JLabel(Tcpmon.getMessage("listenPort00", "Listen Port #")
                + " "), c);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        tmpPanel.add(port = new NumberField(4), c);

        mainPane.add(tmpPanel, c);

        mainPane.add(Box.createRigidArea(new Dimension(1, 5)), c);

        // Group for the radio buttons
        final ButtonGroup btns = new ButtonGroup();

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        mainPane.add(new JLabel(Tcpmon.getMessage("actAs00", "Act as a...")), c);

        // Target Host/Port section
        // /////////////////////////////////////////////////////////////////
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;

        final String listener = Tcpmon.getMessage("listener00", "Listener");

        mainPane.add(listenerButton = new JRadioButton(listener), c);
        btns.add(listenerButton);
        listenerButton.setSelected(true);

        listenerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                if (listener.equals(event.getActionCommand())) {
                    final boolean state = listenerButton.isSelected();

                    tport.setEnabled(state);
                    host.setEnabled(state);
                    hostLabel.setForeground(state ? Color.black
                            : Color.gray);
                    tportLabel.setForeground(state ? Color.black
                            : Color.gray);
                }
            }
        });

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        mainPane.add(Box.createRigidArea(new Dimension(25, 0)));
        mainPane.add(
                hostLabel = new JLabel(Tcpmon.getMessage("targetHostname00",
                        "Target Hostname") + " "), c);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        host = new HostnameField(30);
        mainPane.add(host, c);
        host.setText(DEFAULT_HOST);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        mainPane.add(Box.createRigidArea(new Dimension(25, 0)));
        mainPane.add(
                tportLabel = new JLabel(Tcpmon.getMessage("targetPort00",
                        "Target Port #") + " "), c);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        tport = new NumberField(4);
        mainPane.add(tport, c);
        tport.setValue(DEFAULT_PORT);

        // Act as proxy section
        // /////////////////////////////////////////////////////////////////
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        final String proxy = Tcpmon.getMessage("proxy00", "Proxy");

        mainPane.add(proxyButton = new JRadioButton(proxy), c);
        btns.add(proxyButton);

        proxyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                if (proxy.equals(event.getActionCommand())) {
                    final boolean state = proxyButton.isSelected();

                    tport.setEnabled(!state);
                    host.setEnabled(!state);
                    hostLabel.setForeground(state ? Color.gray
                            : Color.black);
                    tportLabel.setForeground(state ? Color.gray
                            : Color.black);
                }
            }
        });

        // Spacer
        // ///////////////////////////////////////////////////////////////
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        mainPane.add(Box.createRigidArea(new Dimension(1, 10)), c);

        // Options section
        // /////////////////////////////////////////////////////////////////
        final JPanel opts = new JPanel(new GridBagLayout());

        opts.setBorder(new TitledBorder(Tcpmon.getMessage("options00", "Options")));
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        mainPane.add(opts, c);

        // HTTP Proxy Support section
        // /////////////////////////////////////////////////////////////////
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        final String proxySupport = Tcpmon.getMessage("proxySupport00",
                "HTTP Proxy Support");

        opts.add(HTTPProxyBox = new JCheckBox(proxySupport), c);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        opts.add(
                HTTPProxyHostLabel = new JLabel(Tcpmon.getMessage("hostname00",
                        "Hostname") + " "), c);
        HTTPProxyHostLabel.setForeground(Color.gray);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        opts.add(HTTPProxyHost = new HostnameField(30), c);
        HTTPProxyHost.setEnabled(false);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        opts.add(
                HTTPProxyPortLabel = new JLabel(Tcpmon.getMessage("port00",
                        "Port #") + " "), c);
        HTTPProxyPortLabel.setForeground(Color.gray);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        opts.add(HTTPProxyPort = new NumberField(4), c);
        HTTPProxyPort.setEnabled(false);

        HTTPProxyBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                if (proxySupport.equals(event.getActionCommand())) {
                    final boolean b = HTTPProxyBox.isSelected();
                    final Color color = b ? Color.black : Color.gray;

                    HTTPProxyHost.setEnabled(b);
                    HTTPProxyPort.setEnabled(b);
                    HTTPProxyHostLabel.setForeground(color);
                    HTTPProxyPortLabel.setForeground(color);
                }
            }
        });

        // Set default proxy values...
        String tmp = null;//;System.getProperty("http.proxyHost");

        if (tmp != null && tmp.equals("")) {
            tmp = null;
        }
        HTTPProxyBox.setSelected(tmp != null);
        HTTPProxyHost.setEnabled(tmp != null);
        HTTPProxyPort.setEnabled(tmp != null);
        HTTPProxyHostLabel.setForeground(tmp != null ? Color.black
                : Color.gray);
        HTTPProxyPortLabel.setForeground(tmp != null ? Color.black
                : Color.gray);

        if (tmp != null) {
            HTTPProxyBox.setSelected(true);
            HTTPProxyHost.setText(tmp);
            tmp = System.getProperty("http.proxyPort");
            if (tmp != null && tmp.equals("")) {
                tmp = null;
            }
            if (tmp == null) {
                tmp = "80";
            }
            HTTPProxyPort.setText(tmp);
        }

        // add byte delay fields
        opts.add(Box.createRigidArea(new Dimension(1, 10)), c);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        final String delaySupport = Tcpmon.getMessage("delay00",
                "Simulate Slow Connection");
        opts.add(delayBox = new JCheckBox(delaySupport), c);

        // bytes per pause
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        delayBytesLabel = new JLabel(Tcpmon.getMessage("delay01",
                "Bytes per Pause"));
        opts.add(delayBytesLabel, c);
        delayBytesLabel.setForeground(Color.gray);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        opts.add(delayBytes = new NumberField(6), c);
        delayBytes.setEnabled(false);

        // delay interval
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        delayTimeLabel = new JLabel(Tcpmon.getMessage("delay02",
                "Delay in Milliseconds"));
        opts.add(delayTimeLabel, c);
        delayTimeLabel.setForeground(Color.gray);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        opts.add(delayTime = new NumberField(6), c);
        delayTime.setEnabled(false);

        // enabler callback
        delayBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                if (delaySupport.equals(event.getActionCommand())) {
                    final boolean b = delayBox.isSelected();
                    final Color color = b ? Color.black : Color.gray;

                    delayBytes.setEnabled(b);
                    delayTime.setEnabled(b);
                    delayBytesLabel.setForeground(color);
                    delayTimeLabel.setForeground(color);
                }
            }
        });

        // Spacer
        // ////////////////////////////////////////////////////////////////
        mainPane.add(Box.createRigidArea(new Dimension(1, 10)), c);

        // ADD Button
        // /////////////////////////////////////////////////////////////////
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        final String add = Tcpmon.getMessage("add00", "Add");

        mainPane.add(addButton = new JButton(add), c);

        this.add(new JScrollPane(mainPane), BorderLayout.CENTER);

        // addButton.setEnabled( false );
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                if (add.equals(event.getActionCommand())) {
                    // BEGIN BORLAND JBUILDER PATCH
                    // Just added a dummy try catch block to avoid number
                    // format exception, etc
                    try {
                        String text;
                        Listener l = null;
                        int lPort;
                        lPort = port.getValue(0);
                        if (lPort == 0) {
                            // no port, button does nothing
                            return;
                        }
                        final String tHost = host.getText();
                        int tPort = 0;
                        tPort = tport.getValue(0);
                        SlowLinkSimulator slowLink = null;
                        if (delayBox.isSelected()) {
                            final int bytes = delayBytes.getValue(0);
                            final int time = delayTime.getValue(0);
                            slowLink = new SlowLinkSimulator(bytes, time);
                        }
                        final TabWindow tabWindow = (TabWindow) defaultView
                                .getWindowParent();
                        l = new Listener(tabWindow, null, lPort, tHost,
                                tPort, proxyButton.isSelected(), slowLink,
                                false, null,
                                SyntaxConstants.SYNTAX_STYLE_NONE,
                                SyntaxConstants.SYNTAX_STYLE_NONE, true);

                        // Pick-up the HTTP Proxy settings
                        // /////////////////////////////////////////////////
                        if (HTTPProxyBox.isSelected()) {
                            text = HTTPProxyHost.getText();
                            if ("".equals(text)) {
                                text = null;
                            }
                            l.HTTPProxyHost = text;
                            text = HTTPProxyPort.getText();
                            final int proxyPort = HTTPProxyPort
                                    .getValue(-1);
                            if (proxyPort != -1) {
                                l.HTTPProxyPort = Integer.parseInt(text);
                            }
                        }
                        // reset the port
                        port.setText(null);

                        /*
                         * but not, any more, the target port and host
                         * values host.setText(null); tport.setText(null);
                         */
                    } catch (final Exception ex) {
                        log.log(Level.INFO,"", ex);
                        JrrUtilities.showException("",ex);
                    }
                    // END BORLAND JBUILDER PATCH
                }
            }
        });

    }

}
