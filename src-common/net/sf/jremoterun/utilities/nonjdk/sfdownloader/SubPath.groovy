package net.sf.jremoterun.utilities.nonjdk.sfdownloader

import net.sf.jremoterun.utilities.classpath.ToFileRef2
import groovy.transform.CompileStatic;


@CompileStatic
class SubPath {

    ToFileRef2 base;

    String subPath;

    SubPath(ToFileRef2 base, String subPath) {
        this.base = base
        this.subPath = subPath
    }

    @Override
    String toString() {
        return "${base} ${subPath}"
    }
}
