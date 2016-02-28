package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.zeroturnaround.zip.NameMapper

import java.util.logging.Logger

@CompileStatic
class ZipExcludeDefault implements NameMapper {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    "**/.git/**,**/.svn/**,**/*.class,**/*.jar"

    List<String> containsExclude = ['.git/','.svn/','.gradle/']
    List<String> fileSuffixExclude = ['.class','.jar']

    @Override
    String map(String name) {
        log.debug "${name}"
        String find3 = containsExclude.find { name.contains(it)  }
        if (find3 != null) {
            log.debug "ignore ${name} due to contains filter ${find3}"
            return null
        }
        String find = fileSuffixExclude.find { name.endsWith(it) }
        if (find != null) {
            log.debug "ignore ${name} due to endsWith filter ${find}"
            return null
        }
        return name
    }

}
