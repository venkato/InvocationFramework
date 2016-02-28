package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.antutils.JrrAntUtils
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBuilderAddGroovyRuntime
import org.jetbrains.jps.cmdline.Launcher
import org.junit.Test

import java.util.logging.Logger

@CompileStatic
class IdeaRunnerBuilderCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List mavenIds = [

    ]


    File baseDir

    void prepare() {
        if(baseDir==null){
            baseDir = client.ifDir
        }
        assert baseDir!=null
        params.javaVersion = '1.8'
        params.addInDir new File(baseDir,'src-idea-launcher')
//        client.adder.addAll mavenIds
        params.outputDir = new File(baseDir, 'build/idearunner')
    }


    @Test
    @Override
    void all2() {
        super.all2()
    }


    void updateCompiler(File compilerJar) {
        List<Class> classes3 = (List) [Launcher,]
        classes3.each {
            JrrAntUtils.addClassToZip2(compilerJar, params.outputDir, it)
        }
        JrrAntUtils.addPackageToZip(compilerJar, params.outputDir, IdeaBuilderAddGroovyRuntime)
    }

    File  zipp(){
        return null
    }


}
