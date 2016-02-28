package idea.plugins.thirdparty.filecompletion.share

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.TransactionGuard;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.ObjectWrapper;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JrrIdeaUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    static void submitTr(Runnable r) {
        submitTr2(r,false)
    }

    static void submitTr2(Runnable r,boolean waitResult) {
        ObjectWrapper<Boolean> showException = new ObjectWrapper<>(false);
        ObjectWrapper<Throwable> exc = new ObjectWrapper<>(null);
        Runnable r3 = {
            try {
                r.run()
            } catch (Throwable e) {
                log.info "${e}"
                exc.object = e;
                boolean  b = showException.object;
                if(b){
                    JrrUtilities.showException("Exception",e)
                }
            }
        }

        TransactionGuard instance = TransactionGuard.getInstance();
        log.info "instance.contextTransaction = ${instance.contextTransaction}"
        boolean allowed = ApplicationManager.getApplication().writeAccessAllowed
        log.info "wite access ${allowed}"
        if (instance.contextTransaction == null) {
            Runnable r2 = {
                instance.submitTransactionAndWait {
                        ApplicationManager.getApplication().runWriteAction(r3)
                }
            }
            Thread thread = new Thread(r2);
            if(waitResult){
                thread.start()
                thread.join()
            }else{
                showException.object = true
                thread.start()
            }
        } else {
            if (allowed) {
                r.run()
            } else {
                ApplicationManager.getApplication().runWriteAction(r3)
            }
        }
        if (exc.object != null) {
            throw exc.object
        }
    }

}
