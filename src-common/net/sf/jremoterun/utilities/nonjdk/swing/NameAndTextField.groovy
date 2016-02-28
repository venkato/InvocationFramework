package net.sf.jremoterun.utilities.nonjdk.swing

import net.sf.jremoterun.utilities.JrrClassUtils


import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import java.awt.BorderLayout
import java.awt.Dimension
import java.util.logging.Logger

public class NameAndTextField extends JPanel {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	private JLabel label;

	private JTextField textField;
	private int minWidth;

	public NameAndTextField(String labelName, String textField, int minWidth) {
		super(new BorderLayout(3, 3));
		this.label = new JLabel(labelName);
		this.textField = new JTextField(textField);
		// this.textField.setc
		add(label, BorderLayout.WEST);
		add(this.textField, BorderLayout.CENTER);
		this.minWidth = minWidth;
	}

	public NameAndTextField(String labelName, String textField) {
		this(labelName, textField, 30);
		// this.textField.setMinimumSize(new Dimension(300,10));
	}

	public JLabel getLabel() {
		return label;
	}

	public JTextField getTextField() {
		return textField;
	}

	public void setText(String text) {
		textField.setText(text);
	}

	public String getText() {
		return textField.getText();
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dimension = new Dimension(super.getPreferredSize());
//		int min = dimension.width + minWidth;
		if (dimension.width <  minWidth) {
			dimension.width = minWidth;
		}
		return dimension;
	}


	@Override
	public boolean requestFocusInWindow() {
		return textField.requestFocusInWindow();
	}

}
