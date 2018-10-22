package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.ide.plugins.cl.PluginClassLoader
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.mdep.DropClassAdder
import net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClasspathAdd

import java.util.logging.Logger

@CompileStatic
class IdeaRedefineClassloaderSupport implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static ClRef ivyDepResolver = new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.IvyDepResolverSetter')

    public static PluginClassLoader cl = JrrClassUtils.currentClassLoader as PluginClassLoader;

    public static ClRef redefineIdeaClassLoader = new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.IdeaRedefineClassloader')

    public static AddFilesToClassLoaderCommon adder = IdeaClasspathAdd.addCl;

//    public static List<DropshipClasspath> mavenIdDeps = [
////        DropshipClasspath.httpCore,
//        DropshipClasspath.ivyMavenId,
//    ]


    volatile static boolean inited = false

    static void setCommonStuff() {
        if (inited) {
            log.info "already inited"
        } else {
            inited = true
            setCommonStuffImpl()
        }
    }



    static void setCommonStuffImpl() {
        AddJrrLibToCommonIdeaClassloader2.addJrrLibToCommonIdeaClassloader3()
        DropClassAdder.addDepWhichNeeded(adder,cl)
        RunnableFactory.runRunner2(ivyDepResolver, cl)
        RunnableFactory.runRunner2(redefineIdeaClassLoader,cl)

    }

    @Override
    void run() {
        setCommonStuff()
    }
}
