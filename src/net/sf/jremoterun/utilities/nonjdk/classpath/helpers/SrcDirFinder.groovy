package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class SrcDirFinder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    /**
     * Find all dirs with pattern : 'child/childDirs/'
     */
    static List<File> findSourcesSimple(File baseDir, String childDirs) {
        assert baseDir.exists()
        assert baseDir.isDirectory()
        //childDirs = 'src/main/java'
        List<File> files = baseDir.listFiles().toList()
        files = files.findAll { it.isDirectory() }
        files = files.collect { it.child(childDirs) }
        files = files.findAll { it.exists() }
        if (files.size() == 0) {
            throw new Exception("No source found in ${baseDir}, child dirs = ${childDirs}")
        }
        return files;
    }


    /**
     * Find all dirs which have classes or files inside
     */
    static List<File> findSourcesByPackageWithCheck(File baseDir, String samplePackage, int depth) {
        List<File> result = findSourcesByPackage(baseDir, samplePackage, depth)
        if (result.size() == 0) {
            throw new Exception("No source found in ${baseDir}, depth = ${depth}, sample : ${samplePackage} ")
        }
    }

    static List<File> findSourcesByPackage(File baseDir, String samplePackage, int depth) {
        assert baseDir.exists()
        assert baseDir.isDirectory()
        //childDirs = 'src/main/java'
        List<File> files = baseDir.listFiles().toList()
        List<File> allDirs = files.findAll { it.isDirectory() }
        List<File> matched = allDirs.findAll { it.child(samplePackage).exists() }
        if (depth > 0) {
            int newDepth = depth - 1
            allDirs.each {
                matched.addAll(findSourcesByPackage(it, samplePackage, newDepth))
            }
        }
        return matched;
    }


}
