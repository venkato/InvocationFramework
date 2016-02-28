package net.sf.jremoterun.utilities.nonjdk.classpath.refs;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.git.SvnRef
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class SvnRefs {


    public static SvnSpec derbySrc = new SvnSpec('http://svn.apache.org/repos/asf/db/derby/code/trunk/java/drda')

}
