package net.sf.jremoterun.utilities.nonjdk.ziputil

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.nio.charset.Charset;
import java.util.logging.Logger
import java.util.zip.GZIPInputStream;

@CompileStatic
class GzipLineHandlerSupport implements LineHandlerSupport{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    public File file;
    public BufferedInputStream bufferedInputStream;

    public GZIPInputStream gzipInputStream;


    public GzipLineHandlerSupport(File file) {
        this.file = file
    }



    void closeStream(){
        bufferedInputStream.close()
    }


    InputStream openStream() {
        bufferedInputStream = file.newInputStream()
        gzipInputStream = new GZIPInputStream(bufferedInputStream)
    }


}
