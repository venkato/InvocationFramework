package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2

@CompileStatic
interface GitRefRef extends ToFileRef2 {

    GitRef getRef();

}