package net.sf.jremoterun.utilities.nonjdk.ziputil

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface LineHandlerSupport {

    void closeStream() throws IOException;


    InputStream openStream()  throws IOException;
}
