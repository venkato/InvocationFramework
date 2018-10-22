package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.apache.commons.io.FileUtils
import org.junit.Test
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class AutoCompleteCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static GitSpec gitSpec = new GitSpec('https://github.com/venkato/AutoComplete')

    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();
    FileChildLazyRef gitRefResources = GitSomeRefs.rstaAutoCompetionVenkato.childL(  'src/main/resources')


    CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler

//    GitRef gitRefDist = new GitRef('https://github.com/venkato/AutoComplete', 'dist/AutoComplete.jar')

    File autoComplDir

    public static List<MavenId> mavenIds = [
            LatestMavenIds.rsyntaxtextarea.m,
            LatestMavenIds.log4jOld.m,
    ]

    void prepare() {
        compilerPure.javaVersion = '1.6'
        compilerPure.adder.addGenericEnteries(mavenIds)
        compilerPure.adder.addFileWhereClassLocated(JrrUtilities)

    }

    void addDefaulSrc() {
        if (autoComplDir == null) {
            autoComplDir = GitSomeRefs.rstaAutoCompetionVenkato.resolveToFile()
        }
        compilerPure.addInDir GitSomeRefs.rstaAutoCompetionVenkato.childL( 'src/main/java')
        compilerPure.outputDir = new File(autoComplDir, 'build')
        compilerPure.outputDir.mkdirs()
    }

    File dist

    void zip() {
        FileUtilsJrr.copyDirectory(handler.resolveToFile(gitRefResources), compilerPure.outputDir)
        dist = new File(autoComplDir, 'dist/AutoComplete.jar')
        dist.parentFile.mkdir()
        dist.delete()
        assert !dist.exists()
        assert dist.parentFile.exists()
        ZipUtil.pack(compilerPure.outputDir, dist)
    }


    @Test
    void all() {
        prepare()
        addDefaulSrc()
        compilerPure.compile()
        zip()
    }


}
