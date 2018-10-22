package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.OsInegrationClientI
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorWithAdder
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import org.apache.commons.lang3.SystemUtils
import org.fife.rsta.ac.java.JarManager
import org.fife.rsta.ac.java.buildpath.LibraryInfo
import org.fife.ui.autocomplete.Completion
import org.fife.ui.autocomplete.CompletionProvider

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
public class RstaLangSupportStatic {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static RstaLangSupportStatic langSupport = new RstaLangSupportStatic();

//    public

    public static File toolsJarFile;

    // no need cache JarManger as this field has it
    public AddFileSourceToRsta addFileSourceToRsta;

    public OsInegrationClientI osInegrationClient

    public static boolean addJfrIfExistS = true

    public ClassPathCalculatorWithAdder classPathCalculatorGroovy = new ClassPathCalculatorWithAdder();


    JarManager createJarManager(){
        return new LogImprovedJarManager()
    }

    public void init() throws Exception {
        if (addFileSourceToRsta != null) {
            return;
        }
        JarManager jarManager = createJarManager()
        try {
            LibraryInfo info = LibraryInfo.getMainJreJarInfo();
            if(info ==null){
                log.severe "failed find main rt.jar"
            }else {
                log.info "${info}"
                boolean added = jarManager.addClassFileSource(info);
                log.info "jdk source added : ${added}"
//            jarManager.addCurrentJreClassFileSource();
            }
        } catch (IOException ioe) {
            log.log(Level.INFO, "Can't add jdk sources", ioe);
        }
        addFileSourceToRsta = new AddFileSourceToRsta(jarManager);
//        JrrClassUtils.setFieldValue(groovyLanguageSupport, "jarManager", addFileSourceToRsta.jarManager);
        defaultClassPathInit();
        // groovyLanguageSupport.getJarManager();

    }

    void addJfrJarIfExist(){
        File javaHome = SystemUtils.getJavaHome();
        assert javaHome.exists();
        File jfrJar = javaHome.child('lib/jfr.jar');
        if(jfrJar.exists()) {
            classPathCalculatorGroovy.filesAndMavenIds.add(jfrJar)
        }
    }

    void addEntryInFly(Object obj){
        GroovyMethodRunnerParams.gmrp.addFilesToClassLoader.add(obj)
        addFileSourceToRsta.add(obj)
    }

    void defaultClassPathInit() throws Exception {
        addClassesFromUrlClassLoader();
        classPathCalculatorGroovy.calcAndAddClassesToAdded(addFileSourceToRsta);
        log.info("jars added : " + addFileSourceToRsta.addedFiles2.size());

    }

    void addClassesFromUrlClassLoader() {
        if (GroovyMethodRunnerParams.gmrp != null) {
            AddFilesToUrlClassLoaderGroovy addFilesToClassLoaderFromGmrp = GroovyMethodRunnerParams.gmrp.addFilesToClassLoader
            if (addFilesToClassLoaderFromGmrp != null) {
                addFilesToClassLoaderFromGmrp.addedGroovyClassPathFiles.each {
                    classPathCalculatorGroovy.addFilesToClassLoaderGroovySave.addFromGroovyFile(it);
                }
                classPathCalculatorGroovy.filesAndMavenIds.addAll(addFilesToClassLoaderFromGmrp.addedFiles2)
            }
        }
        ClassLoader currentClassLoader = JrrClassUtils.getCurrentClassLoader();
        if (currentClassLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) currentClassLoader;
            classPathCalculatorGroovy.addFilesToClassLoaderGroovySave.addClassPathFromURLClassLoader(urlClassLoader);
        } else {
            log.info("non url classloader");
        }
        MavenCommonUtils mcu = new MavenCommonUtils()
        File javahome = System.getProperty('java.home') as File
        assert javahome.exists()
        classPathCalculatorGroovy.filesAndMavenIds = classPathCalculatorGroovy.filesAndMavenIds.collect {
            if (it instanceof File) {
                File f = (File) it;
                if (mcu.isParent(javahome, f)) {
                    return null
                }
            }
            return it;
        }
        if (toolsJarFile == null) {
            toolsJarFile = mcu.getToolsJarFile();
            if (toolsJarFile != null && toolsJarFile.exists()) {
                classPathCalculatorGroovy.filesAndMavenIds.add toolsJarFile
            } else {
                log.info "failed find tools.jar : ${toolsJarFile}"
            }
        } else {
            classPathCalculatorGroovy.filesAndMavenIds.add toolsJarFile
        }
        if(addJfrIfExistS){
            addJfrJarIfExist()
        }

        classPathCalculatorGroovy.filesAndMavenIds.add DropshipClasspath.groovy

    }


}
