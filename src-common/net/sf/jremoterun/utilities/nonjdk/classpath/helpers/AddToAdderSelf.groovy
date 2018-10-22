package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.nonjdk.classpath.console.auxp.AddFilesToClassLoader;

import java.util.logging.Logger;

@CompileStatic
interface AddToAdderSelf {

    void addToAdder(AddFilesToClassLoaderCommon adder)


}
