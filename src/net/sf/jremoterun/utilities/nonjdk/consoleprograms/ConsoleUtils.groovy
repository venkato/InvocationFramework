package net.sf.jremoterun.utilities.nonjdk.consoleprograms;

import net.sf.jremoterun.utilities.JrrClassUtils
import org.fusesource.jansi.Ansi;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class ConsoleUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    static boolean waitConsoleYes(){
//        log.info "write : y"
        BufferedReader reader = System.in.newReader();
        String line = reader.readLine()
        if(line.length()==0){
            System.out.println("type y(es) or any text to reject")
            return waitConsoleYes();
        }
        line = line.toLowerCase()
        if(line == 'y'||line=='yes'){
            return true
        }
        return false
    }

    static Ansi hilightOutput(Object msg){
        Ansi msg2 = Ansi.ansi().bg(Ansi.Color.YELLOW).a(msg).bg(Ansi.Color.DEFAULT).a('\n')
        return msg2
    }

}
