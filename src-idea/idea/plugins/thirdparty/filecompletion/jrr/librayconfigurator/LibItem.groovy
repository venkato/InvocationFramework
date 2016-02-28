package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.roots.libraries.Library;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class LibItem implements Comparable<LibItem>{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Library library;

    LibItem(Library library) {
        this.library = library
    }

    @Override
    String toString() {
        return library.getName()
    }

    @Override
    int compareTo(@NotNull LibItem o) {
        return this.library.getName().compareTo(o.library.getName())
    }


}
