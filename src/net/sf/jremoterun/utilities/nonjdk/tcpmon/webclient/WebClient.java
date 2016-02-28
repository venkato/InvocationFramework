package net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient;

import groovy.transform.CompileStatic;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtils;
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;


/**
 * A graphical client that lets you interactively connect to Web servers and
 * send custom request lines and request headers.
 * <P>
 * Taken from Core Servlets and JavaServer Pages from Prentice Hall and Sun
 * Microsystems Press, http://www.coreservlets.com/. &copy; 2000 Marty Hall; may
 * be freely used or adapted.
 */

@CompileStatic
public class WebClient extends JPanel implements Runnable, Interruptible,
		ActionListener {

private static final Logger log = LogManager.getLogger();

	private HttpClient httpClient;

	// public static void main(final String[] args) {
	// new WebClient("Web Client");
	// }

	private final LabeledTextField hostField, portField;

	// requestLineField;
	private final MyTextArea requestHeadersArea, resultArea;

	// private String host; // requestLine;

	private int port;

	private final String[] requestHeaders = new String[30];

	private final Button cloneButton = new Button("Clone");
	private final Button submitButton = new Button("Submit");

	private final Button interruptButton = new Button("Interrupt Download");;

	private boolean isInterrupted = false;

	public View infodockView = new View("Web client", null, this);

	// static boolean doNewLine=false;
	public static Checkbox doNewLineCheckbox = new Checkbox("Do new line", true);

	public WebClient(final TabWindow defaultTabWindow) {
		// super(title);
		setBackground(Color.lightGray);
		setLayout(new BorderLayout(5, 30));
		final int fontSize = 14;
		final Font labelFont = new Font("Serif", Font.BOLD, fontSize);
		final Font textFont = new Font("Monospaced", Font.BOLD, fontSize - 2);
		final Panel inputPanel = new Panel();
		inputPanel.setLayout(new BorderLayout());
		final Panel labelPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		// labelPanel.setLayout(new Flo(1, 3));
		hostField = new LabeledTextField("Host:", labelFont,
				"", 30, textFont);
		portField = new LabeledTextField("Port:", labelFont, "80", 5, textFont);
		// Use HTTP 1.0 for compatibility with the most servers.
		// If you switch this to 1.1, you *must* supply a
		// Host: request header.
		/*
		 * requestLineField = new LabeledTextField("Request Line:", labelFont,
		 * "GET / HTTP/1.0", 50, textFont);
		 */
		labelPanel.add(hostField);
		labelPanel.add(portField);
		labelPanel.add(submitButton);
		labelPanel.add(cloneButton);
		labelPanel.add(interruptButton);
		labelPanel.add(doNewLineCheckbox);
		// labelPanel.add(requestLineField);
		/*
		 * Label requestHeadersLabel = new Label("Request Headers:");
		 */
		// requestHeadersLabel.setFont(labelFont);
		// labelPanel.add(requestHeadersLabel);
		inputPanel.add(labelPanel, BorderLayout.NORTH);
		requestHeadersArea = new MyTextArea(6, 80);
		requestHeadersArea.setFont(textFont);
		requestHeadersArea.setText("GET / HTTP/1.1");
		RTextScrollPane requestHeadersAreaScrollPane = MyTextArea
				.buildRTextScrollPane(requestHeadersArea);
		inputPanel.add(requestHeadersAreaScrollPane, BorderLayout.CENTER);
		final Panel buttonPanel = new Panel();
		submitButton.addActionListener(this);
		cloneButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				WebClient webClient = new WebClient(defaultTabWindow);
				webClient.requestHeadersArea.setText(requestHeadersArea
						.getText());
				webClient.hostField.getTextField().setText(
						hostField.getTextField().getText());
				webClient.portField.getTextField().setText(
						portField.getTextField().getText());
				// TODO correct
				final TabWindow tabWindow2 = IdwUtils
						.findVisibleTabWindow(infodockView,defaultTabWindow);
				IdwUtils.addTab(tabWindow2, webClient.infodockView,defaultTabWindow);
//				TcpMonUtils.utilsTabWindows.addTab(webClientView);
//				webClientView.getWindowProperties().setCloseEnabled(false);
			}
		});
		infodockView.getWindowProperties().setCloseEnabled(false);
		submitButton.setFont(labelFont);
		// buttonPanel.add(submitButton);
		inputPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(inputPanel, BorderLayout.NORTH);
		final Panel resultPanel = new Panel();
		resultPanel.setLayout(new BorderLayout());
		interruptButton.setEnabled(false);
		// final Label resultLabel = new Label("Results", Label.CENTER);
		// resultLabel.setFont(headingFont);
		// resultPanel.add(resultLabel, BorderLayout.NORTH);
		resultArea = new MyTextArea();
		resultArea.setEditable(false);
		resultArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
		RTextScrollPane resultAreaScrollPane = MyTextArea
				.buildRTextScrollPane(resultArea);
		resultPanel.add(resultAreaScrollPane, BorderLayout.CENTER);
		final Panel interruptPanel = new Panel();
		interruptButton.addActionListener(this);
		interruptButton.setFont(labelFont);
		// interruptPanel.add(interruptButton);
		resultPanel.add(interruptPanel, BorderLayout.SOUTH);
		add(resultPanel, BorderLayout.CENTER);
		// setSize(600, 700);
		// setVisible(true);
		hostField.getTextField().addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				// log.info("key typed");
				// log.info(e);
				if (httpClient == null || httpClient.isStopDownload()) {
					if (e.getKeyChar() == '\n') {
						final Thread downloader = new Thread(WebClient.this);
						downloader.start();
					}
				}
			}

		});
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		if (event.getSource() == submitButton) {
			final Thread downloader = new Thread(this);
			downloader.start();
		} else if (event.getSource() == interruptButton) {
			isInterrupted = true;
			// submitButton.setEnabled(true);
		}
	}

	@Override
	public void run() {
		String host = hostField.getTextField().getText();
		log.info("connection to " + host);
		isInterrupted = false;
		if (hasLegalArgs()) {
			interruptButton.setEnabled(true);
			submitButton.setEnabled(false);
			try {
				httpClient = new HttpClient(host, port, requestHeaders,
						resultArea, this);
			} catch (Exception e) {
				log.info(e);
				final String s = JrrUtils.exceptionToString(JrrUtils
						.getRootException(e));
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						resultArea.setText(s);

					}

				});
				downloadFinish();
			}
		}
	}

	@Override
	public boolean isInterrupted() {
		return (isInterrupted);
	}

	private boolean hasLegalArgs() {
		String host = hostField.getTextField().getText();
		if (host.length() == 0) {
			report("Missing hostname");
			return (false);
		}
		final String portString = portField.getTextField().getText();
		if (portString.length() == 0) {
			report("Missing port number");
			return (false);
		}
		try {
			port = Integer.parseInt(portString);
		} catch (final NumberFormatException nfe) {
			report("Illegal port number: " + portString);
			return (false);
		}
		/*
		 * requestLine = requestLineField.getTextField().getText(); if
		 * (requestLine.length() == 0) { report("Missing request line");
		 * return(false); }
		 */
		getRequestHeaders();
		return (true);
	}

	private void report(final String s) {
		resultArea.setText(s);
	}

	private void getRequestHeaders() {
		for (int i = 0; i < requestHeaders.length; i++)
			requestHeaders[i] = null;
		int headerNum = 0;
		final String header = requestHeadersArea.getText();
		final StringTokenizer tok = new StringTokenizer(header, "\r\n");
		while (tok.hasMoreTokens())
			requestHeaders[headerNum++] = tok.nextToken();
	}

	@Override
	public void downloadFinish() {
		interruptButton.setEnabled(false);
		submitButton.setEnabled(true);
	}
}
