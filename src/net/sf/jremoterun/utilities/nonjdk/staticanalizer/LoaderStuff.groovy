package net.sf.jremoterun.utilities.nonjdk.staticanalizer

import groovy.transform.CompileStatic
import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.StaticElementInfo

import java.util.logging.Logger

@CompileStatic
class LoaderStuff {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClassLoader classLoader = JrrClassUtils.currentClassLoader

    List<ProblemFoundBean> problems = []
//    List<ProblemFoundBean> problems2
    List<ProblemFoundBean> parentFiles = []
    PrintStream problemStream = System.out


    void problemFound(StaticElementInfo pb, String msg) {
        ProblemFoundBean problemFoundBean = new ProblemFoundBean()
        problemFoundBean.msg = msg
        problemFoundBean.pb = pb
        problems.add(problemFoundBean)
    }

    Class loadClass(String className) {
        return classLoader.loadClass(className)
    }

    boolean checkClassExists(String className) {
        try {
            loadCtClass(className)
            return true
        } catch (NotFoundException e) {
            try {
                loadClass(className)
            } catch (ClassNotFoundException e2) {
                return false;
            }
        }
    }


    void onFileFound(StaticElementInfo pb, File f) {
        ProblemFoundBean problemFoundBean = new ProblemFoundBean()
        problemFoundBean.msg = f.absolutePath
        problemFoundBean.pb = pb
        parentFiles.add(problemFoundBean)
//        problemFound2(pb,f.absolutePath)
    }

    void problemFound2(StaticElementInfo pb, String msg) {
        int lineNo = pb.lineNumber
        if (lineNo == -1) {
            lineNo = 1
        }
        File f = pb.printablePath as File
        if (f.exists()) {
            problemStream.println("${pb.printablePath}: ${lineNo}: ${msg}")
        } else {
            problemStream.println("${pb.printablePath}.${pb.fieldName}(${pb.fileName}:${lineNo}) - ${msg}")
        }
    }


    void eachProblem(ProblemFoundBean pb) {
        switch (pb) {
            case { pb.pb.className == null }:
            case { pb.pb.printablePath == null }:
                throw new Exception("bad ${pb.pb}")
        }
    }

    boolean isPrintProblem(ProblemFoundBean pb) {
        return true
    }

    boolean isParentFile(ProblemFoundBean pb) {
        return pb.pb.isParentFile && pb.pb
    }

    void printProblems() {
        if (problems.size() == 0) {
            log.info("no problems found")
        } else {
            problems.each { eachProblem(it) }
            problems = problems.sort()
            problems = problems.findAll { isPrintProblem(it) }
            problemStream.println("================")
            problemStream.println("founded problems ${problems.size()} :")
            problems.each {
                problemFound2(it.pb, it.msg)
            }
        }
    }


    void printParentFiles() {
//        parentFiles.each { eachProblem(it) }
        parentFiles = parentFiles.sort()
        parentFiles = parentFiles.findAll { isParentFile(it) }
        problemStream.println("================")
        problemStream.println("parent files ${parentFiles.size()} :")
        parentFiles.each {
            problemFound2(it.pb, it.msg)
        }
    }


    CtClass loadCtClass(String className) {
        return ClassPool.getDefault().get(className)
    }


    void onException(Throwable excption, Object object) {
        log.info "failed analize ${object} ${excption}"
        throw excption;
    }

}
