package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.ideasdk

import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.LibConfigurator8
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.LibItem
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.LibManager3;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences

import net.sf.jremoterun.utilities.nonjdk.idea.IdeaMavenRepoParser;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class IdeaSdkAddSourcesUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ClRef ideaJdkCl = new ClRef('org.jetbrains.idea.devkit.projectRoots.IdeaJdk')

//    public static List<GitRef> sourcesJterm = [
//            GitReferences.jtermSrc,
//            GitReferences.jtermPty.refToSourceOnly,
//            GitReferences.jtermSsh.refToSourceOnly,
//    ]


    static ProjectJdkImpl findIdeaSdk() {
        Sdk[] jdks = ProjectJdkTable.getInstance().getAllJdks()
        Sdk sdkFound = jdks.find {
            return isSdkMatched(it)
        }
        return sdkFound as ProjectJdkImpl
    }

    static boolean isSdkMatched(com.intellij.openapi.projectRoots.Sdk sdk){
        return sdk.getSdkType().getClass().getName() == ideaJdkCl.getName()
    }

    static void addMavenIdsSourcesToIdeaSdk(List items) {
        IdeaSdkToLibAdapterRead read = new IdeaSdkToLibAdapterRead()
        read.projectJdk = findIdeaSdk()
        LibItem libItem = new LibItem(read);
        LibManager3.addCustomAny(libItem, new NewValueListener<LibConfigurator8>() {
            @Override
            void newValue(LibConfigurator8 libConfigurator8) {
                libConfigurator8.addSourceGenericAll(items)
            }
        })
    }

    static void addIdeaSrc(){
        IdeaSdkToLibAdapterRead read = new IdeaSdkToLibAdapterRead()
        read.projectJdk = findIdeaSdk()
        LibItem libItem = new LibItem(read);
        LibManager3.addCustomAny(libItem, new NewValueListener<LibConfigurator8>() {
            @Override
            void newValue(LibConfigurator8 libConfigurator8) {
                File f = new IdeaMavenRepoParser().downloadIdeaSource(null)
                libConfigurator8.addSourceF(f);
            }
        })

        //IdeaMavenRepoParser
    }

    static void addSourcesToIdeaSdk() {
        IdeaSdkToLibAdapterRead read = new IdeaSdkToLibAdapterRead()
        read.projectJdk = findIdeaSdk()
        LibItem libItem = new LibItem(read);
        LibManager3 manager3 = new LibManager3(null)
        manager3.addSources(libItem, null)
    }




}
