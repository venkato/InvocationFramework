package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ConsoleTextFunctionPrefix {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String functionWord = 'TextFunction : ';

    public static String ignoreWord = 'ignore ';

    public static List<String> ignoreWords = [ignoreWord, 'echo '];

}
