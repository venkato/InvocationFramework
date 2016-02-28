package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class JrrUtilsCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List mavenIds = [
            LatestMavenIds.log4jOld,
    ]

    File baseDir

    void prepare() {
        params.javaVersion = '1.6'
        client.adderParent.addAll DropshipClasspath.allLibsWithGroovy
        client.adder.addGenericEnteries(mavenIds)
        client.adder.addFileWhereClassLocated(JrrUtilities)
        client.adder.addFileWhereClassLocated(JrrClassUtils)
//        client.adder.addMavenPath DropshipClasspath.sisiFileBin
        addDefaulSrc()
    }

    @Override
    void compile() {
        log.info "compiling"
        super.compile()
    }

    void addDefaulSrc() {
        if(baseDir==null){
            baseDir= GroovyMethodRunnerParams.gmrp.grHome
        }
        addInDir new File(baseDir, 'JrrUtilities/src')
        addInDir new File(baseDir, 'JrrStarter/src')
        assert params.dirs.each { it.exists() }
        params.outputDir = new File(baseDir, 'JrrUtilities/build/out2')
        params.outputDir.mkdirs()
    }


    File zipp() {
        File destJar = new File(baseDir, 'onejar/jrrutilities.jar')
        if(!destJar.parentFile.exists()){
            assert destJar.parentFile.mkdir()
        }
        ZipUtil.pack(params.outputDir, destJar)
        return destJar
    }


}