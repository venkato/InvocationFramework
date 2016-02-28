package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.utilities.JrrClassUtils;

import java.util.logging.Logger;

import groovy.transform.CompileStatic;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

@CompileStatic
public class Log4j2ColorPatternLayout extends Log4j2PatternLayout {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static volatile Color consoleColor =Color.YELLOW;


    @Override
    public void logMessage(StringBuilder sb, String msg,LogEvent logRecord) {
        if(logRecord.getLevel().isMoreSpecificThan(Level.WARN)){
            Ansi ansi = ansi();
            ansi = ansi.bg(consoleColor).a(msg);
            ansi = ansi.bg(Color.DEFAULT);
            sb.append(ansi);
        }else {
            sb.append(msg);
        }
    }
}
