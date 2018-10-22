package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2;

import java.util.logging.Logger;

@CompileStatic
interface ChildFileLazy {

    FileChildLazyRef childL(String child);


}
