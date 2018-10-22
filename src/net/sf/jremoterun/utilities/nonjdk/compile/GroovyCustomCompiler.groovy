package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.antutils.JrrAntUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.langi.JrrStaticCompilationVisitor
import net.sf.jremoterun.utilities.nonjdk.log.FileExtentionClass
import net.sf.jremoterun.utilities.nonjdk.log.JdkLoggerExtentionClass
import org.apache.commons.io.FileUtils
import org.junit.Test
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class GroovyCustomCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File baseDir


    void prepare() {
        if (baseDir == null) {
            baseDir = client.ifDir
        }
        params.javaVersion = '1.6'
        params.addInDir new File(baseDir, "groovycustom/src")
        params.addInDir new File(baseDir, IfFrameworkSrcDirs.src_logger_ext_methods.dirName)
        params.outputDir = new File(baseDir, "build/groovycustom1")
    }

    File dest

    File zip() {
        FileUtilsJrr.copyDirectory(new File(baseDir, "resources-groovy"), params.outputDir)
        dest = new File(baseDir, "build/groovy_custom.jar")
        dest.delete()
        ZipUtil.pack(params.outputDir, dest)
        return dest
    }


    @Test
    @Override
    void all2() {
        super.all2()
        zip()
    }


    void updateCompilerDefaultDir() {
        File child = GitSomeRefs.starter.childL('libs/origin/groovy_custom.jar').resolveToFile()
        assert child.exists()
        updateCompiler(child)
        FileUtilsJrr.copyFile(child, JrrStarterJarRefs2.groovy_custom.resolveToFile())
    }

    void updateCompiler(File compilerJar) {

        List<Class> classes3 = (List) [JdkLoggerExtentionClass, FileExtentionClass, CompileStatic,]
        classes3.each {
            JrrAntUtils.addClassToZip2(compilerJar, params.outputDir, it)
        }
        log.info "updating package : ${JrrStaticCompilationVisitor.package.name}"
        JrrAntUtils.addPackageToZip(compilerJar, params.outputDir, JrrStaticCompilationVisitor)

    }
}
