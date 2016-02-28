package net.sf.jremoterun.utilities.nonjdk.compiler3.javac

import com.sun.tools.javac.file.JavacFileManager
import com.sun.tools.javac.util.Context;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.tools.JavaFileManager;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JavaCompiler3 extends com.sun.tools.javac.main.Main{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    JavaCompiler3(String s, PrintWriter printWriter) {
        super(s, printWriter)
    }

    int compile3( String[] javacParameters){
//        return compile(javacParameters).exitCode
        Object[] arg = [javacParameters];
//        compile(javacParameters)
        Object result = JrrClassUtils.invokeJavaMethod2(this,'compile',arg);
        if (result instanceof Integer) {
            Integer  r= (Integer) result;
            return r;
        }
        Enum enumm = result as Enum
        int res = JrrClassUtils.getFieldValue(enumm,'exitCode') as int
        return res
    }

//    @Override
//    com.sun.tools.javac.main.Main.Result compile(String[] args, Context context) {
//        context.put(JavaFileManager.class, new Context.Factory<JavaFileManager>() {
//            public JavaFileManager make(Context c) {
//                return new JavacFileManagerC(c, true, null);
//            }
//        });
//        return super.compile(args, context)
//    }


}
