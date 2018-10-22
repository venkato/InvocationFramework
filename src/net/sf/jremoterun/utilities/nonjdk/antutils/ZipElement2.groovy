package net.sf.jremoterun.utilities.nonjdk.antutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2

@CompileStatic
interface ZipElement2 {

    public static String includesAll = "**/*";

    String name();

    ToFileRef2 getBaseDir();

    String getIncludes()

    String getExcludes()


}
