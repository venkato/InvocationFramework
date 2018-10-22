package net.sf.jremoterun.utilities.nonjdk.antutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface ZipElement {

    public static String includesAll = "**/*";

    String name();

    File getBaseDir();

    String getIncludes()

    String getExcludes()


}
