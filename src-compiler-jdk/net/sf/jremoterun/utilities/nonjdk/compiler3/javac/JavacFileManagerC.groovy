package net.sf.jremoterun.utilities.nonjdk.compiler3.javac

import com.sun.tools.javac.file.JavacFileManager
import com.sun.tools.javac.util.Context;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.nio.charset.Charset;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JavacFileManagerC extends JavacFileManager{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    JavacFileManagerC(Context context, boolean register, Charset charset) {
        super(context, register, charset)
    }
}
