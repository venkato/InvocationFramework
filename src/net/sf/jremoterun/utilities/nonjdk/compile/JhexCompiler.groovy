package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JavaVMClient
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure

import java.util.logging.Logger

@CompileStatic
class JhexCompiler  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List mavenIds =[
            LatestMavenIds.guavaMavenId,
    ]

    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();


    void prepare() {
//        params.printWarning = false
        compilerPure.outputDir = GitReferences.jhexViewer.specOnly.resolveToFile().child('build/b1')
        compilerPure.outputDir.mkdirs()

//        addInDir GitReferences.jhexViewer
        File baseDir = GitReferences.jhexViewer.resolveToFile()
        File subDir = baseDir.child('src/main/java/com/google/security/zynamics/zylib')
        compilerPure. addInDir subDir.child('gui/JHexPanel/')
//        addInDir subDir.child('general/')
        compilerPure.addInDir subDir.child('gui/JCaret/')
        compilerPure.addInDir subDir.child('general/Convert.java')
        compilerPure.addInDir subDir.child('gui/GuiHelper.java')

        compilerPure.adder.addAll mavenIds
        compilerPure.adder.addFileWhereClassLocated(JavaVMClient)
        compilerPure.adder.addFileWhereClassLocated(JdkLogFormatter)
        compilerPure.javaVersion = '1.7'
    }



}
