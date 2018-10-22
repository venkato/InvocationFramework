package net.sf.jremoterun.utilities.nonjdk.swing


import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextArea
import org.fife.ui.rtextarea.RTextScrollPane

import javax.swing.*
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

public class MyTextArea extends RSyntaxTextArea {



	private long editsTry = 0;

	public static final Font defaultFont = new Font("Dialog", Font.PLAIN, 12);

	public MyTextArea() {
		setFont(defaultFont);
	}

	public MyTextArea(final boolean editable) {
		setFont(defaultFont);
		setEditable(editable);
	}

	public MyTextArea(final Object object, final Object object2, final int i,
			final int j) {
		super(null, null, i, j);
		setFont(defaultFont);
	}

	public MyTextArea(final int i, final int j) {
		super(i, j);
	}

	public void appendInSwingThread(final String str) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				append(str);
				// log.info(str.substring(0,10));
				// getParent().getParent().getParent().
				// getParent().getParent().getParent().repaint();
			}
		});
	}

	@Override
	public void append(final String str) {
		super.append(str);
		if (!isEditable()) {
			editsTry++;
			if (editsTry > 5) {
				// discarding for avoiding memory leak
				discardAllEdits();
				editsTry = 0;
			}
		}
	}

	public void setTextInSwingThread(final String text) {
		if (SwingUtilities.isEventDispatchThread()) {
			setText(text);

		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					setText(text);
				}
			});
		}
	}

	@Override
	public void setText(final String t) {
		super.setText(t);
		if (!isEditable()) {
			editsTry++;
			if (editsTry > 5) {
				// discarding for avoiding memory leak
				discardAllEdits();
				editsTry = 0;
			}
		}
		super.setSelectionStart(0);
		super.setSelectionEnd(0);
		// super.getDocument().

	}

	public void setWordTransfering(final boolean transfer) {
		setLineWrap(transfer);
		setWrapStyleWord(transfer);
	}

	public static RTextScrollPane buildRTextScrollPane(final MyTextArea textArea) {
		final RTextScrollPane scrollPane = new RTextScrollPane(textArea, true);
		textArea.setWordTransfering(true);
		// scrollPane.setLineNumbersEnabled(true);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		return scrollPane;
	}

	@Override
	protected JPopupMenu createPopupMenu() {
		final JPopupMenu jpopupmenu = new JPopupMenu();
		JMenuItem jmenuitem = null;
		if (isEditable()) {
			jmenuitem = new JMenuItem(getAction(RTextArea.UNDO_ACTION));
			jmenuitem.setAccelerator(null);
			jmenuitem.setToolTipText(null);
			jpopupmenu.add(jmenuitem);
			jmenuitem = new JMenuItem(getAction(RTextArea.REDO_ACTION));
			jmenuitem.setAccelerator(null);
			jmenuitem.setToolTipText(null);
			jpopupmenu.add(jmenuitem);
			jpopupmenu.addSeparator();
			jmenuitem = new JMenuItem(getAction(RTextArea.CUT_ACTION));
			jmenuitem.setAccelerator(null);
			jmenuitem.setToolTipText(null);
			jpopupmenu.add(jmenuitem);
		}
		jmenuitem = getCopyMenuItem();
		jpopupmenu.add(jmenuitem);
		if (isEditable()) {
			jmenuitem = new JMenuItem(getAction(RTextArea.PASTE_ACTION));
			jmenuitem.setAccelerator(null);
			jmenuitem.setToolTipText(null);
			jpopupmenu.add(jmenuitem);
			jmenuitem = new JMenuItem(getAction(RTextArea.DELETE_ACTION));
			jmenuitem.setAccelerator(null);
			jmenuitem.setToolTipText(null);
			jpopupmenu.add(jmenuitem);
		}
		// jpopupmenu.addSeparator();

		jmenuitem = new JMenuItem(getAction(RTextArea.SELECT_ALL_ACTION));
		jmenuitem.setAccelerator(null);
		jmenuitem.setToolTipText(null);
		jpopupmenu.add(jmenuitem);
		return jpopupmenu;

	}

	public JMenuItem getCopyMenuItem() {
		final JMenuItem copyMenuItem = new JMenuItem("Copy");
		final String text = getText();
		if ((text == null) || text.length() == 0 || text.trim().length() == 0) {
			copyMenuItem.setEnabled(false);
		} else {
			copyMenuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final int i = getSelectionStart();
					final int j = getSelectionEnd();
					if (i == j) {
						Toolkit.getDefaultToolkit()
								.getSystemClipboard()
								.setContents(
										new StringSelection(text.replaceAll(
												"\\r\\n", "\n")), null);
					} else {
						copyAsRtf();
					}
				}
			});
		}
		return copyMenuItem;
	}

}
