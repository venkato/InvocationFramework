package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class JrrCoreCompiler  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    public static List mavenIds = [
            LatestMavenIds.swtWin,
            LatestMavenIds.eclipseWorkbench,
    ]

    File baseDir

    void prepare() {
        compilerPure.javaVersion = '1.5'
        compilerPure.adder.addAll mavenIds
        compilerPure.addInDir new File(baseDir, 'src')
        compilerPure.outputDir = new File(baseDir, 'build/classes2')
        compilerPure.outputDir.mkdirs()
    }


    File zipp() {
        FileUtilsJrr.copyDirectoryToDirectory(new File(baseDir,"src/META-INF"),compilerPure.outputDir);
        File destJar = new File(baseDir, 'build/jremoterun_try.jar');

        assert destJar.parentFile.exists()
        ZipUtil.pack(compilerPure.outputDir, destJar)
        File dest2Jar = new File(baseDir, 'build/jremoterun.jar');
        FileUtilsJrr.copyFile(destJar,dest2Jar)
        return dest2Jar
    }


}
