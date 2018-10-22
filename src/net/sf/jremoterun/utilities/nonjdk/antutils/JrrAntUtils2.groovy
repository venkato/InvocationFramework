package net.sf.jremoterun.utilities.nonjdk.antutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import org.apache.tools.ant.taskdefs.Zip
import org.apache.tools.ant.types.ZipFileSet

import java.util.logging.Logger


@CompileStatic
class JrrAntUtils2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static Zip createZipTask() {
        AntBuilder antBuilder = new AntBuilder()
        Zip zip = new Zip()
        zip.setProject(antBuilder.getAntProject())
        return zip;
    }

    static List<ZipFileSet> addClassesToZip(Class... clazz) {
        List<ZipFileSet> res = []
        for (Class cl : clazz) {
            addClassToZip(cl);
        }
        return res
    }

    static String convertClassName(Class clazz) {
        return clazz.name.replace('.', '/') + '*'
    }

    static String convertPackageName(Class clazz) {
        return clazz.getPackage().getName().replace('.', '/') + '/**/*'
    }


    static ZipFileSet addClassToZip(Class clazz) {
        ZipFileSet zipFileSet = new ZipFileSet()
        zipFileSet.setDir UrlCLassLoaderUtils.getClassLocation(clazz)
        zipFileSet.setIncludes convertClassName(clazz)
        return zipFileSet
    }


}



