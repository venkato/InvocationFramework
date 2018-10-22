package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.zeroturnaround.zip.ZipEntryCallback
import org.zeroturnaround.zip.ZipUtil;

import java.util.logging.Logger
import java.util.zip.ZipEntry;

@CompileStatic
class GetClassesFromLocation {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    Map<File,List<String>> loadClassesOnLocation(Collection<File> files) {
        Map<File,List<String>> classesFromFile = [:]
        files.each {
            classesFromFile.put(it,handleOneFile(it));
        }
        return classesFromFile;
    }

    List<String> handleOneFile(File file){
        if (file.isDirectory()) {
            return handleDir(file);
        }
        return handleZip(file)
    }



    List<String> handleZip(File zipFile) {
        List<String> result = []
        ZipEntryCallback zipEntryCallback = new ZipEntryCallback() {
            @Override
            void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
                String entryName = zipEntry.getName();
                if(entryName.endsWith('.class')) {
                    String className1 = entryName.replace('/', '.');
                    result.add(className1.substring(0,className1.length()-6))
                }
            }
        }
        ZipUtil.iterate(zipFile, zipEntryCallback)
        return result
    }

    List<String> handleDir(File dir) {
        List<String> result = []
        File[] files = dir.listFiles()
        files.toList().each {

            String fileName = it.getName()
            if (it.isFile()) {
                if (fileName.endsWith('.class')) {
                    String name1 = fileName.substring(0,fileName.length()-6)
                    result.add(name1)
                }
            }
            if (it.isDirectory()) {
                List<String> resTmp = handleDir(it);
                result.addAll(resTmp.collect { fileName + '.' + it })
            }
        }
        return result
    }

}
