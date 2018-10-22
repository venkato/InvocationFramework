package net.sf.jremoterun.utilities.nonjdk.classpath.search

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId

import java.util.logging.Logger

//@CompileStatic
class MavenResponseParser {

    private static final Logger log = Logger.getLogger(MavenResponseParser.getName());

    public Date acceptOnlyAfterDate;

    static List<String> parseResponse(Map json) {
        try {
            return json.response.docs.collect { it.id }
        } catch (Exception e) {
            log.info "failed parse : ${json}"
            throw e;
        }
    }


    static List<MavenId> parseAllWithGroupLatestResponse(Map json) {
        try {
            return json.response.docs.collect { new MavenId(it.id + ':' + it.latestVersion) }
        } catch (Exception e) {
            log.info "failed parse : ${json}"
            throw e;
        }
    }


    List<MavenId> parseAllWithGroupLatestResponse2(Map json) {
        try {
            List<Map> docs = json.response.docs;
            if (docs.size() == 0) {
                throw new IllegalArgumentException("No docs found in ${json}")
            }
            docs = docs.findAll { isDocMatched(it) }
            return docs.collect { new MavenId(it.id + ':' + it.latestVersion) }
        } catch (Exception e) {
            log.info "failed parse : ${json}"
            throw e;
        }
    }


    /**
     * key	value
     * a	azure-storage-queue
     * ec	ArrayList(4) = [-sources.jar, -javadoc.jar, .jar, .pom]
     * g	com.azure
     * id	com.azure:azure-storage-queue
     * latestVersion	12.2.0
     * p	jar
     * repositoryId	central
     * text	ArrayList(6) = [com.azure, azure-storage-queue, -sources.jar, -javadoc.jar, .jar, .pom]
     * timestamp	1578943729000
     * versionCount	8
    */
    boolean isDocMatched(Map json) {
        long ts = json.timestamp;
        return isDocMatched2(json, new Date(ts));
    }

    boolean isDocMatched2(Map json, Date lastUploadTime) {
        boolean accepted = false
        if (json.p == 'jar') {
            if (acceptOnlyAfterDate == null) {
                accepted = true;
            } else {
                accepted = lastUploadTime.getTime() > acceptOnlyAfterDate.getTime();
            }
        }
        return accepted;
    }

}
