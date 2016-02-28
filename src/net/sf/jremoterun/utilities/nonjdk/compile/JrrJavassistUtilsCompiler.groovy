package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds

import java.util.logging.Logger

@CompileStatic
class JrrJavassistUtilsCompiler  extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static List mavenIds =  [
            LatestMavenIds.log4jOld
    ]

    void prepare() {
        params.javaVersion = '1.6'
        client.adder.addAll DropshipClasspath.allLibsWithGroovy
        client.adder.addAll mavenIds
        client.adder.add mcu.getToolsJarFile()
//        client.adder.addMavenPath DropshipClasspath.sisiFileBin
    }



}
