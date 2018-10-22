package net.sf.jremoterun.utilities.nonjdk.decompiler;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import org.apache.commons.lang3.SystemUtils
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler
import org.jetbrains.java.decompiler.struct.StructContext;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class DecompilerAddFiles extends AddFilesToClassLoaderGroovy{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    StructContext structContext

    DecompilerAddFiles(StructContext structContext) {
        this.structContext = structContext
    }

    @Override
    void addFileImpl(File file) throws Exception {
        structContext.addSpace(file,false)
    }
    
    
    void addRtJar(){
        File javaHome = SystemUtils.getJavaHome();
        assert javaHome.exists();
        File rtJar = javaHome.child('lib/rt.jar');
        add( rtJar);
    }

    void addJfrJarsIfExists(){
        File javaHome = SystemUtils.getJavaHome();
        assert javaHome.exists();
        File jfrJar = javaHome.child('lib/jfr.jar');
        if(jfrJar.exists()) {
            add(jfrJar);
        }
    }

    void addToolsJar(){
        File file = new MavenCommonUtils().getToolsJarFile()
        if(file!=null){
            add file
        }
    }


    void addAllJdkJars(){
        addRtJar()
        addJfrJarsIfExists()
        addToolsJar()
    }

    @Override
    void addFilesFromGmrp() {
        GroovyMethodRunnerParams gmrp = GroovyMethodRunnerParams.gmrp
        if (gmrp == null) {
            throw new IllegalStateException("gmrp not inited")
        }
        gmrp.addFilesToClassLoader.addedFiles2.each {
            try {
                add it
            }catch (Throwable e){
                log.warn( "failed add : ${it} ",e)
            }
        }

    }
}
