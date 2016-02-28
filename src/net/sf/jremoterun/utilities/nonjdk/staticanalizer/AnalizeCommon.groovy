package net.sf.jremoterun.utilities.nonjdk.staticanalizer

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.StaticElementInfo

import java.util.logging.Logger

@CompileStatic
abstract class AnalizeCommon<T extends StaticElementInfo> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    LoaderStuff loaderStuff;


    HashSet<String> badClassed = new HashSet()


    /**
     * Return false - means stop analisys. <br/>
     * Return true - means continue analisys
     *
     */
    abstract boolean analizeElement3(T t)

    abstract List<T> analizeDir2(File dir);

    abstract List<T> analizeJar(File dir);

//    abstract ProblemFoundBean buildLocaltion(T el, String msg);

//    private void analisysDirOrJar(File dirOrJar) {
//        assert dirOrJar.exists()
//        List<T> res = dirOrJar.directory ? analizeDir2(dirOrJar) : analizeJar(dirOrJar)
//        List<T> res2 = []
//
//        res.each {
//            if (analizeElement3(it)) {
//                res2.add(it)
//            }
//        }
//        res2.each { analize4(it) }
//    }


    abstract List<T> analizeFile(File f);

    HashSet<String> analizedGroovyFiles = new HashSet()

    List<StaticElementInfo> analisysDirOrJar3(File dirOrJar) {
        assert dirOrJar.exists()
        List<T> res;
        if (dirOrJar.isDirectory()) {
            res = analizeDir2(dirOrJar);
        } else {
            if (dirOrJar.name.endsWith('.jar')) {
                res = analizeJar(dirOrJar)
            } else {
                res = analizeFile(dirOrJar)
//                log.info "${res.collect{it.fieldName}}"
            }
        }

        List<T> res2 = []
//        log.info "${dirOrJar.name} ${res.size()}"
        res.each {
            try {
                analizedGroovyFiles.add(it.className)
                if (analizeElement3(it)) {
//                log.info "${it.fieldName}"
//                Thread.dumpStack()
                    res2.add(it)
                }
            } catch (Throwable e) {
                log.info "failed analize : ${it}"
                loaderStuff.onException(e, it)
//                throw e;
            }

        }
        return res2
    }


}
