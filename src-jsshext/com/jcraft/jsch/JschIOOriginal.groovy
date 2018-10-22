package com.jcraft.jsch

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JschIOOriginal extends IO{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void setOutputStream(OutputStream out) {
        super.setOutputStream(out)
    }

    @Override
    void setOutputStream(OutputStream out, boolean dontclose) {
        super.setOutputStream(out, dontclose)
    }

    @Override
    void setExtOutputStream(OutputStream out) {
        super.setExtOutputStream(out)
    }

    @Override
    void setExtOutputStream(OutputStream out, boolean dontclose) {
        super.setExtOutputStream(out, dontclose)
    }

    @Override
    void setInputStream(InputStream inputStream) {
        super.setInputStream(inputStream)
    }

    @Override
    void setInputStream(InputStream inputStream, boolean dontclose) {
        super.setInputStream(inputStream, dontclose)
    }

    @Override
    void put(byte[] array, int begin, int length) throws IOException {
        super.put(array, begin, length)
    }

    @Override
    void put_ext(byte[] array, int begin, int length) throws IOException {
        super.put_ext(array, begin, length)
    }

    @Override
    int getByte() throws IOException {
        return super.getByte()
    }

    @Override
    void getByte(byte[] array) throws IOException {
        super.getByte(array)
    }

    @Override
    void getByte(byte[] array, int begin, int length) throws IOException {
        super.getByte(array, begin, length)
    }

    @Override
    void out_close() {
        super.out_close()
    }
}
