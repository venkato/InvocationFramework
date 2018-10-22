package net.sf.jremoterun.utilities.nonjdk.ziputil

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.nio.charset.Charset
import java.util.logging.Logger
import java.util.zip.GZIPInputStream

@CompileStatic
class StdLineHandlerSupport implements LineHandlerSupport{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    public File file;
    public BufferedInputStream bufferedInputStream;


    StdLineHandlerSupport(File file) {
        this.file = file
    }



    void closeStream(){
        bufferedInputStream.close()
    }


    InputStream openStream() {
        bufferedInputStream = file.newInputStream()
    }

}
