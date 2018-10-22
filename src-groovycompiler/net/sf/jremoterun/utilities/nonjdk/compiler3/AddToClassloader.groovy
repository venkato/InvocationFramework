package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon;

@CompileStatic
interface AddToClassloader {

    void add(AddFilesToClassLoaderCommon adder);

}