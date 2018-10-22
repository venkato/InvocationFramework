package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class IdeaAddFileWithSourcesFactory {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static IdeaAddFileWithSourcesFactory defaultFactory = new IdeaAddFileWithSourcesFactory();

    IdeaAddFileWithSources createAdded(){
        return new IdeaAddFileWithSources();
    }

}
