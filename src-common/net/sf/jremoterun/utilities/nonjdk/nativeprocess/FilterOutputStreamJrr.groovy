package net.sf.jremoterun.utilities.nonjdk.nativeprocess

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.io.output.TeeOutputStream
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@CompileStatic
class FilterOutputStreamJrr extends OutputStream {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public List<OutputStream> allOut = []

    public OutputStream out;


    FilterOutputStreamJrr(OutputStream out) {
        this.out =  out;
    }

    @Override
    void write(@NotNull byte[] b, int off, int len) throws IOException {
        out.write(b, off, len)
    }

    @Override
    void write(int b) throws IOException {
        out.write(b)
    }

    @Override
    void write(@NotNull byte[] b) throws IOException {
        out.write(b)
    }

    @Override
    void flush() throws IOException {
        out.flush()
    }

    @Override
    void close() throws IOException {
        out.close()
    }

    void addNonClosableStream(OutputStream newOut){
        assert out != newOut
        addStream(new NonClosableStream(newOut));
    }


    void addStream(OutputStream newOut) {
        assert out != newOut
        out = new TeeOutputStream(out, newOut);
        allOut.add(newOut)
    }
}
