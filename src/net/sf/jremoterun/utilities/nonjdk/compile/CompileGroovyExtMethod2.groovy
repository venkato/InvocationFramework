package net.sf.jremoterun.utilities.nonjdk.compile;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.compiler3.CompileGroovyExtMethod
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class CompileGroovyExtMethod2 extends CompileGroovyExtMethod{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    CompileGroovyExtMethod2(File ifDir) {
        super(ifDir)
    }

    File zipp() {
        File resource = createGroovyExtMethodDir(ifDir);
        FileUtils.copyDirectory(resource, params.outputDir);
        File destJar = new File(ifDir, 'build/ifframework-ext-methods.jar')
        destJar.delete()
        assert !destJar.exists()
        ZipUtil.pack(params.outputDir, destJar)
        return destJar
    }

}
