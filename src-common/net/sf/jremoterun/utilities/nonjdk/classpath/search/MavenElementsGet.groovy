package net.sf.jremoterun.utilities.nonjdk.classpath.search

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenVersionComparator
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements;

import java.util.logging.Logger;

@CompileStatic
class MavenElementsGet {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<String> getElements(URL url, boolean needFiles) {
        long startTime = System.currentTimeMillis()
        Document doc = Jsoup.connect(url.toString()).get();
        long duration = System.currentTimeMillis() - startTime
        checkDuration(url, duration)
        Elements newsHeadlines = doc.select("a");
        if (newsHeadlines.size() == 0) {
            throw new Exception("a elements not found on ${url}")
        }
        List<String> collect1 = newsHeadlines.collect { getRef(it,needFiles) }
        collect1 = collect1.findAll { it != null }
        if (collect1.size() == 0) {
            throw new Exception("No good a elements found on ${url}")
        }
        return collect1;
    }

    Map<String, String> findLatestVersionForArtifacts(URL url) {
        String parentUrl = url.toString()
        if (!parentUrl.endsWith('/')) {
            parentUrl += '/'
        }
        Map<String, String> result = [:]
        List<String> elements = getElements(url,false)
        elements.each {
            URL url12 = new URL(parentUrl + it + '/');
            String latestVer = findLatestVersionForArtifact(url12);
            result.put(it, latestVer);
        }
        return result
    }

    void checkDuration(URL url, long duration) {
        if (duration > 5000) {
            log.info "long duration ${duration / 1000} sec for ${url}"
        }
    }


    String findLatestVersionForArtifact(URL url) {
        List<String> elements = getElements(url,false)
        return findLatestVersionFromList(elements)
    }

    String getRef(Element element, boolean needFiles) {
        List<Node> nodes = element.childNodes()
        if (nodes.size() != 1) {
            return null
        }
        Node node = nodes.get(0)
        if (!(node instanceof TextNode)) {
            return false
        }
        TextNode tn = (TextNode) node;
        String text1 = tn.text()
        final String textOrig = text1
        text1 = text1.trim()
        boolean needed = isTextNeedInclude(tn, text1, needFiles)
        if (!needed) {
            return null
        }
        if (text1.endsWith('/')) {
            text1 = text1.substring(0, text1.length() - 1)
        }
        boolean isGood = isTextGood(text1)
        if (!isGood) {
            throw new Exception("Bad text : ${textOrig}")
        }
        return text1
    }

    boolean isTextNeedInclude(TextNode tn, String text, boolean needFiles) {
        if (text.contains('Parent directory')) {
            return false
        }
        if (text.contains('..')) {
            return false
        }
        if (needFiles) {
            return !text.endsWith('/')
        }
        return text.endsWith('/')
    }

    boolean isTextGood(String text1) {
        boolean isBad = text1.contains('/') || text1.contains(' ') || text1.contains('\n') || text1.contains('"') || text1.contains("'")
        return !isBad
    }


    String findLatestVersionFromList(List<String> els) {
        MavenVersionComparator comparator = new MavenVersionComparator()
        String ver = els.first()
        els.each {
            int res = comparator.isOverrideMavenId(ver, it)
            if (res > 0) {
                ver = it
            }
        }
        return ver

    }

}
