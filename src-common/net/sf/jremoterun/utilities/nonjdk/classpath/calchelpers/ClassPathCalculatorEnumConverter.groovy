package net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorAbstract
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorWithAdder;
import net.sf.jremoterun.utilities.classpath.MavenId;
import net.sf.jremoterun.utilities.classpath.MavenIdContains;
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.AllMavenIdsRefs;

import java.util.Map;

@CompileStatic
public class ClassPathCalculatorEnumConverter extends ClassPathCalculatorSup2Groovy {

    AllMavenIdsRefs allMavenIdsRefs;
    Map<String, Object> mavenIdMavenIdContainsMap;

    public ClassPathCalculatorEnumConverter(AllMavenIdsRefs allMavenIdsRefs) {
        this.allMavenIdsRefs = allMavenIdsRefs;
        mavenIdMavenIdContainsMap = allMavenIdsRefs.buildMapWithoutVersion()
    }

    @Override
    Object filterOnMavenId(MavenId mavenId, String group, String artifact) {
        String mavenId1 = allMavenIdsRefs.convertMavenId(mavenId)
        Object mavenIdContains1 = mavenIdMavenIdContainsMap.get(mavenId1)
        if(mavenIdContains1==null){
            return mavenId;
        }
        return mavenIdContains1;
    }

    @Override
    Object transformMavenIdContains(MavenIdContains mavenIdContains) {
        return mavenIdContains;
    }
}
