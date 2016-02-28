package net.sf.jremoterun.utilities.nonjdk.classpath.search

import net.sf.jremoterun.utilities.classpath.MavenId

import java.util.logging.Logger

class MavenResponseParser {

    private static final Logger log = Logger.getLogger(MavenResponseParser.name);

    static List<String> parseResponse(Object json){
        try {
            return json.response.docs.collect { it.id }
        }catch (Exception e){
            log.info "failed parse : ${json}"
            throw e;
        }
    }


    static List<MavenId> parseAllWithGroupLatestResponse(Object json){
        try {
            return json.response.docs.collect { new MavenId(it.id +':'+ it.latestVersion) }
        }catch (Exception e){
            log.info "failed parse : ${json}"
            throw e;
        }
    }

}
