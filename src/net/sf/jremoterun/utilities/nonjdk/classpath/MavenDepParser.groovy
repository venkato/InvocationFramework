package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.util.slurpersupport.GPathResult;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


// @CompileStatic
class MavenDepParser {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static List<MavenId> findDepsFromPomXml(String text){
        GPathResult xmlRes = new XmlSlurper().parseText(text);
        Object deps2 = xmlRes.dependencies;
        if(deps2==null){
            throw new IllegalArgumentException("no dependencies element")
        }
        List deps = deps2.dependency.list();
        List<MavenId> mavenIds = deps.collect {
            String group = it.groupId;
            String artifact = it.artifactId;
            String version = it.version;
            return new MavenId(group, artifact, version)
        }
        return mavenIds
    }


    static List<MavenId> getDepsFromMavenDepSnippet(String text){
        String s = "<any> ${text}  </any>"
        GPathResult res = new XmlSlurper().parseText( s    );
        List deps = res.dependency.list();
        List<MavenId> mavenIds = deps.collect {
            String group = it.groupId;
            String artifact = it.artifactId;
            String version = it.version;
            return new MavenId(group, artifact, version)
        }
        return mavenIds
    }


}
