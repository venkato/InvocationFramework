package net.sf.jremoterun.utilities.nonjdk.idea.set2

import com.intellij.tools.Tool
import com.intellij.tools.ToolManager
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.IdeaLibManagerSwing
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.store.IgnoreField
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean
import net.sf.jremoterun.utilities.nonjdk.store.JavaBeanCustomSaver
import net.sf.jremoterun.utilities.nonjdk.store.JavaBeanStore
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.Writer3;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class IdeaLibManagerConfig implements JavaBean, JavaBeanCustomSaver{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File libBaseDir

    OpenFileToolEnum openFileTool;


    JarResolutionToMavenId resolveJarsToMavenId =JarResolutionToMavenId.max

//    @IgnoreField
    int minWidth = 250;

    void setMavenServer(String mavenServer){
        MavenDefaultSettings.mavenDefaultSettings.mavenServer = mavenServer;
    }

    @Override
    List<String> save(String varName, Writer3 writer3,ObjectWriter objectWriter){
        String mavenServer3 = MavenDefaultSettings.mavenDefaultSettings.mavenServer;
        String mavenServerS = objectWriter.writeObject(writer3,mavenServer3)
        List<Tool> tools = ToolManager.instance.tools
        tools =  tools.findAll {it.enabled}
        String toolsS =  tools.collect{it.name}.join(',')
        String s = "${varName}.setMavenServer ${mavenServerS} ;";
//        int minWidth = ideaLibManagerSwing.getMinWidth()
//        String s2 = "${varName}.ideaLibManagerSwing.setMinWidth ${minWidth} ;";
        toolsS = "// Put any available tools from Settings->Tools->External tools: ${toolsS}"
        List<String> list = JavaBeanStore.save(varName, this, writer3, objectWriter,true)
        List<String> result = [s,toolsS]+list
        return result
    }

}
