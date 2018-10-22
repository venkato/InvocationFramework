package net.sf.jremoterun.utilities.nonjdk.classpath.search

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.UserBintrayRepo;

import java.util.logging.Logger;

@CompileStatic
class BintraySearchResult {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public UserBintrayRepo bintrayRepo;

    public MavenId mavenId;
    public Map<String,String> rawResult;

    @Override
    String toString() {
        return mavenId.toString()
    }
}
