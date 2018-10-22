package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode

import java.util.logging.Logger

@CompileStatic
class IdeaBRunner21 extends InjectedCode {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public
    static ClRef ifFrameWoekAdder = new ClRef('net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure')

    public static AddFilesToUrlClassLoaderGroovy adder = new AddFilesToUrlClassLoaderGroovy(JrrClassUtils.currentClassLoaderUrl)


    @Override
    Object get(Object key) {
        File f = key as File
        f1(f)
        return null
    }

    static void f1(File ifBaseDir) {
        log.info "loading framework"
        assert ifBaseDir.exists()
        adder.add ifBaseDir.child("src-frameworkloader")
        RunnableWithParamsFactory.fromClass4(ifFrameWoekAdder, [adder, ifBaseDir])

    }
}
