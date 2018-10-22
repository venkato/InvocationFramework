package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.ideasdk

import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectModelExternalSource
import com.intellij.openapi.roots.RootProvider
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTable
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import com.intellij.openapi.vfs.VirtualFile
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.LibConfigurator8
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.LibItem
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.LibManager3
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import org.jdom.Element
import org.jetbrains.annotations.NotNull

import java.util.logging.Logger

@CompileStatic
class IdeaSdkToLibAdapterRead implements Library {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    com.intellij.openapi.projectRoots.impl.ProjectJdkImpl projectJdk

    IdeaSdkToLibAdapter3 modif

//    @Override
//    void addRoot(@NotNull VirtualFile file, @NotNull OrderRootType rootType){
//        projectJdk.addRoot(file,rootType)
//    }

//    @Override
//    void commit() {
//        projectJdk.commitChanges();
//    }



    @Override
    String getName() {
        return projectJdk.name
    }

    //@Override
    String getPresentableName() {
        return getName()
    }

    @Override
    String[] getUrls(@NotNull OrderRootType rootType) {
        return projectJdk.getRoots(rootType).toList().collect { it.toString() }.toArray(new String[0])
    }

    @Override
    VirtualFile[] getFiles(@NotNull OrderRootType rootType) {
        return projectJdk.getRoots(rootType);
    }

    @Override
    Library.ModifiableModel getModifiableModel() {
        if (modif != null) {
            return modif
        }
        log.info "creating idea modif"
        modif = new IdeaSdkToLibAdapter3()
        modif.projectJdk = projectJdk.sdkModificator;
        return modif
    }

    @Override
    LibraryTable getTable() {
        return null
    }

    @Override
    RootProvider getRootProvider() {
        return null
    }

    @Override
    boolean isJarDirectory(@NotNull String url) {
        return false
    }

    @Override
    boolean isJarDirectory(@NotNull String url, @NotNull OrderRootType rootType) {
        return false
    }

    @Override
    boolean isValid(@NotNull String url, @NotNull OrderRootType rootType) {
        return false
    }


    boolean hasSameContent(@NotNull Library library) {
        log.info "has same content lib ${library}"
        return false
    }

    @Override
    void dispose() {

    }

    @Override
    void readExternal(Element element) throws InvalidDataException {

    }

    @Override
    void writeExternal(Element element) throws WriteExternalException {

    }

    @Override
    ProjectModelExternalSource getExternalSource() {
        return null
    }

}
