package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Date;

public class IdeaBuildRunnerSettings {
    public static String jrrUseOneJarS = "jrrUseOneJar";
    public static String jrrStartOriginalIfJrrMissingS = "jrrStartOriginalIfJrrMissing";
    public static String jrrStartShowExceptionInSwingS = "jrrStartShowExceptionInSwing";
    public static String jrrIdeaRedirectOutToFileAuxS = "jrrIdeaRedirectOutToFileAux";
    public static String jrrIdeaForceUseStdS = "jrrIdeaForceUseStd";
    public static volatile boolean redirectOutToFileAux = ("true".equalsIgnoreCase(System.getProperty(jrrIdeaRedirectOutToFileAuxS)));
    public static volatile boolean jrrIdeaForceUseStd = ("true".equalsIgnoreCase(System.getProperty(jrrIdeaForceUseStdS)));
    public static volatile boolean useOneJar = !("false".equalsIgnoreCase(System.getProperty(jrrUseOneJarS)));
    public static volatile boolean startOriginal = !("false".equalsIgnoreCase(System.getProperty(jrrStartOriginalIfJrrMissingS)));
    public static volatile boolean originalTried = false;
    public static volatile boolean jrrStartShowExceptionInSwing = !("false".equalsIgnoreCase(System.getProperty(jrrStartShowExceptionInSwingS)));
    public static volatile URLClassLoader groovyCl;
    public static volatile File jrrpathF;
    public static volatile File outputFile;
    public static volatile Runnable beforeMainOriginalRun;
    //public static volatile Runnable afterMainOriginalRun;
    public static volatile File userHome = new File(System.getProperty("user.home"));
    public static volatile File ideaBuilderConfigFile = new File(userHome, "jrr/configs/ideaBuilder.groovy");
    public static Date startDate = new Date();
    public static volatile String[] argsP;
}
