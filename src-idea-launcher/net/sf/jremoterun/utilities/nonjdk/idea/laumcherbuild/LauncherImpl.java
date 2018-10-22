package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class LauncherImpl {

    public static void main(String[] args) throws Exception {
        IdeaBuildRunnerSettings.argsPv2 = new ArrayList<>( Arrays.asList(args));
        try {
            IdeaBuilderAddGroovyRuntime.f1();
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }


}
