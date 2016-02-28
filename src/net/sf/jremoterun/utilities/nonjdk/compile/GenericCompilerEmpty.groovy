package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.compiler3.CompileRequestClient
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompilerParams
import org.apache.commons.io.FileUtils

import java.util.logging.Logger

@CompileStatic
class  GenericCompilerEmpty extends GenericCompiler{

    @Override
    void prepare() {

    }
}
