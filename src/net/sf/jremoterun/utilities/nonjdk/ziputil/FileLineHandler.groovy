package net.sf.jremoterun.utilities.nonjdk.ziputil

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.nio.charset.Charset
import java.util.logging.Logger
import java.util.zip.GZIPInputStream

/**
 * TODO use
 * @see org.apache.commons.compress.compressors.CompressorStreamFactory#createCompressorInputStream(java.lang.String, java.io.InputStream, boolean)
 */
@CompileStatic
abstract class FileLineHandler extends FileLineHandlerBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    FileLineHandler(File file) {
        super(file)
    }

    FileLineHandler(File file, LineHandlerSupport lineHandlerSupport) {
        super(file, lineHandlerSupport)
    }

    /**
     * return true - will continue processing
     * return false - need stop processing.
     *
     * Auto-detect EOF
     */
    abstract boolean handleLine(String line) throws Exception;

    void onFinishedFine() {

    }

    void runHandleEachLine() {
        openStream()
        try {
            while (true) {
                String line = readNextLine();
                if (line == null) {
                    break;
                }
                try {
                    boolean needContinue = handleLine(line)
                    if (!needContinue) {
                        break;
                    }
                } catch (Throwable e) {
                    onFailed(line, e)
                }
            }
            onFinishedFine()
        } finally {
            onClose()
        }
    }

    void onFailed(String line, Throwable exception1) {
        log.info("failed handle ${line} ${exception1}");
        throw exception1
    }


}
