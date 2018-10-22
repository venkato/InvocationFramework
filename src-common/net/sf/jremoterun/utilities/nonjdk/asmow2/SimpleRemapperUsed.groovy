package net.sf.jremoterun.utilities.nonjdk.asmow2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.objectweb.asm.commons.SimpleRemapper;

import java.util.logging.Logger;

@CompileStatic
class SimpleRemapperUsed extends SimpleRemapper{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    Map<String, String> mapping2
    boolean used = false

    SimpleRemapperUsed(Map<String, String> mapping) {
        super(mapping)
        mapping2 = mapping
    }

    @Override
    String map(String key) {
        if(mapping2.containsKey(key)){
            used = true
        }
        return super.map(key)
    }
}
