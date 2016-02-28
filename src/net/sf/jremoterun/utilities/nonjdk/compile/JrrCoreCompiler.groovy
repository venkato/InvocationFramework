package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class JrrCoreCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List mavenIds = [
            LatestMavenIds.swtWin,
            LatestMavenIds.eclipseWorkbench,
    ]

    File baseDir

    void prepare() {
        params.javaVersion = '1.5'
        client.adder.addAll mavenIds
        params.addInDir new File(baseDir, 'src')
        params.outputDir = new File(baseDir, 'build/classes')
        params.outputDir.mkdirs()
    }


    File zipp() {
        FileUtils.copyDirectoryToDirectory(new File(baseDir,"src/META-INF"),params.outputDir);
        File destJar = new File(baseDir, 'build/jremoterun.jar');
        assert destJar.parentFile.exists()
        ZipUtil.pack(params.outputDir, destJar)
        return destJar
    }


}
