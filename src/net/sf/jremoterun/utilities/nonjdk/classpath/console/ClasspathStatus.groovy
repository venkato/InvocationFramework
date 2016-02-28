package net.sf.jremoterun.utilities.nonjdk.classpath.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGroovyWithDownloadWise
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorSup2Groovy
import net.sf.jremoterun.utilities.nonjdk.classpath.UrlCLassLoaderUtils2
import net.sf.jremoterun.utilities.nonjdk.store.ListStore

import java.util.logging.Logger

@CompileStatic
class ClasspathStatus implements ClassNameSynonym{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static URLClassLoader thisClassLoader = JrrClassUtils.currentClassLoaderUrl

    static void dumpClassPathFiles1() {
        log.info "class path :\n ${GroovyMethodRunnerParams.gmrp.addFilesToClassLoader.addedFiles2}"
    }

    static void dumpClassPathWise1() {
        ClassPathCalculatorSup2Groovy classPathCalculatorGroovy = new ClassPathCalculatorSup2Groovy();
        classPathCalculatorGroovy.filesAndMavenIds.addAll(GroovyMethodRunnerParams.gmrp.addFilesToClassLoader.addedFiles2)
        classPathCalculatorGroovy.calcClassPathFromFiles12()
        log.info "class path :\n ${classPathCalculatorGroovy.filesAndMavenIds}"
    }


    static void dumpClassPathFiles2() {
        List<File> files = UrlCLassLoaderUtils.getFilesFromUrlClassloader(thisClassLoader)
        log.info "class path :\n ${files}"
    }


    static void dumpClassPathFilesWise2() {
        List<File> files = UrlCLassLoaderUtils.getFilesFromUrlClassloader(thisClassLoader)
        ClassPathCalculatorSup2Groovy classPathCalculatorGroovy = new ClassPathCalculatorSup2Groovy();
        classPathCalculatorGroovy.filesAndMavenIds.addAll(files)
        classPathCalculatorGroovy.calcClassPathFromFiles12()
        log.info "class path :\n ${classPathCalculatorGroovy.filesAndMavenIds}"
    }

    static void dumpClassPathFilesWiseOnlyDir() {
        List<File> files = UrlCLassLoaderUtils.getFilesFromUrlClassloader(thisClassLoader)
        ClassPathCalculatorSup2Groovy classPathCalculatorGroovy = new ClassPathCalculatorSup2Groovy();
        classPathCalculatorGroovy.filesAndMavenIds.addAll(files)
        classPathCalculatorGroovy.calcClassPathFromFiles12()
        List<File> all = classPathCalculatorGroovy.filesAndMavenIds.findAll { (it instanceof File) } as List
        all = all.findAll { it.isDirectory() }
        log.info "class path :\n ${all}"
    }

    static void dumpClassPathFilesWiseOnlyMavenIds(ClassPathCalculatorGroovyWithDownloadWise classPathCalculatorGroovy, File saveFile) {
        IvyDepResolver2.setDepResolver()
        classPathCalculatorGroovy.calcClassPathFromFiles12()
        List<MavenId> filesAndMavenIds = classPathCalculatorGroovy.filesAndMavenIds.findAll {
            (it instanceof MavenId)
        } as List
        ListStore<MavenId> idListStore = new ListStore<>(saveFile)
        idListStore.saveToFile(filesAndMavenIds)
    }

    static void dumpClassPathFilesWiseOnlyCustom() {
        List<File> files = UrlCLassLoaderUtils.getFilesFromUrlClassloader(thisClassLoader)
        ClassPathCalculatorSup2Groovy classPathCalculatorGroovy = new ClassPathCalculatorSup2Groovy();
        classPathCalculatorGroovy.filesAndMavenIds.addAll(files)
        classPathCalculatorGroovy.calcClassPathFromFiles12()
        List all = classPathCalculatorGroovy.filesAndMavenIds.findAll { !(it instanceof MavenId) }
        log.info "class path :\n ${all}"
    }


    static void dumpClassLocation(ClRef className) {
        File location = UrlCLassLoaderUtils.getClassLocation(className.loadClass(thisClassLoader))
        log.info "location : ${location}"
    }


    static void dumpClassLocationWise(ClRef className) {
        File location = UrlCLassLoaderUtils.getClassLocation(className.loadClass(thisClassLoader))
//        log.info "${location}"
        assert location.exists()
        ClassPathCalculatorSup2Groovy classPathCalculatorGroovy = new ClassPathCalculatorSup2Groovy();
        classPathCalculatorGroovy.filesAndMavenIds.add(location)
        classPathCalculatorGroovy.calcClassPathFromFiles12()
        log.info "location :\n ${classPathCalculatorGroovy.filesAndMavenIds.first()}"

    }


    static void dumpClassLocationsWise(ClRef className) {
        List<URL> location1 = thisClassLoader.getResources(UrlCLassLoaderUtils.buildClassNameSuffix(className.className)).toList()
        List<URL> location2 = thisClassLoader.getResources(UrlCLassLoaderUtils.buildClassNameSuffixGroovy(className.className)).toList()
        log.info "groovy files : ${location2}"
        List<File> collect = []
        collect.addAll(location1.collect {
            String urlAsString = it.toString()
            if (urlAsString.startsWith('jar:')) {
                return UrlCLassLoaderUtils2.convertFullPathToRootDotClass(it, className.className)
            }
            return UrlCLassLoaderUtils.convertClassLocationToPathToJar2(it)
        }
        )
        collect.addAll(location2.collect {
            String urlAsString = it.toString()
            if (urlAsString.startsWith('jar:')) {
                return UrlCLassLoaderUtils2.convertFullPathToRootDotGroovy(it, className.className)
            }
            return UrlCLassLoaderUtils.convertClassLocationToPathToJar2(it)
        }
        )
        log.info "${collect}"
        ClassPathCalculatorSup2Groovy classPathCalculatorGroovy = new ClassPathCalculatorSup2Groovy();
        classPathCalculatorGroovy.filesAndMavenIds.addAll(collect)
        classPathCalculatorGroovy.calcClassPathFromFiles12()
        log.info "locations :\n ${classPathCalculatorGroovy.filesAndMavenIds.first()}"

    }


}
