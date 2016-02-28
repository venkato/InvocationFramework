package net.sf.jremoterun.utilities.nonjdk.compile;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import org.junit.Test

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@Deprecated
@CompileStatic
class RDesktopCompiler  extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List mavenIds = [
            LatestMavenIds.log4jOld,LatestMavenIds.gnuGetOpt,
    ]

    static ClRef cnr = new ClRef('net.propero.rdp.Rdesktop')

    static void run(List<String> args){
        Class cll = cnr.loadClass(JrrClassUtils.currentClassLoader)
        JrrClassUtils.runMainMethod(cll,args.toArray(new String[0]))
    }


    File baseDir

    void prepare() {
        assert baseDir!=null
        params.javaVersion = '1.6'
        params.addInDir new File(baseDir,'src')
        params.addInDir new File(baseDir,'src1.4')
        params.outputDir = new File(baseDir,'build')
        params.additionalFlags.add '-encoding'
        params.additionalFlags.add 'ISO-8859-1'

        client.adder.addAll mavenIds
//        client.adder.addAllJarsInDir new File(baseDir,'lib')
    }

    BinaryWithSource compileAndBuild(){
        baseDir = RstaCoreCompiler.handler.cloneGitRepo3.cloneGitRepo3(GitReferences.rdesktop)
        prepare()
        compile()
        BinaryWithSource binaryWithSource = new BinaryWithSource(params.outputDir,new File(baseDir,'src'))
        return binaryWithSource
    }

    @Test
    @Override
    void all2() {
        super.all2()
    }

    File  zipp(){
        return null
    }


}
