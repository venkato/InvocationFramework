package net.sf.jremoterun.utilities.nonjdk.depanalise

import groovy.transform.CompileStatic
import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorSup2Groovy

import java.util.logging.Logger

/**
 * Doesn't check that method exists
 */
@CompileStatic
class DependencyChecker {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AddFilesToJavasistPool classPool = new AddFilesToJavasistPool(new ClassPool());
    Set<File> urls = new HashSet<>()
    Collection<String> checked = []
    DepChekrInt depCheck = new GroovyDepCheker();

    CtClass getClass(String name) {
        return classPool.classPool.get(name)
    }

    ClassPathCalculatorSup2Groovy resolveMavenIds() {
        ClassPathCalculatorSup2Groovy classPathCalculatorGroovy = new ClassPathCalculatorSup2Groovy()
        classPathCalculatorGroovy.addFilesToClassLoaderGroovySave.addGenericEnteries(urls)
        classPathCalculatorGroovy.calcClassPathFromFiles12()
        return classPathCalculatorGroovy
    }

    void checkForCurrentCLassPath(String name) {
        JrrJavassistUtils.init()
        classPool.classPool = ClassPool.getDefault();
//        classPool.addFileWhereClassLocated(JrrClassUtils)
//        classPool.addFileWhereClassLocated(JrrUtils)
        checkRefs(name)
        printInfo()
    }

    void check(File classPath, String name) {
        classPool.addFromGroovyFile(classPath)
        checkRefs(name)
        printInfo()
    }

    void printInfo() {
        log.info "lookup classes : ${checked.size()}"
        resolveMavenIds().filesAndMavenIds.sort().each { println(it) }
    }


    void checkRefs(Class name) {
        checkRefs(name.name)
    }

    void checkRefs(String name) {
        List<String> classStackOnException = []
        try {
            checkRefsImpl(name, classStackOnException)
        } catch (NotFoundException e) {
//            classStackOnException.each {println(it)}
            log.fine("failed resolve dep for ${name}", e)
            throw new NotFoundException("failed resolve all dep for ${name}, classes stack :\n ${classStackOnException.join('\n')}");
        }
    }





    void checkRefsImpl(String name, List<String> classStackOnException) {
        CtClass clazz = classPool.classPool.get(name);
        checked.add(name);
        URL url = classPool.classPool.find(name)
        assert url != null
        urls.add(UrlCLassLoaderUtils.convertClassLocationToPathToJar(url, name))
        List<String> each = clazz.getRefClasses() as List
        each = each.findAll { depCheck.needAnalize(it) };
        each.each {
            if (!checked.contains(it)) {
                try {
                    checkRefsImpl(it, classStackOnException);
                } catch (NotFoundException e) {
                    classStackOnException.add(it)
                    throw e
                }
            }
        }
    }
}
