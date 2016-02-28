package net.sf.jremoterun.utilities.nonjdk.tcpmon

import groovy.transform.CompileStatic;
import org.apache.logging.log4j.LogManager;

/**
 * because we cant use Java1.4's JFormattedTextField, here is a class that
 * accepts numbers only
 */
@CompileStatic
class NumberField extends RestrictedTextField {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();

    private static final String VALID_TEXT = "0123456789";

    /**
     * Constructs a new <code>TextField</code>. A default model is created,
     * the initial string is <code>null</code>, and the number of columns is
     * set to 0.
     */
    public NumberField() {
        super(VALID_TEXT);
    }

    /**
     * Constructs a new empty <code>TextField</code> with the specified
     * number of columns. A default model is created and the initial string
     * is set to <code>null</code>.
     *
     * @param columns
     *            the number of columns to use to calculate the preferred
     *            width; if columns is set to zero, the preferred width will
     *            be whatever naturally results from the component
     *            implementation
     */
    public NumberField(final int columns) {
        super(columns, VALID_TEXT);
    }

    /**
     * get the int value of a field, any invalid (non int) field returns the
     * default
     *
     * @param def
     *            default value
     * @return the field contents
     */
    public int getValue(final int deff) {
        int result = deff;
        final String text = getText();
        if (text != null && text.length() != 0) {
            try {
                result = Integer.parseInt(text);
            } catch (final NumberFormatException e) {
                log.info("", e);
            }
        }
        return result;
    }

    /**
     * set the text to a numeric value
     *
     * @param value
     *            number to assign
     */
    public void setValue(final int value) {
        setText(Integer.toString(value));
    }

} // end class NumericTextField
