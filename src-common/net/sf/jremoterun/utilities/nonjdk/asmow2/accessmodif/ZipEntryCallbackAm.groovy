package net.sf.jremoterun.utilities.nonjdk.asmow2.accessmodif

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.io.IOUtils
import org.zeroturnaround.zip.ZipEntryCallback;

import java.util.logging.Logger
import java.util.zip.ZipEntry

@CompileStatic
class ZipEntryCallbackAm implements ZipEntryCallback {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    JarAccessModifController accessModifController

    ZipEntryCallbackAm(JarAccessModifController accessModifController) {
        this.accessModifController = accessModifController
    }

    @Override
    void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
        if (accessModifController.needEntry(zipEntry)) {
            String name = zipEntry.getName()
            if (accessModifController.zipEntries.contains(name)) {
                accessModifController.onDuplicate(zipEntry)
                accessModifController.duplicatedEntries.add(name)
            }else {
                byte[] array = IOUtils.toByteArray(inputStream)
                if (name.endsWith(".class")) {
                    array = accessModifController.removeFinalModifier(name, array)
                }
                String saveAsName = accessModifController.saveAsName(zipEntry)
                ZipEntry entryNew = new ZipEntry(saveAsName);
                accessModifController.zipOutputStream.putNextEntry(entryNew);
                accessModifController.zipOutputStream.write(array, 0, array.length)
                accessModifController.zipOutputStream.closeEntry()
                accessModifController.zipEntries.add(zipEntry.getName())
            }
        } else {
            accessModifController.skippedEntries.add(zipEntry.getName())
        }
    }

}
