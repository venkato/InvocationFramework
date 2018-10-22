package net.sf.jremoterun.utilities.nonjdk.nativeprocess

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@CompileStatic
class NonClosableStream extends FilterOutputStreamJrr{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    NonClosableStream(OutputStream out) {
        super(out)
    }

    @Override
    void close() throws IOException {

    }
}
