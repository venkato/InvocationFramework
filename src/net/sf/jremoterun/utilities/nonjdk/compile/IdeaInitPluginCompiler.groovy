package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class IdeaInitPluginCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    IdeaInitPluginCompiler() {
    }

    @Override
    void prepare() {

    }

    void compile3(File ideaDir) {
        prepare(ideaDir)
        compile()
        postCompileStep()
    }

    @Override
    void compile() {
        log.info "compiling ..."
        super.compile()
    }

    static File getJrrUtilsJar() {
        JrrUtilsCompiler compiler = new JrrUtilsCompiler()
        compiler.all2()
        compiler.zipp()
        if (GroovyMethodRunnerParams.instance.grHome != null) {
            File child = GroovyMethodRunnerParams.instance.grHome.child("onejar/jrrutilities.jar")
            if (child.exists()) {
                return child
            }
            log.info "file not found : ${child}"
        }
        File f =  new GitRef(GitReferences.starter, "onejar/jrrutilities.jar").resolveToFile()
        return f
    }

    void prepare(File ideaDir) {
        assert ideaDir.exists()
        client.adder.addAllJarsInDir new File(ideaDir, "lib/")
        client.adder.addAllJarsInDir new File(ideaDir, 'plugins/Groovy/lib/');
        params.outputDir = new File(client.ifDir, 'build/ideainitpluginbuild')
        params.outputDir.mkdirs()
        params.javaVersion = '1.6'
        File dir = new File(client.ifDir, 'src-idea/net/sf/jremoterun/utilities/nonjdk/idea/init')
        assert dir.exists()
        params.files.addAll(dir.listFiles().toList())


        client.adder.add getJrrUtilsJar()
        client.adder.addFileWhereClassLocated(JrrUtilities)
        client.adder.addFileWhereClassLocated(JrrClassUtils)
        client.adder.addFileWhereClassLocated(AddFilesToClassLoaderGroovy)
        client.adder.addFileWhereClassLocated(DropshipClasspath)

    }

    void testUpdateIdeaJar(File tmpJar, File metaInf, File targetJar) {
        assert targetJar.parentFile.exists()
        testUpdateIdeaJar2(tmpJar, metaInf)
        assert targetJar.parentFile.exists()
        FileUtils.copyFile(tmpJar, targetJar)
        assert DigestUtils.sha256(tmpJar.bytes) == DigestUtils.sha256(targetJar.bytes)
        tmpJar.delete()
        log.info "file updated : ${targetJar}"
    }

    void testUpdateIdeaJar2(File tmpJar, File metaInf) {
//        JdkLogFormatter.setLogFormatter()
        tmpJar.delete()
        assert !tmpJar.exists()

        assert metaInf.directory
        FileUtils.copyDirectoryToDirectory(metaInf, params.outputDir)
        ZipUtil.pack(params.outputDir, tmpJar)


    }

}

