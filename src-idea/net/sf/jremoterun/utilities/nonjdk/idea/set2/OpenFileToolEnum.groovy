package net.sf.jremoterun.utilities.nonjdk.idea.set2

import com.intellij.tools.Tool
import com.intellij.tools.ToolManager
import idea.plugins.thirdparty.filecompletion.jrr.IndexReadyListener;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.EnumIdea
import net.sf.jremoterun.utilities.nonjdk.store.EnumIdeaSupport2

import javax.swing.JOptionPane
import javax.swing.SwingUtilities;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class OpenFileToolEnum implements EnumIdea {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String toolName;

    @Override
    EnumIdeaSupport2 getEnumIdeaSupport2() {
        return null
    }

    static OpenFileToolEnum getE(String value){
//        List<Tool> tools = ToolManager.instance.tools
//        Tool tool3 = tools.find { it.name == value }
//        if(tool3==null){
//            throw new Exception("tool not found ${value}")
//        }
        OpenFileToolEnum res = new OpenFileToolEnum();
        res.toolName = value
        if(IndexReadyListener.indexReady){
            if(res.findTool()==null){
                throw new IllegalArgumentException("tool not found : ${res.toolName}")
            }
        }else{
            IndexReadyListener.addListenerAfterIndexReady{
                if(res.findTool()==null){
                    SwingUtilities.invokeLater{
                    JOptionPane.showMessageDialog(null,"tool not found : ${res.toolName}")

                    }
                }
            }
        }
        return res;
    }

    Tool findTool(){
        List<Tool> tools = ToolManager.instance.tools
        Tool tool3 = tools.find { it.name == toolName }
        if(tool3==null){
            log.info ("tool not found : ${toolName}")
        }
//        OpenFileToolEnum res = new OpenFileToolEnum();
//        res.tool = tool3
        return tool3
    }


    @Override
    String getName() {
        return toolName
    }



}
