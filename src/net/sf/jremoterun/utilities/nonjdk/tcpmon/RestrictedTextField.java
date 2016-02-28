package net.sf.jremoterun.utilities.nonjdk.tcpmon;

import groovy.transform.CompileStatic;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * a text field with a restricted set of characters
 */
@CompileStatic
class RestrictedTextField extends JTextField {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();

    protected String validText;

    public RestrictedTextField(final String validText) {
        setValidText(validText);
    }

    public RestrictedTextField(final int columns, final String validText) {
        super(columns);
        setValidText(validText);
    }

    public RestrictedTextField(final String text, final String validText) {
        super(text);
        setValidText(validText);
    }

    public RestrictedTextField(final String text, final int columns,
                               final String validText) {
        super(text, columns);
        setValidText(validText);
    }

    private void setValidText(final String validText) {
        this.validText = validText;
    }

    /**
     * fascinatingly, this method is called in the super() constructor,
     * meaning before we are fully initialized. C++ doesnt actually permit
     * such a situation, but java clearly does...
     *
     * @return a new document
     */
    @Override
    public Document createDefaultModel() {
        return new RestrictedDocument();
    }

    /**
     * this class strips out invaid chars
     */
    class RestrictedDocument extends PlainDocument {

        /**
         * Constructs a plain text document. A default model using
         * <code>GapContent</code> is constructed and set.
         */
        public RestrictedDocument() {

        }

        /**
         * add a string; only those chars in the valid text list are allowed
         *
         * @param offset
         * @param string
         * @param attributes
         * @throws BadLocationException
         */
        @Override
        public void insertString(final int offset, final String string,
                final AttributeSet attributes) throws BadLocationException {

            if (string == null) {
                return;
            }
            final int len = string.length();
            final StringBuffer buffer = new StringBuffer(string.length());
            for (int i = 0; i < len; i++) {
                final char ch = string.charAt(i);
                if (validText.indexOf(ch) >= 0) {
                    buffer.append(ch);
                }
            }
            super.insertString(offset, new String(buffer), attributes);
        }
    } // end class NumericDocument
}
