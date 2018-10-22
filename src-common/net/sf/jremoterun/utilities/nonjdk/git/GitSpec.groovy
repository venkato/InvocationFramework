package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.Sortable
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef

import java.util.logging.Logger

@Canonical
@CompileStatic
@Sortable
class GitSpec implements Serializable, ToFileRef2, ChildFileLazy,GitSpecRef  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static String checkoutDirDefault = 'git';

    String repo;
    String commitId;
    String branch;
    String tag;
    String checkoutDir = checkoutDirDefault;

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

//    File resolveToFileIfDownloaded() {
//        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
//        if(handler==null){
//            throw new IllegalStateException("customObjectHandler was not set")
//        }
//        return handler.resolveToFileIfDownloaded(this)
//    }


    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child)
    }

    @Override
    GitSpec getGitSpec() {
        return this
    }

    SvnSpec convertToSvnRef(){
        return GitToSvnConverter.buildRef2(this)
    }
}
