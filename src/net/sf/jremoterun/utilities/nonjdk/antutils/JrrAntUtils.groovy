package net.sf.jremoterun.utilities.nonjdk.antutils

import net.sf.jremoterun.utilities.UrlCLassLoaderUtils

import java.util.logging.Logger


class JrrAntUtils {

    private static final Logger log = Logger.getLogger(JrrAntUtils.getName());


    public static AntBuilder ant = new AntBuilder()

    static void addClassesToZip(File zipFile, Class... clazz) {
        clazz.each {
            addClassToZip(zipFile,it);
        }
    }

    static void addClassToZip(File zipFile, Class clazz) {
        File location = UrlCLassLoaderUtils.getClassLocation(clazz)
        String include = clazz.name.replace('.', '/') + '*'
        ant.zip(destfile: zipFile, update: 'true', duplicate: 'fail', basedir: location.absolutePath, includes: include)
    }


    static void addClassToZip2(File zipFile, File baseDir, Class clazz) {
        String include = clazz.name.replace('.', '/') + '*'
        ant.zip(destfile: zipFile, update: 'true', duplicate: 'fail', basedir: baseDir, includes: include)
    }



    static void addPackageToZip(File zipFile, File baseDir, Class clazz) {
        String include = clazz.getPackage().name.replace('.', '/') + '/**/*'
        log.info "include = ${include}"
        ant.zip(destfile: zipFile, update: 'true', duplicate: 'fail', basedir: baseDir, includes: include)
    }

    /**
     * If include is null, then include all in thisDir
     */
    static void addDirToZip(File zipFile, File baseDir, String include, String exclude) {
        if (include == null) {
            include = "**/*"
        }
        if (exclude == null) {
            exclude = ""
        }
        ant.zip(destfile: zipFile, update: 'true', duplicate: 'fail', basedir: baseDir.absolutePath, includes: include, excludes: exclude)
    }


}



