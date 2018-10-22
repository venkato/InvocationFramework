package net.sf.jremoterun.utilities.nonjdk.compile.auxh

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.compiler3.AddGroovyToParentCl;

import java.util.logging.Logger;

@CompileStatic
class AddGroovyToParentClResolver extends AddGroovyToParentCl{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void addGroovyJarToParentClassLoader(AddFilesToClassLoaderCommon adderParent) {
        //File dir = GitReferences.groovyClasspathDir.resolveToFile()
        adderParent.add JrrStarterJarRefs2.groovy_custom
        adderParent.add JrrStarterJarRefs2.groovy
        log.info "tmp cp1"
    }

    static void setRef(){
        AddGroovyToParentCl.defaultAddtoParentCl = new  AddGroovyToParentClResolver()
    }

}
