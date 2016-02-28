package net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.git.GitRef

import java.util.logging.Logger

@CompileStatic
class ClassPathCalculatorGitRefSup extends ClassPathCalculatorSup2Groovy {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static CustomObjectHandler customObjectHandler

    static CustomObjectHandler findHadnler() {
        if (customObjectHandler == null) {
            customObjectHandler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
            if (customObjectHandler == null) {
                throw new NullPointerException("customObjectHandler is null")
            }
        }
        return customObjectHandler
    }


    @Override
    File convertSpecificToFile(Object obj) {
        File f = super.convertSpecificToFile(obj)
        if (f == null) {
            if (obj instanceof GitRef) {
                GitRef gitRef = (GitRef) obj;
                findHadnler()
                f = customObjectHandler.resolveToFile(gitRef)
            }
        }
        return f;
    }

}
