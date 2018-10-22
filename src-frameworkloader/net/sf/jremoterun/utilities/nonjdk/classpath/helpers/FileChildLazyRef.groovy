package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2;

import java.util.logging.Logger;

@CompileStatic
class FileChildLazyRef implements ToFileRef2 , ChildFileLazy{
    //private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ToFileRef2 parentRef;
    String child;

    FileChildLazyRef(ToFileRef2 parentRef, String child) {
        this.parentRef = parentRef
        this.child = child
    }

    @Override
    File resolveToFile() {
        File parentFile = parentRef.resolveToFile()
        return new File(parentFile,child);
//        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
//        if(handler==null){
//            throw new IllegalStateException("customObjectHandler was not set")
//        }
//        return handler.resolveToFile(this)
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child)
//        return null;
    }

    @Override
    String toString() {
        return "${parentRef} .child( ${child} )"
    }
}
