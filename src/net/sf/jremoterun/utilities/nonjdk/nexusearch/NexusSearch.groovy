package net.sf.jremoterun.utilities.nonjdk.nexusearch

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.converters.basic.StringConverter
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.sonatype.nexus.rest.index.MIndexerXStreamConfiguratorLightweight
import org.sonatype.nexus.rest.model.SearchNGResponse
import org.sonatype.nexus.rest.model.XStreamConfigurator
import org.sonatype.plexus.rest.xstream.xml.LookAheadXppDriver

import java.util.logging.Logger

/*

https://help.sonatype.com/repomanager3/rest-and-integration-api/search-api


https://repository.sonatype.org/nexus-indexer-lucene-plugin/default/docs/path__lucene_search.html

json api:
https://help.sonatype.com/repomanager3/rest-and-integration-api/search-api

com.intellij.jarFinder.SonatypeSourceSearcher
 */

@CompileStatic
class NexusSearch {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
//    URL url = new URL("https://oss.sonatype.org/service/local/lucene/search?sha1=9b0befc6cc40b80b3d3a685a7cd4657f225177cb")

//        URL url = new URL("https://oss.sonatype.org/service/local/lucene/search?a=org.osgi")

    static SearchNGResponse search(URL url) {
        String text = url.text
        log.info "${text}"
        return parseResult(text)
    }

    static SearchNGResponse parseResult(String text) {
        XStream stream = createXmlXStream()
        MIndexerXStreamConfiguratorLightweight.configureXStream(stream)
        log.info "${text}"
        SearchNGResponse searchNGResponse = stream.fromXML(text) as SearchNGResponse
        return searchNGResponse

    }


    public static XStream createXmlXStream() {
        XStream xstream = new XStream(new LookAheadXppDriver());
        XStreamConfigurator.configureXStream(xstream);
        MIndexerXStreamConfiguratorLightweight.configureXStream(xstream);
        xstream.registerConverter(new StringConverter());
        return xstream;
    }

}
