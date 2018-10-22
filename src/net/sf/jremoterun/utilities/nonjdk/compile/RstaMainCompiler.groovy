package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.apache.commons.io.FileUtils
import org.junit.Test
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class RstaMainCompiler   {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler

    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    public static List mavenIds = [
            LatestMavenIds.rsyntaxtextarea,
            LatestMavenIds.log4jOld,
            LatestMavenIds.rstaui,
            LatestMavenIds.rsyntaxtextarea,
            LatestMavenIds.rhino,
//            new MavenId('org.mozilla:rhino:1.7.7.2'),
    ]



    void prepare() {
        compilerPure.javaVersion = '1.6'
        compilerPure.adder.addGenericEnteries(mavenIds)
        compilerPure.adder.addFileWhereClassLocated(JrrUtilities)

    }

    File repoBase;

    void addDefaulSrc() {
        if(repoBase==null) {
            repoBase = handler.resolveToFile(new GitSpec(  GitReferences.rsta.repo))
        }
        compilerPure.addInDir new File(repoBase,'src/main/java')
        compilerPure.adder.add GitReferences.rstaAutoCompetion
        compilerPure.outputDir = new File(repoBase,'bin')
        compilerPure.outputDir.mkdirs()
    }

    File dist;

    File zip() {
        FileUtilsJrr.copyDirectory(new File(repoBase,'src/main/resources'),compilerPure.outputDir)
        dist = new File(repoBase,'build/rsta.jar');
        dist.parentFile.mkdir()
        assert dist.parentFile.exists()
        dist.delete()
        assert !dist.exists()
        ZipUtil.pack(compilerPure.outputDir,dist)
        return dist
    }


    @Test
    void all() {
        prepare()
        addDefaulSrc()
        compilerPure.compile()
        zip()
    }


}
