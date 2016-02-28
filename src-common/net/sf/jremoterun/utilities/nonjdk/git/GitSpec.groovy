package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2

import java.util.logging.Logger

@Canonical
@CompileStatic
class GitSpec implements Serializable, ToFileRef2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String repo;
    String commitId;
    String branch;
    String tag;

    GitSpec getSpecOnly() {
        return this
    }

    @Override
    File resolveToFile() {
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if(handler==null){
            throw new IllegalStateException("customObjectHandler was not set")
        }
        return handler.resolveToFile(this)
    }


}
