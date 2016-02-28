package net.sf.jremoterun.utilities.nonjdk.staticanalizer;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class CurrentDirDetector {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static boolean isCurrentDir(String text){
        if(text.length()==0||text=='.'){
            return true
        }
        return false
    }

}
