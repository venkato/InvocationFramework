package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs

import java.util.logging.Logger

@CompileStatic
class DockingFramesCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List mavenIds = [
    ]

    File baseDir2

    DockingFramesCompiler() {
    }

    void prepare() {
        params.printWarning = false
        params.javaVersion = '1.8'
        if (baseDir2 == null) {
            baseDir2 = GitSomeRefs.dockingFrames.resolveToFile()
        }
        log.info "${baseDir2}"
        List<File> dirs = findSrc(baseDir2)
        log.info "dirs : ${dirs}"
        dirs.each {
            addInDir(it)
        }

        client.adder.add new MavenCommonUtils().getToolsJarFile()

        params.outputDir = new File(this.baseDir2, 'target/1')
        params.outputDir.mkdirs()
//        client.adder.addAll mavenIds

    }


    static List<File> findSrc(File baseDir3) {
        List<File> dirs = baseDir3.listFiles().toList().findAll {
            it.directory && it.name != 'docking-frames-ext-glass'
        }.collect { it.child('src') }.findAll {
            it.exists()
        }
        return dirs
    }


}
