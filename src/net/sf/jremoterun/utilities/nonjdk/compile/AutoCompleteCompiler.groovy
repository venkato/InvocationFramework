package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import org.apache.commons.io.FileUtils
import org.junit.Test
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class AutoCompleteCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static GitSpec gitSpec = new GitSpec('https://github.com/venkato/AutoComplete')

    GitRef gitRefSrc = new GitRef(gitSpec, 'src/main/java')
    GitRef gitRefResources = new GitRef(gitSpec, 'src/main/resources')


    CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler

//    GitRef gitRefDist = new GitRef('https://github.com/venkato/AutoComplete', 'dist/AutoComplete.jar')

    File autoComplDir

    public static List<MavenId> mavenIds = [
            LatestMavenIds.rsyntaxtextarea.m,
            LatestMavenIds.log4jOld.m,
    ]

    void prepare() {
        params.javaVersion = '1.6'
        client.adder.addGenericEnteries(mavenIds)
        client.adder.addFileWhereClassLocated(JrrUtilities)

    }

    void addDefaulSrc() {
        if (autoComplDir == null) {
            autoComplDir = handler.resolveToFile(gitSpec)
        }
        params.addInDir handler.resolveToFile(gitRefSrc)
        params.outputDir = new File(autoComplDir, 'build')
        params.outputDir.mkdirs()
    }

    File dist

    void zip() {
        FileUtils.copyDirectory(handler.resolveToFile(gitRefResources), params.outputDir)
        dist = new File(autoComplDir, 'dist/AutoComplete.jar')
        dist.parentFile.mkdir()
        dist.delete()
        assert !dist.exists()
        assert dist.parentFile.exists()
        ZipUtil.pack(params.outputDir, dist)
    }


    @Test
    void all() {
        prepare()
        addDefaulSrc()
        compile()
        zip()
    }


}
