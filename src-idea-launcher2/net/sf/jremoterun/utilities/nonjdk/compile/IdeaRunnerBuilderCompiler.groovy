package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.antutils.JrrAntUtils
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBuilderAddGroovyRuntime
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.jetbrains.jps.cmdline.Launcher
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class IdeaRunnerBuilderCompiler  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    public static List mavenIds = [
            DropshipClasspath.groovy
    ]


    public File baseDir

    void prepare() {
        if(baseDir==null){
            baseDir = InfocationFrameworkStructure.ifDir;
        }
        assert baseDir!=null
        compilerPure.javaVersion = '1.8'

        compilerPure.addInDir IfFrameworkSrcDirs.src_idea_launcher
//        params.addInDir new File(baseDir,'src-idea-launcher')
        compilerPure.adder.addAll mavenIds
        compilerPure.outputDir = new File(baseDir, 'build/idearunner')
    }




    File createCustomJar() {
        File fileJar = new File(baseDir, 'build/jps-launcher.jar')
        fileJar.delete();
        assert !fileJar.exists()
        ZipUtil.pack(compilerPure.outputDir, fileJar)
        return fileJar;
    }

    void updateCompiler(File compilerJar) {
        List<Class> classes3 = (List) [Launcher,]
        classes3.each {
            JrrAntUtils.addClassToZip2(compilerJar, compilerPure.outputDir, it)
        }
        JrrAntUtils.addPackageToZip(compilerJar, compilerPure.outputDir, IdeaBuilderAddGroovyRuntime)
    }

    static File getJarFile(File ideaPath){
        assert ideaPath.exists()
        File f = ideaPath.child('plugins/java/lib/jps-launcher.jar');
        assert f.exists()
        return f;
    }


}
