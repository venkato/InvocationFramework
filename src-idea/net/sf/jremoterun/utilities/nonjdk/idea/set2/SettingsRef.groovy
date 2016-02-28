package net.sf.jremoterun.utilities.nonjdk.idea.set2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.nonjdk.classpath.sl.GroovySettingsLoader

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class SettingsRef {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static IdeaLibManagerConfig config = new IdeaLibManagerConfig();

    public static File location = (System.getProperty('user.home') + '/jrr/idea/libmanager.groovy') as File

    public static File locationEdit = (System.getProperty('user.home') + '/jrr/idea/libmanager_edit.groovy') as File




    static void loadSettingsS2() {
        try {
            if (location.exists()) {
                SettingsRef.loadSettingsS(location)
            }
        } catch (Throwable e) {
            log.log(Level.SEVERE, "failed load config", e)
            JrrUtilities.showException("failed load config", e)
        }
    }

    static void loadSettingsS(File configLocation) {
        log.info "file exist : ${configLocation} ? ${configLocation.exists()}"

        if (configLocation.exists()) {
            try {
                loadSettingsS(configLocation.text, configLocation.name, config)
            } catch (Throwable e) {
                log.info("failed load : ${configLocation}", e)
                throw e
            }
        } else {
            throw new FileNotFoundException("${configLocation}")
        }
    }

    static void loadSettingsS(String scriptSource, String scriptName, IdeaLibManagerConfig config3) {
        Map<File, String> map = new HashMap<>()
        Binding binding = new Binding()
        binding.setVariable('a', config3)

        Script script = GroovySettingsLoader.groovySettingsLoader.createScript(scriptSource, scriptName, binding)
        script.run();
    }


}
