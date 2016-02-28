package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import org.apache.commons.io.FileUtils
import org.junit.Test
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class RstaMainCompiler  extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler

    public static List mavenIds = [
            LatestMavenIds.rsyntaxtextarea,
            LatestMavenIds.log4jOld,
            LatestMavenIds.rstaui,
            LatestMavenIds.rsyntaxtextarea,
            LatestMavenIds.rhino,
//            new MavenId('org.mozilla:rhino:1.7.7.2'),
    ]



    void prepare() {
        params.javaVersion = '1.6'
        client.adder.addGenericEnteries(mavenIds)
        client.adder.addFileWhereClassLocated(JrrUtilities)

    }

    File repoBase;

    void addDefaulSrc() {
        if(repoBase==null) {
            repoBase = handler.resolveToFile(new GitSpec(  GitReferences.rsta.repo))
        }
        params.addInDir new File(repoBase,'src/main/java')
        client.adder.add GitReferences.rstaAutoCompetion
        params.outputDir = new File(repoBase,'bin')
        params.outputDir.mkdirs()
    }

    void compile() {
        client.compile(params)
    }

    File dist;

    File zip() {
        FileUtils.copyDirectory(new File(repoBase,'src/main/resources'),params.outputDir)
        dist = new File(repoBase,'build/rsta.jar');
        dist.parentFile.mkdir()
        assert dist.parentFile.exists()
        dist.delete()
        assert !dist.exists()
        ZipUtil.pack(params.outputDir,dist)
        return dist
    }


    @Test
    void all() {
        prepare()
        addDefaulSrc()
        compile()
        zip()
    }


}
