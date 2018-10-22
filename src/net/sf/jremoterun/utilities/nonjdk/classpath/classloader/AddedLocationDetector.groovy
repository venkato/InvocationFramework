package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace
import org.zeroturnaround.zip.ZipEntryCallback
import org.zeroturnaround.zip.ZipUtil;

import java.util.logging.Logger
import java.util.zip.ZipEntry;

@CompileStatic
class AddedLocationDetector extends AddFilesToClassLoaderGroovy {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<File, List<JustStackTrace>> locationMap = [:]

    public List<File> addedFiles3 = []

    @Override
    void addFile(File file) throws Exception {
        file = file.getCanonicalFile().getAbsoluteFile();
        addedFiles3.add(file)
        List<JustStackTrace> locations = locationMap.get(file)
        if (locations == null) {
            locations = []
            locationMap.put(file, locations)
        }
        locations.add(new JustStackTrace())
    }

    List<File> getAddedFiles4(){
        return addedFiles3.unique()
    }

    @Override
    void addFileImpl(File file) throws Exception {
        throw new Exception('Should not be used')
    }

    Map<File, List<JustStackTrace>> findDuplicates() {
        return locationMap.findAll { it.value.size() > 1 }
    }

    void printDuplicates() {
        Map<File, List<JustStackTrace>> duplicates = findDuplicates()
    }



}
