package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic;

import javax.swing.*;

@CompileStatic
public interface Shortcuts {

    String getDisplayName();

    KeyStroke getKeyStroke();


}
