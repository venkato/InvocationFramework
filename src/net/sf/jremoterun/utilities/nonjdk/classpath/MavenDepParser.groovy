package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChildren;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


// @CompileStatic
class MavenDepParser {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<MavenId> findDepsFromPomXml2(String text){
        GPathResult result = new XmlSlurper().parseText(text);
        Collection deps2 =result.depthFirst().findAll {it.name() == 'dependency'}
        if(deps2==null){
            throw new IllegalArgumentException("no dependencies element")
        }
        List<MavenId> mavenIds = deps2.collect {convertDepToMavenId(it)}
        return mavenIds

    }


    MavenId extractMavenId(String text){
        GPathResult xmlRes2 = new XmlSlurper().parseText(text);
        Object xmlRes =xmlRes2
//        Object xmlRes =xmlRes2.result.depthFirst().findAll {it.name() == 'project'}[0]
//        groovy.util.slurpersupport.Node xmlRes =xmlRes2.childNodes().next()

//        assert xmlRes!=null
//        log.info "${xmlRes.getClass()}"
        String group = xmlRes.groupId;
//        log.info "group = ${group}"
        String artifact = xmlRes.artifactId;
        assert artifact!=null
        assert artifact.length()>0
        assert group!=null
        assert group.length()>0
        String version = xmlRes.version;
        if(version==null || version.length()==0){
            //groovy.util.slurpersupport.NodeChildren
            Object parent1 = xmlRes.parent
            if(parent1==null){
                throw new Exception("Failed find version")
            }
            assert parent1!=null
//            log.info "${parent1.getClass()}"
            version= parent1.version
        }
        assert version!=null
        assert version.length()>0

        MavenId mavenId = new MavenId(group,artifact,version)
        return mavenId
    }

    List<MavenId> findDepsFromPomXml(String text){
        GPathResult xmlRes = new XmlSlurper().parseText(text);
        Object deps2 = xmlRes.dependencies;
        if(deps2==null){
            throw new IllegalArgumentException("no dependencies element")
        }
        List deps = deps2.dependency.list();
        List<MavenId> mavenIds = deps.collect {convertDepToMavenId(it)}
        return mavenIds
    }


    MavenId convertDepToMavenId(Object obj){
        String group = obj.groupId;
        String artifact = obj.artifactId;
        String version = obj.version;
        return new MavenId(group, artifact, version)
    }

    List<MavenId> getDepsFromMavenDepSnippet(String text){
        String s = "<any> ${text}  </any>"
        GPathResult res = new XmlSlurper().parseText( s    );
        List deps = res.dependency.list();
        List<MavenId> mavenIds = deps.collect {convertDepToMavenId(it)}
        return mavenIds
    }


}
