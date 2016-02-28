package net.sf.jremoterun.utilities.nonjdk.idea.init2;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.ConsoleRedirect;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class IdeaLogRedirect {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    static void doLogOutRedirect(File logdir) throws Exception {
        File lockFile = null;
        int lockId = -1;
        for (int i = 1; i < 9; i++) {
            try {
                lockFile = new File(logdir, "lock-" + i + ".lock");
                RandomAccessFile raf = new RandomAccessFile(lockFile, "rwd");
                raf.writeChars("lock file");
                raf.getChannel().lock();
                lockId = i;
                break;
            } catch (Exception e) {
                log.info(i + "", e);
                lockFile = null;
            }
        }
        if (lockId > 0) {
            File logFile = new File(logdir, "idea-" + lockId + ".log");
            ConsoleRedirect.setOutputWithRotationAndFormatter(logFile, 10);
        } else {
            log.error("can't find log file");
        }

    }

}
