package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class LastByteArrayOutputStream extends ByteArrayOutputStream {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static int msgSizeDefault = 240;
    /**
     * Value could be 8192 or higer
     * @see org.codehaus.groovy.runtime.ProcessGroovyMethods.ByteDumper
     */
    public static int bufferSizeDefault = 16_000;

    int msgSize = msgSizeDefault

    byte[] buf1 = new byte[bufferSizeDefault]
    byte[] buf2 = new byte[bufferSizeDefault]

    boolean usesFirst = true

    LastByteArrayOutputStream() {
        super(0)
        buf = buf1
    }

    void check() {
        if (count > msgSize) {
            checkImpl(false)
        }
        if (buf.is(buf1) || buf.is(buf2)) {
        } else {
            int len = Math.min(count, 80)
            String s = new String(buf, 0, len)
            log.info "got another ref : ${count} : ${s}"
            checkImpl(true)
        }
    }

    synchronized void checkImpl(boolean anotherRef) {
        int begin = count - msgSize
        if (begin < 0) {
            if (anotherRef) {

            } else {
                String s = new String(buf, 0, count)
                log.warn "${count} , ${s}"
                throw new IllegalStateException("${begin} ${count}")
            }
        }
        byte[] freeBuf = usesFirst ? buf2 : buf1
        System.arraycopy(buf, begin, freeBuf, 0, msgSize)
        count = msgSize
        buf = freeBuf
        usesFirst = !usesFirst
    }

    @Override
    synchronized void write(int b) {
        check()
        super.write(b)
        check()
    }

    @Override
    synchronized void write(byte[] b) throws IOException {
        check()
        super.write(b)
        check()
    }

    @Override
    synchronized void write(byte[] b, int off, int len) {
        check()
        super.write(b, off, len)
        check()
    }
}
