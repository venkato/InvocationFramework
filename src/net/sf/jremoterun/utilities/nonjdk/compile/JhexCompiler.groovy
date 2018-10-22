package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JavaVMClient
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure

import java.util.logging.Logger

@CompileStatic
class JhexCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List mavenIds = [
            LatestMavenIds.guavaMavenId,
    ]

    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();


    void prepare() {
//        params.printWarning = false
        compilerPure.outputDir = GitSomeRefs.jhexViewer.childL('build/b1').resolveToFile()
        compilerPure.outputDir.mkdirs()

//        addInDir GitReferences.jhexViewer
        compilerPure.addInDir GitSomeRefs.jhexViewer.childL('src/main/java/com/google/security/zynamics/zylib/gui/JHexPanel/')
        compilerPure.addInDir GitSomeRefs.jhexViewer.childL('src/main/java/com/google/security/zynamics/zylib/gui/JCaret/')
        compilerPure.addInDir GitSomeRefs.jhexViewer.childL('src/main/java/com/google/security/zynamics/zylib/general/Convert.java')
        compilerPure.addInDir GitSomeRefs.jhexViewer.childL('src/main/java/com/google/security/zynamics/zylib/gui/GuiHelper.java')

        compilerPure.adder.addAll mavenIds
        compilerPure.adder.addFileWhereClassLocated(JavaVMClient)
        compilerPure.adder.addFileWhereClassLocated(JdkLogFormatter)
        compilerPure.javaVersion = '1.7'
    }


}
