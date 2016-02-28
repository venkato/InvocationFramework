package idea.plugins.thirdparty.filecompletion.share.Ideasettings

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.idea.set2.SettingsRef


@CompileStatic
class IdeaJavaRunner2Settings {


    public static File baseDir

    public static File libs
    public static File runners
    public static File jars

    public static volatile boolean suggestGrodupMavenIds = true

    public static List<String> jvmOptions = []

    static void initIfExist(){
        if(SettingsRef.config.libBaseDir==null) {
        }else{
            init3()

        }
    }

    static void init3(){
        assert SettingsRef.config.libBaseDir!=null
        setBaseDir(SettingsRef.config.libBaseDir)
    }

    @Deprecated
    static void setBaseDir(File baseDir3){
        baseDir = baseDir3
        libs = new File(baseDir3 ,'libraries')
        runners = new File(baseDir3 ,'perrunner')
        jars = new File(baseDir3 ,'jars')

    }

}
