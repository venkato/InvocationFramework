package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import java.util.logging.Level;
import java.util.logging.LogRecord

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter;
import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;

@CompileStatic
public class JdkLogColorFormatter extends JdkLogFormatter {

    public static volatile Ansi.Color consoleColor = Ansi.Color.YELLOW;

    @Override
    public  void logMessage(LogRecord logRecord, StringBuilder sb) {
        if(logRecord.getLevel().intValue()>= Level.SEVERE.intValue()){
            Ansi ansi = ansi();
            ansi = ansi.bg(consoleColor).a(logRecord.getMessage());
            ansi = ansi.bg(Ansi.Color.DEFAULT);
            sb.append(ansi);
        }else {
            super.logMessage(logRecord, sb);
        }
    }
}
