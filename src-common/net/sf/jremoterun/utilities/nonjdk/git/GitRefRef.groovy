package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy

@Deprecated
@CompileStatic
interface GitRefRef extends ToFileRef2, ChildFileLazy {

    GitRef getRef();

}