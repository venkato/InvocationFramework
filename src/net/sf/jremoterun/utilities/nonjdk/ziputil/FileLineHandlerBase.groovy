package net.sf.jremoterun.utilities.nonjdk.ziputil

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.nio.charset.Charset
import java.util.logging.Logger

/**
 * TODO use
 * @see org.apache.commons.compress.compressors.CompressorStreamFactory#createCompressorInputStream(java.lang.String, java.io.InputStream, boolean)
 */
@CompileStatic
abstract class FileLineHandlerBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public final File file;
    public final LineHandlerSupport lineHandlerSupport
    public InputStream inputStream;
    public BufferedReader reader;
    public int processedLines = 0;
    public boolean autoClose = true;
    public boolean reachedEof = false;
    public boolean streamClosed = false;


    FileLineHandlerBase(File file) {
        this.file = file
        if (file.getName().endsWith('.gz')) {
            lineHandlerSupport = new GzipLineHandlerSupport(file)
        } else {
            lineHandlerSupport = new StdLineHandlerSupport(file)
        }
    }

    FileLineHandlerBase(File file, LineHandlerSupport lineHandlerSupport) {
        this.file = file
        this.lineHandlerSupport = lineHandlerSupport
    }


    String readNextLine()  throws IOException{
        if (reader == null) {
            openStream()
        }
        String line = reader.readLine();
        if (line == null) {
            reachedEof = true;
            if (autoClose) {
                onClose()
            }
            return null
        }
        processedLines++;
        return line
    }


    void onClose() throws IOException {
        if (!streamClosed) {
            try {
                lineHandlerSupport.closeStream()
                streamClosed = true
            } catch (Throwable e) {
                onCloseException(e);
            }
        }
    }

    void onCloseException(Throwable e) {
        throw e;
    }


    void openStream() {
        inputStream = lineHandlerSupport.openStream()
        reader = new BufferedReader(new InputStreamReader(inputStream, getCharset()));
    }

    String getCharset() {
        return Charset.defaultCharset()
    }


}
