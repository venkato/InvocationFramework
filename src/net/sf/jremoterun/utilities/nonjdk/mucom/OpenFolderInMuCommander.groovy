package net.sf.jremoterun.utilities.nonjdk.mucom;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
abstract class OpenFolderInMuCommander {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static OpenFolderInMuCommander openFolderInMuCommander


    abstract void openInMuCommander(String host,String folder)

}
