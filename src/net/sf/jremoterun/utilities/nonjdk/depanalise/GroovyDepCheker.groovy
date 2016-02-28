package net.sf.jremoterun.utilities.nonjdk.depanalise;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class GroovyDepCheker implements DepChekrInt{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

   public List<String> ignoreClasses = [
//            org.codehaus.groovy.control.XStreamUtils.name,
    ]

    @Override
    boolean needAnalize(String name) {
        switch (name){
            case {name.startsWith('org.ietf.jgss.')}:
            case {name.startsWith('org.xml.sax.')}:
//            case {name.startsWith('org.w3c.dom.')}:
            case {name.startsWith('java.')}:
            case {name.startsWith('javax.')}:
            case {name.startsWith('sun.reflect.')}:
            case {name.startsWith('sun.misc.')}:
            case {name.startsWith('sun.awt.')}:
            case {name.startsWith('groovy.lang.')}:
            case {name.startsWith('groovy.ui.')}:
            case {name.startsWith('groovy.io.')}:
            case {name.startsWith('groovy.transform.')}:
            case {name.startsWith('org.codehaus.groovy.')}:
            case {name.startsWith('groovyjarjarasm.asm.')}:
            case {name.startsWith('groovyjarjarcommonscli.')}:
                return false;
//            case {name.startsWith('sun.')}:
//                log.info("${name}")
//                return false;
        }
        if(ignoreClasses.contains(name)){
            return false
        }
        return true
    }
}
