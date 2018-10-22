package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId;

import java.util.logging.Logger;

@CompileStatic
class AndroidArchive {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public MavenId m;

    AndroidArchive(MavenId m) {
        this.m = m
    }

    @Override
    String toString() {
        return "aar: " + m.toString()
    }
}
