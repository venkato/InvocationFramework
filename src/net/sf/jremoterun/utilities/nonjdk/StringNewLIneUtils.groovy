package net.sf.jremoterun.utilities.nonjdk;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class StringNewLIneUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static String normalizeLine(String s ){
        s = s.replace('\r\n','\n');
        s = s.replace('\r','\n');
        return s
    }

}
