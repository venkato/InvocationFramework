package net.sf.jremoterun.utilities.nonjdk.classpath.helpers;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class RefLink implements ToFileRef2{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Enum enumm
    public ToFileRef2 fileRef;

    RefLink(Enum enumm) {
        this.enumm = enumm
        fileRef = enumm as ToFileRef2
        if(enumm==null){
            throw new NullPointerException("enum is null")
        }
    }

    @Override
    File resolveToFile() {
        return fileRef.resolveToFile()
    }

}
