package net.sf.jremoterun.utilities.nonjdk.compile;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.classpath.ToFileRefSelf
import net.sf.jremoterun.utilities.nonjdk.compiler3.CompileRequestClient
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompilerParams
import org.apache.commons.io.FileUtils;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
abstract class GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public MavenCommonUtils mcu = new MavenCommonUtils()

    public GroovyCompilerParams params = new GroovyCompilerParams()

    public CompileRequestClient client = new CompileRequestClient()

    abstract void prepare();

    void compile() {
        client.compile(params)
    }


    void all2(){
        prepare()
        compile()
        postCompileStep()
    }

    void postCompileStep(){

    }

    static void copyMavenIdToDir(MavenIdContains mavenId, File toDir){
        File fileMavenId = new MavenCommonUtils().findMavenOrGradle(mavenId.m)
        assert fileMavenId!=null
        FileUtils.copyFileToDirectory(fileMavenId, toDir)
    }

    void addInDir(File... files){
        params.addInDir(files)
    }

    void addInDir(ToFileRefSelf toFileRef){
        params.addInDir toFileRef.resolveToFile()
    }

    void addInDir(ToFileRef2 toFileRef){
        params.addInDir toFileRef.resolveToFile()
    }

}
