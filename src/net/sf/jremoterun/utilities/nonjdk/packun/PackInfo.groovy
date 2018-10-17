package net.sf.jremoterun.utilities.nonjdk.packun

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean2;

import java.util.logging.Logger;

@CompileStatic
class PackInfo implements JavaBean2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String zipLocation
    File unzipLocation


    static PackInfo derivePackInfo(File baseDir, File childDir) {
        assert baseDir.isChildFile(childDir)
        String pathToParent = new MavenCommonUtils().getPathToParent(baseDir, childDir)
        if(!pathToParent.endsWith('.zip')){
            pathToParent +='.zip'
        }
        return new PackInfo(zipLocation: pathToParent, unzipLocation: childDir)

    }

    static List<PackInfo> derivePackInfos(File baseDir, File childDir,List<String> ignoreDirs) {
        List<File> list = childDir.listFiles().toList()
        list=list.findAll {it.isDirectory()}
        list=list.findAll {!ignoreDirs.contains(it.name)}

        List<PackInfo> result= list.collect {derivePackInfo(baseDir,it)}
        return result

    }


}
