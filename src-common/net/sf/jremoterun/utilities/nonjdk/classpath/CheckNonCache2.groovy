package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2

import java.util.logging.Logger

@CompileStatic
abstract class CheckNonCache2 extends GroovyRunnerConfigurator2{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClRef jfeeObject = new ClRef('org.jfree.data.ComparableObjectItem')

    ClRef ivyCl = new ClRef('org.sonatype.aether.RepositorySystem')

    ClassLoader cl = JrrClassUtils.currentClassLoader;

    static void check() {
        check(CheckNonCache2);
    }

    static void check(Class toCheck) {
        URL location = JrrUtils.getClassLocation(toCheck);
        if (location == null) {

        } else {
            String string = location.toString();
            if (string==null||string.contains("cache")) {
                JrrUtilities.showException("Cached classes used : ${string}", new Exception("Cache used : ${string} for ${toCheck.name}"))
            }
        }
    }


    void loadcl(ClRef cnr) {
        try {
            cnr.loadClass(cl)
            log.info "nik cl loaderd : ${cl}"
        } catch (Throwable e) {
            log.info "nik cl failed loaderd : ${cl} ${e}"
        }
    }

    void initideacl(int id) {
        try {
            jfeeObject.loadClass(cl)
            log.info "nik classloaded"
        } catch (Throwable e) {
            log.info "${e}"
        }

        try {
            cl.loadClass("nosuchpackage.nusuchfile${id}")
        } catch (Throwable e) {

        }
    }


    void f1() {
        initideacl(1)
        loadcl(ivyCl)
        initideacl(2)
    }


}
