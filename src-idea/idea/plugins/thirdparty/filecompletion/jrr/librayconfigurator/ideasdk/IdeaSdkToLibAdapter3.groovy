package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.ideasdk

import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.vfs.VirtualFile
import groovy.transform.CompileStatic
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.NotNull

@CompileStatic
class IdeaSdkToLibAdapter3 implements Library.ModifiableModel {
//     ProjectJdkTable.instance.allJdks.dumpc()

    SdkModificator projectJdk


    @Override
    void addRoot(@NotNull VirtualFile file, @NotNull OrderRootType rootType) {
        projectJdk.addRoot(file, rootType)
    }


    @Override
    VirtualFile[] getFiles(@NotNull OrderRootType rootType) {
        return projectJdk.getRoots(rootType);
    }

    @Override
    void commit() {
        projectJdk.commitChanges();
//        projectJdk.
    }

    @Override
    void addJarDirectory(@NotNull VirtualFile file, boolean recursive) {

    }

    @Override
    void addJarDirectory(@NotNull VirtualFile file, boolean recursive, @NotNull OrderRootType rootType) {

    }

    @Override
    void moveRootUp(@NotNull String url, @NotNull OrderRootType rootType) {

    }

    @Override
    void moveRootDown(@NotNull String url, @NotNull OrderRootType rootType) {

    }

    @Override
    boolean removeRoot(@NotNull String url, @NotNull OrderRootType rootType) {
        return false
    }


    @Override
    boolean isChanged() {
        return false
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

    @Override
    String[] getUrls(@NotNull OrderRootType rootType) {
        return new String[0]
    }

    @Override
    void setName(String name) {

    }

    @Override
    String getName() {
        return projectJdk.name
    }

    @Override
    void addRoot(@NotNull @NonNls String url, @NotNull OrderRootType rootType) {

    }

    @Override
    void addJarDirectory(@NotNull String url, boolean recursive) {

    }

    @Override
    void addJarDirectory(@NotNull String url, boolean recursive, @NotNull OrderRootType rootType) {

    }

    @Override
    void dispose() {

    }
}
