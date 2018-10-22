package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GroovyMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.Log4j2MavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.SshdMavenIds

import java.util.logging.Logger

import static net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.getMcu
import static net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.guavaMavenId
import static net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.jline2
import static net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.jline3
import static net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.junit

@CompileStatic
class DefaultClasspathAdder extends InjectedCode {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean addLatestFlag =false;

    public static List findLatest = []
    static {
        List l2 = findLatest
        l2.addAll((List) LatestMavenIds.usefulMavenIdSafeToUseLatest)
        l2.add CustObjMavenIds.slf4jApi
        l2.add Log4j2MavenIds.slf4j_impl
    }

    @Override
    Object get(Object o) {
        AddFilesToClassLoaderCommon adder = o as AddFilesToClassLoaderCommon;
        if (adder == null) {
            throw new IllegalArgumentException("adder is null")
        }
        addRefs(adder);
        return null;
    }

    static void addRefs(AddFilesToClassLoaderCommon adder) {
        adder.addM guavaMavenId;
        adder.addM junit;
        adder.addAll GroovyMavenIds.all;
        if(addLatestFlag) {
            List<MavenIdContains> findLatest3 = findLatest
            findLatest3.each {
                adder.addM(mcu.findLatestMavenOrGradleVersion2(it.m))
            };
        }else{
            adder.addAll(findLatest)
        }
        adder.addMWithDependeciesDownload jline3
        adder.addMWithDependeciesDownload jline2
        adder.addMWithDependeciesDownload SshdMavenIds.core
    }
}
