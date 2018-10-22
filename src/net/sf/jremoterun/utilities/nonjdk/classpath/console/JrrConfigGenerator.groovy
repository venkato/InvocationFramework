package net.sf.jremoterun.utilities.nonjdk.classpath.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.groovystarter.JrrStarterConstatnts
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorSup2Groovy
import org.apache.commons.io.IOUtils

import java.util.logging.Logger

@CompileStatic
class JrrConfigGenerator implements ClassNameSynonym{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String configText

    void generateClassPath(String filePrefix) {
        File f = "${filePrefix}.groovy" as File
        ClassPathCalculatorSup2Groovy calc = new ClassPathCalculatorSup2Groovy()
        f.text = calc.saveClassPath7([])
        log.info "file created : ${f.absolutePath}"
    }

    static  String readText(String fileSuffix) {
        String resourceName = 'templates/'+fileSuffix
        InputStream inputStream = JrrClassUtils.currentClassLoader.getResourceAsStream(resourceName)
        if (inputStream == null) {
            throw new IllegalStateException("failed find resource ${resourceName}")
        }
        byte[] bytes = IOUtils.toByteArray(inputStream)
        String s = new String(bytes)
        return s
    }

    static File getJrrConfigDir(){
        File userHome = new File(System.getProperty("user.home"));
        File jrrConfigDir = userHome.child(JrrStarterConstatnts.jrrConfigDir)
        return jrrConfigDir
    }

    void generateConfig(boolean raw, boolean userConfig, boolean overrideFile) {
        String s = configText
        if (s == null) {
            s = readText(JrrStarterConstatnts.rawConfigFileName)
        }
        File jrrConfigDir = getJrrConfigDir()
        String className3
        File f
        if (raw) {
            className3 = userConfig ? 'userConfigRaw' : 'dirConfigRaw'
            if (userConfig) {
                jrrConfigDir.mkdirs()
                f = jrrConfigDir.child(JrrStarterConstatnts.rawConfigFileName);
            } else {
                f = JrrStarterConstatnts.rawConfigFileName as File
            }
        } else {
            className3 = userConfig ? 'userConfig' : 'dirConfig'
            if (userConfig) {
                jrrConfigDir.mkdirs()
                f = jrrConfigDir.child(JrrStarterConstatnts.configFileName);
            } else {
                f = JrrStarterConstatnts.configFileName as File
            }
        }
        String fileContent = s.replace(JrrStarterConstatnts.rawConfigFileName, className3)
        if (f.exists() && !overrideFile) {
            throw new IOException("file exist : ${f.absolutePath}")
        }
        f.text = fileContent
        log.info "created file : ${f.absolutePath}"
    }



}
