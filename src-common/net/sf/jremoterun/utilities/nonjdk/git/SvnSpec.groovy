package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef

import java.util.logging.Logger

@Canonical
@CompileStatic
class SvnSpec implements Serializable,  SvnSpecRef {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String repoPrefix = '';
    public final String repo;
    public final boolean runExport = true;

    SvnSpec(String repo) {
        this.repo = repo
    }

    @Override
    File resolveToFile() {
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if(handler==null){
            throw new IllegalStateException("customObjectHandler was not set")
        }
        return handler.resolveToFile(this)
    }


    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child)
    }

    @Override
    SvnSpec getSvnSpec() {
        return this;
    }

    SvnSpec buildChildRef(String subPath){
        String repo1= repo

        if(!repo1.endsWith('/')){
            repo1 = repo1+'/'
        }
        repo1 = repo1+subPath
        return new SvnSpec(repo1)
    }
}
