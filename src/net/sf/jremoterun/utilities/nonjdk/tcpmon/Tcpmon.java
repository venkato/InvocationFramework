package net.sf.jremoterun.utilities.nonjdk.tcpmon;

import groovy.transform.CompileStatic;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * TCP monitor to log http messages and responses, both SOAP and plain HTTP.
 */
@CompileStatic
public class  Tcpmon {

	public static final int maxLength = 1000 * 100;

	final static String save = getMessage("save00", "Save");
	final static String resend = getMessage("resend00", "Resend");
	private static final Logger log = LogManager.getLogger();

	private View adminView;

	private final TabWindow notebook = new TabWindow();

	static final int STATE_COLUMN = 0;

	static final int OUTHOST_COLUMN = 3;

	static final int REQ_COLUMN = 4;

	public static final Map<String, String> syntaxisFields = new TreeMap();
	public static final Map<String, String> syntaxisFieldsInverse = new TreeMap();

	static {
		try {
			for (final Field field : SyntaxConstants.class.getFields()) {
				final String fiName = field.getName().substring(13)
						.toLowerCase().replace('_', ' ');
				final String value = (String) field.get(null);
				syntaxisFields.put(fiName, value);
				syntaxisFieldsInverse.put(value, fiName);
			}
		} catch (final Exception e) {
			throw new Error(e);
		}
	}


	public Tcpmon(File addHostScript) {
		// BEGIN BORLAND JBUILDER PATCH
		// just adding space between TCP and Monitor to comply with UI
		// guidelines
		// super(getMessage("t100", "TCP Monitor"));
		// END BORLAND JBUILDER PATCH

		// this.getContentPane().add(notebook);
		String adminName = getMessage("admin00", "Admin");

		AdminPage2 adminPage = new AdminPage2(addHostScript);
		adminView = new View(adminName, null, adminPage.getMainPanel());
		notebook.addTab(adminView);
		notebook.repaint();
		// notebook.setSelectedIndex(notebook.getTabCount() - 1);
		adminView.getWindowProperties().setCloseEnabled(false);
		adminView.requestFocus();
		adminView.requestFocusInWindow();
		adminPage.defaultView = adminView
		;
	}

	public void addListener(final int listenPort, final String name,
			final String targetHost, final int targetPort, boolean doStart,
			final String requestEncoding, final String responseEncoding,
			final boolean changeHost, final String inputStyle,
			final String outputStyle, final ShowMsgData showMsgData) {
		final TabWindow tabWindow = notebook;
		final Listener l = new Listener(tabWindow, name, listenPort,
				targetHost, targetPort, targetHost == null, null, changeHost,
				showMsgData, inputStyle, outputStyle, doStart);
		// notebook.setSelectedIndex(1);
		l.requestEncoding.setText(requestEncoding);
		l.responseEncoding.setText(responseEncoding);
		l.HTTPProxyHost = System.getProperty("http.proxyHost");
		if (l.HTTPProxyHost != null && l.HTTPProxyHost.equals("")) {
			l.HTTPProxyHost = null;
		}

		if (l.HTTPProxyHost != null) {
			String tmp = System.getProperty("http.proxyPort");

			if (tmp != null && tmp.equals("")) {
				tmp = null;
			}
			if (tmp == null) {
				l.HTTPProxyPort = 80;
			} else {
				l.HTTPProxyPort = Integer.parseInt(tmp);
			}
		}
		// return l;
	}

	// protected void processWindowEvent(final WindowEvent event) {
	// switch (event.getID()) {
	// case WindowEvent.WINDOW_CLOSING:
	// exit();
	// break;
	//
	// default:
	// super.processWindowEvent(event);
	// break;
	// }
	// }

	// private void exit() {
	// // BEGIN BORLAND JBUILDER PATCH
	// // replace System.exit with jbuilder friendly close
	// for (int i = 1; i < notebook.getTabCount(); i++) {
	// ((Listener) notebook.getComponentAt(i)).close();
	// }
	// setVisible(false);
	// dispose();
	// // System.exit(0);
	// // END BORLAND JBUILDER PATCH
	// }

	// public void setInputPort(final int port) {
	// }

	// public void setOutputHostPort(final char hostName, final int port) {
	// }

	/**
	 * set up the L&F
	 */
	private static void setupLookAndFeel(final boolean nativeLookAndFeel)
			throws Exception {
		UIManager.setLookAndFeel(nativeLookAndFeel ? UIManager
				.getSystemLookAndFeelClassName() : UIManager
				.getCrossPlatformLookAndFeelClassName());
		// JFrame.setDefaultLookAndFeelDecorated(true);
	}

	// public static void main(final String[] args) {
	// try {
	// // switch between swing L&F here
	// setupLookAndFeel(true);
	// if (args.length == 3) {
	// final int p1 = Integer.parseInt(args[0]);
	// final int p2 = Integer.parseInt(args[2]);
	//
	// new Tcpmon(p1, args[1], p2);
	// } else if (args.length == 1) {
	// final int p1 = Integer.parseInt(args[0]);
	//
	// new Tcpmon(p1, null, 0);
	// } else if (args.length != 0) {
	// System.err.println(getMessage("usage00", "Usage:")
	// + " t1 [listenPort targetHost targetPort]\n");
	// } else {
	// new Tcpmon(0, null, 0);
	// }
	// } catch (final Throwable exp) {
	// log.info(null, exp);
	// }
	// }

	// Message resource bundle.
	private static ResourceBundle messages = null;

	/**
	 * Get the message with the given key. There are no arguments for this
	 * message.
	 */
	public static String getMessage(final String key, final String defaultMsg) {
		try {
			if (messages == null) {
				initializeMessages();
			}
			return messages.getString(key);
		} catch (final Throwable t) {
			// log.info(null,t);
			// If there is any problem whatsoever getting the internationalized
			// message, return the default.
			return defaultMsg;
		}
	} // getMessage

	/**
	 * Load the resource bundle messages from the properties file. This is ONLY
	 * done when it is needed. If no messages are printed (for example, only
	 * Wsdl2java is being run in non- verbose mode) then there is no need to
	 * read the properties file.
	 */
	private static void initializeMessages() {
		messages = ResourceBundle.getBundle("org.apache.axis.utils.t1");
	} // initializeMessages

	public TabWindow getNotebook() {
		return notebook;
	}

	static void setTextArea(final MyTextArea inputText) {
		inputText.setHyperlinksEnabled(false);
		// outputText.setHyperlinksEnabled(false);
		// outputText.setEditable(false);
		inputText.setWordTransfering(true);
		// inputText.setLinkScanningMask(DEFAULT_PORT)
		// outputText.setLineWrap(true);
		inputText.setEditable(false);

	}
}
