package idea.plugins.thirdparty.filecompletion.jrr.a.file.sample

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
enum SampleEnum {
    a1('c:/Users/' as File),
    a2('c:/windows/' as File),
    ;

    public File f;

    SampleEnum(File f) {
        this.f = f
    }

    File getF2(){
        return f;
    }
}
