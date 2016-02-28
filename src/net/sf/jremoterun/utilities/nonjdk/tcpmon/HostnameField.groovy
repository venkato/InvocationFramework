package net.sf.jremoterun.utilities.nonjdk.tcpmon;

import groovy.transform.CompileStatic;

/**
 * hostname fields
 */
@CompileStatic
class HostnameField extends RestrictedTextField {

    // list of valid chars in a hostname
    private static final String VALID_TEXT = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWZYZ-.';

    public HostnameField(final int columns) {
        super(columns, VALID_TEXT);
    }

    public HostnameField() {
        super(VALID_TEXT);
    }
}
