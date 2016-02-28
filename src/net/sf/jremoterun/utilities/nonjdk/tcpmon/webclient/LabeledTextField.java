package net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient;

import groovy.transform.CompileStatic;

import java.awt.*;

/**
 * A TextField with an associated Label.
 * <P>
 * Taken from Core Servlets and JavaServer Pages from Prentice Hall and Sun
 * Microsystems Press, http://www.coreservlets.com/. &copy; 2000 Marty Hall; may
 * be freely used or adapted.
 */

@CompileStatic
public class LabeledTextField extends Panel {

    private final Label label;

    private final TextField textField;

    public LabeledTextField(final String labelString, final Font labelFont,
            final int textFieldSize, final Font textFont) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        label = new Label(labelString, Label.RIGHT);
        if (labelFont != null)
            label.setFont(labelFont);
        add(label);
        textField = new TextField(textFieldSize);
        if (textFont != null)
            textField.setFont(textFont);
        add(textField);
    }

    public LabeledTextField(final String labelString,
            final String textFieldString) {
        this(labelString, null, textFieldString, textFieldString.length(), null);
    }

    public LabeledTextField(final String labelString, final int textFieldSize) {
        this(labelString, null, textFieldSize, null);
    }

    public LabeledTextField(final String labelString, final Font labelFont,
            final String textFieldString, final int textFieldSize,
            final Font textFont) {
        this(labelString, labelFont, textFieldSize, textFont);
        textField.setText(textFieldString);
    }

    /**
     * The Label at the left side of the LabeledTextField. To manipulate the
     * Label, do:
     * 
     * <PRE>
     *    LabeledTextField ltf = new LabeledTextField(...);
     *    ltf.getLabel().someLabelMethod(...);
     * </PRE>
     * 
     * @see #getTextField
     */

    public Label getLabel() {
        return (label);
    }

    /**
     * The TextField at the right side of the LabeledTextField.
     * 
     * @see #getLabel
     */

    public TextField getTextField() {
        return (textField);
    }
    
    
}
