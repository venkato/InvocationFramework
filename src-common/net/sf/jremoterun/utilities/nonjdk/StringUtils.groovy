package net.sf.jremoterun.utilities.nonjdk;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class StringUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static String replaceNewLine(String s){
        s = s.replace('\r\n','\n');
        s = s.replace('\r','\n');
        return s;
    }



    static int lastIndexOf(String orig,char symbol,int skipCount,int fromIndex){
        //lastIndexOf(orig,symbol,skipCount--)
        int i = orig.lastIndexOf((int)symbol, fromIndex);
        if(i==-1){
            return -1;
        }
        if(skipCount==0){
            return i;
        }
        if(i==0){
            return -1;
        }
        return lastIndexOf(orig,symbol,skipCount-1,i-1);


    }

    static int lastIndexOf(String orig,char symbol,int skipCount){
        return lastIndexOf(orig,symbol,skipCount,orig.length()-1)
    }

}
