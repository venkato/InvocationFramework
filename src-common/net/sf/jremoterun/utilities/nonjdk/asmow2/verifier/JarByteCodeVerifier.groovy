package net.sf.jremoterun.utilities.nonjdk.asmow2.verifier

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.AsmUtils
import org.apache.commons.io.IOUtils
import org.zeroturnaround.zip.ZipEntryCallback
import org.zeroturnaround.zip.ZipUtil;

import java.util.logging.Logger
import java.util.zip.ZipEntry;

@CompileStatic
class JarByteCodeVerifier implements ZipEntryCallback {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AsmUtils asmUtils = new AsmUtils()

    AsmByteCodeVerifier asmByteCodeVerifier = new AsmByteCodeVerifier(getClass().getClassLoader())

    void verifyJar(File jar) {
        assert jar.exists()
        ZipUtil.iterate(jar, this)
    }

    @Override
    void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
        if (needVerify(zipEntry)) {
            byte[] array = IOUtils.toByteArray(inputStream)
            String name = zipEntry.getName()
            try {
                asmByteCodeVerifier.verifyByteCode(array)
            } catch (Exception e) {
                log.info "failed verify : ${name} : ${e}"
                throw e
            }
        }
    }

    boolean needVerify(ZipEntry zipEntry) {
        return zipEntry.getName().endsWith('.class')
    }
}
