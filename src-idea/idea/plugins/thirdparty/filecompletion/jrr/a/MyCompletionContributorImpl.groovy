package idea.plugins.thirdparty.filecompletion.jrr.a

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

/**
 * Reserved for future flexibility
 */
@CompileStatic
class MyCompletionContributorImpl extends CompletionContributor {

    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
    }
}
