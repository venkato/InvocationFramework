package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon

@CompileStatic
interface AddToAdderSelf {

    void addToAdder(AddFilesToClassLoaderCommon adder)


}
