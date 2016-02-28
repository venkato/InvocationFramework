package net.sf.jremoterun.utilities.nonjdk.ideadep

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class LongTaskInfo {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    boolean cancelled = false;

    Stack<String> taskNames = new Stack<>();

    LongTaskInfo() {
    }

    void checkForCancel() throws UserTaskCancelled {
        if (getCancelled()) {
            throw new UserTaskCancelled()
        }
    }

    boolean getCancelled() {
        return cancelled
    }

    void setCurrentTask(String taskName) {
        checkForCancel()
        if (taskNames.empty()) {
            taskNames.push(taskName)
        } else {
            int position = taskNames.size() - 1
            taskNames.set(position, taskName)
        }
    }

    void startSubTask(String taskName) {
        taskNames.push(taskName)
    }


    void finishSubTask() {
        taskNames.pop()
    }

}
