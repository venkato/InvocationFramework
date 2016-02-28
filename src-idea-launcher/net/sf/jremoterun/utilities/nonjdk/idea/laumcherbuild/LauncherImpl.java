package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import java.util.Date;

public class LauncherImpl {

    public static String[] argsP;
    public static Date startDate = new Date();



    public static void main(String[] args) throws Exception {
        argsP = args;
        IdeaBuilderAddGroovyRuntime.f1();
    }


}
