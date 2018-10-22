package net.sf.jremoterun.utilities.nonjdk.idea.init2

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.InitPlugin2
import net.sf.jremoterun.SimpleFindParentClassLoader
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.nonjdk.LogExitTimeHook
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClasspathAdd
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2Utils

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class IdeaInit4 implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        init()
    }

    public static boolean inited = false;

    static void init() {
        if(inited){

        }else {
            inited = true
            try {
                initImpl()
            }catch(Throwable e){
                log.log(Level.SEVERE,"failed init idea ",e);
                JrrUtilities.showException("failed init idea ",e);
                throw e;
            }
        }
    }

    static void initImpl() {
        IdeaClasspathAdd.addCl.add GitReferences.rsta
        IdeaClasspathAdd.addCl.add GitReferences.rstaAutoCompetion
        net.sf.jremoterun.utilities.nonjdk.idea.IdeaCommonInit.init1()
        LogExitTimeHook.addShutDownHook();
        SimpleFindParentClassLoader.setDefaultClassLoader(JrrClassUtils.getCurrentClassLoader());
        Log4j2Utils.setLogLevel("idea.plugins.thirdparty.filecompletion", org.apache.log4j.Level.DEBUG)
        InitPlugin2.init()
        log.info2(new Date());
    }



}
