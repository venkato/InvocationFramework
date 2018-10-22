package net.sf.jremoterun.utilities.nonjdk.antutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import org.apache.tools.ant.taskdefs.Zip
import org.apache.tools.ant.types.ZipFileSet

import java.util.logging.Logger

@CompileStatic
class ZipElements {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static Zip.Duplicate createDuplicateFailed() {
        Zip.Duplicate duplicate = new Zip.Duplicate()
        duplicate.setValue('fail')
        return duplicate
    }

    static Zip.WhenEmpty createEmptyFailed() {
        Zip.WhenEmpty emptyFailed = new Zip.WhenEmpty()
        emptyFailed.setValue('fail')
        return emptyFailed
    }

    static Zip createArchive(File destFile, List<? extends ZipElement> els) {
        assert destFile.parentFile.exists()
        Zip zip = JrrAntUtils2.createZipTask()
        els.collect {
            try {
                createZipFileSet(it)
            }catch(Throwable e){
                log.info "failed handle ${it} : ${e}"
                throw e
            }
        }.each { zip.addZipfileset(it) }
        zip.setDestFile(destFile)
        zip.setUpdate(false)
        zip.setDuplicate(createDuplicateFailed())
        zip.setWhenempty(createEmptyFailed())
        destFile.delete()
        assert !destFile.exists()
        return zip
    }


    static Zip createArchive2(File destFile, List<? extends ZipElement2> els) {
        assert destFile.parentFile.exists()
        Zip zip = JrrAntUtils2.createZipTask()
        els.collect {
            try {
                createZipFileSet(it)
            }catch(Throwable e){
                log.info "failed handle ${it} : ${e}"
                throw e
            }
        }.each { zip.addZipfileset(it) }
        zip.setDestFile(destFile)
        zip.setUpdate(false)
        zip.setDuplicate(createDuplicateFailed())
        zip.setWhenempty(createEmptyFailed())
        destFile.delete()
        assert !destFile.exists()
        return zip
    }

    static ZipFileSet createZipFileSet(ZipElement location) {
        File baseDir1 = location.getBaseDir()
        JrrUtilities.checkFileExist(baseDir1)
        ZipFileSet zipFileSet = new ZipFileSet()
        zipFileSet.setDir(baseDir1)
        zipFileSet.setIncludes(location.getIncludes())
        String excludes = location.getExcludes()
        if (excludes != null) {
            zipFileSet.setExcludes(excludes)
        }
        zipFileSet.setPrefix(location.name())
        return zipFileSet
    }


    static ZipFileSet createZipFileSet(ZipElement2 location) {
        File baseDir1 = location.getBaseDir().resolveToFile()
        JrrUtilities.checkFileExist(baseDir1)
        ZipFileSet zipFileSet = new ZipFileSet()
        zipFileSet.setDir(baseDir1)
        zipFileSet.setIncludes(location.getIncludes())
        String excludes = location.getExcludes()
        if (excludes != null) {
            zipFileSet.setExcludes(excludes)
        }
        zipFileSet.setPrefix(location.name())
        return zipFileSet
    }

}
