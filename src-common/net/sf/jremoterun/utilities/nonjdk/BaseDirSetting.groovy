package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ZeroOverheadFileRef;

import java.util.logging.Logger;

@CompileStatic
enum BaseDirSetting implements ToFileRef2, ChildFileLazy, ZeroOverheadFileRef{

    baseDirSetting,
    ;

    public File baseDir = new File(MavenDefaultSettings.mavenDefaultSettings.userHome, "jrr/");

    @Override
    File resolveToFile() {
        return baseDir;
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child)
    }
}
