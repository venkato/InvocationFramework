package net.sf.jremoterun.utilities.nonjdk.staticanalizer

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.StaticElementInfo
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.StaticElementInfoJavassist
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap

import java.lang.reflect.Constructor
import java.util.logging.Logger

@CompileStatic
class AnalizeCommon2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    AnalizeGroovyFile analizeGroovyFile;
    AnalizeJavaBinaryClass analizeJavaBinaryClass
    AnalizeJavaSourceFile analizeJavaSourceFile


    AnalizeCommon2(LoaderStuff loaderStuff) {
        analizeGroovyFile = new AnalizeGroovyFile(loaderStuff)
        analizeJavaBinaryClass = new AnalizeJavaBinaryClass(loaderStuff)
        analizeJavaSourceFile = new AnalizeJavaSourceFile(loaderStuff)
    }

//    Set<String> groovyClassesFound

    protected List<StaticElementInfo> filter1(List<StaticElementInfo> res) {
        // res.findAll {it instanceof ElementInfoGroovy}.collect{it.className}.toSet()
        res = res.findAll { filterEach(it) }
        return res
    }

    protected boolean filterEach2(StaticElementInfo el) {
        return true;
    }

    protected boolean filterEach(StaticElementInfo el) {
        String className = el.className
        boolean needAnalize = true
        if (el instanceof StaticElementInfoJavassist) {
            StaticElementInfoJavassist ee = (StaticElementInfoJavassist) el;
//            String className = ee.className
            if (analizeGroovyFile.analizedGroovyFiles.contains(className)) {
                needAnalize = false;
            }
            if (analizeJavaSourceFile.analizedGroovyFiles.contains(className)) {
                needAnalize = false;
            }
        }
        if (needAnalize) {
            needAnalize = filterEach2(el);
        }

        return needAnalize
    }


    void analisysDirOrJarMany(List<File> dirOrJar) {
        List<StaticElementInfo> res = [];
        dirOrJar.each {
            assert it.exists()
            try {
                res.addAll analisysDirOrJar(it)
            } catch (Throwable e) {
                // log.info("failed on inst ${className}", e)
                analizeGroovyFile.loaderStuff.onException(e, it)
            }
        }
//log.info("${res.size()}")
        res = filter1(res);
//        log.info("${res.size()}")

        ArrayListValuedHashMap<String, StaticElementInfo> staticFields = new ArrayListValuedHashMap();
        ArrayListValuedHashMap<String, StaticElementInfo> objectFields = new ArrayListValuedHashMap();
        List<StaticElementInfo> el = res.each {
            StaticElementInfo el = it
            if (el.isStatic()) {
                staticFields.put(el.className, el)
            } else {
                objectFields.put(el.className, el)
            }

        }
        staticFields.keySet().each {
            String className = it
            List<StaticElementInfo> get2 = staticFields.get(className)
            try {
                Class clazz = analizeGroovyFile.loaderStuff.loadClass(className)
                analizeS(clazz, get2)
            } catch (Throwable e) {
                // log.info("failed getClass ${className}", e)
                objectFields.remove(className)
                analizeGroovyFile.loaderStuff.onException(e, className)
            }
        }
//        log.info("${objectFields.size()}")
        objectFields.keySet().each {
            String className = it
            List<StaticElementInfo> get2 = objectFields.get(className)
            try {
//                log.info "${get2.collect {it.fieldName}}"
                Class clazz = analizeGroovyFile.loaderStuff.loadClass(className)
                Constructor constructor = clazz.getDeclaredConstructor(params)
                constructor.setAccessible(true)
                Object inst = constructor.newInstance(args)
                analizeS(inst, get2)
            } catch (Throwable e) {
                // log.info("failed on inst ${className}", e)
                analizeGroovyFile.loaderStuff.onException(e, className)
            }
        }
    }

    protected List<StaticElementInfo> analisysDirOrJar(File dirOrJar) {
        assert dirOrJar.exists()
        List<StaticElementInfo> res = []
        boolean handled = false;
        if (dirOrJar.file) {
            if (dirOrJar.name.endsWith('.groovy')) {
//                log.info "checking ${dirOrJar.name}"
                res.addAll analizeGroovyFile.analisysDirOrJar3(dirOrJar)
                handled = true;
            }
            if (dirOrJar.name.endsWith('.java')) {
//                log.info "checking ${dirOrJar.name}"
                res.addAll analizeJavaSourceFile.analisysDirOrJar3(dirOrJar)
                handled = true;
            }
            if (dirOrJar.name.endsWith('.class')) {
                res.addAll analizeJavaBinaryClass.analisysDirOrJar3(dirOrJar)
                handled = true;
            }
        }
        if (!handled) {
            res.addAll analizeJavaBinaryClass.analisysDirOrJar3(dirOrJar)
            res.addAll analizeJavaSourceFile.analisysDirOrJar3(dirOrJar)
            res.addAll analizeGroovyFile.analisysDirOrJar3(dirOrJar)
        }
        return res;

    }

    private static Class[] params = new Class[0]
    private static Object[] args = new Object[0]


    protected void analizeS(Object instanm, List<StaticElementInfo> els) {
        assert instanm != null
        els.each {
            StaticElementInfo el = it;
            try {
                Object value3 = JrrClassUtils.getFieldValue(instanm, it.fieldName)

                if (value3 instanceof File) {
                    File f3 = (File) value3;
                    if (f3.exists()) {
                        analizeGroovyFile.loaderStuff.onFileFound(el, f3)
                    } else {
                        analizeGroovyFile.loaderStuff.problemFound(it, f3.path)
                    }
                }
                if (value3 instanceof ClRef) {
                    ClRef cnr = (ClRef) value3;
                    if (!analizeGroovyFile.loaderStuff.checkClassExists(cnr.className)) {
                        analizeGroovyFile.loaderStuff.problemFound(it, cnr.className)
                    }
//                    try {
//                        cnr.loadClass(analizeGroovyFile.loaderStuff.classLoader)
//                    } catch (ClassNotFoundException cnfe) {
//
//                    }

                }
            } catch (Throwable e) {
                analizeGroovyFile.loaderStuff.onException(e, el)
            }
        }
    }


}
