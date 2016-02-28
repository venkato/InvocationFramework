package net.sf.jremoterun.utilities.nonjdk.idea.set2

import com.intellij.tools.Tool
import com.intellij.tools.ToolManager;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.EnumIdeaSupport2;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class OpenFileToolEnumIdeaSupport implements EnumIdeaSupport2{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    List<String> getProposals(String enteredText) {
        List<Tool> tools = ToolManager.instance.tools
        return tools.collect {it.name}
    }
}
