package timmoson.common.sertcp

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.nio.charset.Charset;
import java.util.logging.Logger;

@CompileStatic
class TimmosonSettings {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Charset defaultEncoding = Charset.forName('cp1251')


}
