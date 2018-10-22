package net.sf.jremoterun.utilities.nonjdk.asmow2.accessmodif

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.zeroturnaround.zip.ZipEntryCallback
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@CompileStatic
class JarAccessModifController extends AccessModifController {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ZipOutputStream zipOutputStream


    HashSet<String> zipEntries = new HashSet<>()
    HashSet<String> duplicatedEntries = new HashSet<>()
    HashSet<String> skippedEntries = new HashSet<>()

    void handleJarSelf(File inJarFile) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        handleJarImpl(inJarFile, outputStream)
        inJarFile.bytes = outputStream.toByteArray()
    }

    void handleJar(File inJarFile, File outJar) {
        BufferedOutputStream outputStream = outJar.newOutputStream()
        handleJarImpl(inJarFile, outputStream)
    }

    void handleJarImpl(File inJarFile, OutputStream outputStream) {
//        BufferedOutputStream outputStream = outJarFile.newOutputStream();
        zipOutputStream = new ZipOutputStream(outputStream)
        ZipEntryCallback zipEntryCallback = createZipEntryCallback()
        ZipUtil.iterate(inJarFile, zipEntryCallback)
        zipOutputStream.flush()
        zipOutputStream.close()
        onFinish()
    }


    ZipEntryCallback createZipEntryCallback(){
        return new ZipEntryCallbackAm(this)
    }

    void onDuplicate(ZipEntry zipEntry) {

    }

    String saveAsName(ZipEntry zipEntry) {
        return zipEntry.getName()
    }
}
