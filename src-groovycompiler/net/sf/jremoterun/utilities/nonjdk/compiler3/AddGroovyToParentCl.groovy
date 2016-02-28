package net.sf.jremoterun.utilities.nonjdk.compiler3;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.mdep.DropshipClasspath;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class AddGroovyToParentCl {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static AddGroovyToParentCl defaultAddtoParentCl = new AddGroovyToParentCl();


    void addGroovyJarToParentClassLoader(AddFilesToClassLoaderCommon adderParent){
        File file = UrlCLassLoaderUtils.getClassLocation(org.codehaus.groovy.runtime.typehandling.ShortTypeHandling)
        if(file.isFile()) {
//            log.info "adding groovy jar : ${file}"
            adderParent.add file
//            adderParent.add DropshipClasspath.groovy
        }else{
            log.warn("ShortTypeHandling strange : ${file}")
//            log.info "adding groovy jar : DropshipClasspath.groovy"
//            adderParent.add DropshipClasspath.groovy
        }
        adderParent.addFileWhereClassLocated GroovyObject
    }


}
