package net.sf.jremoterun.utilities.nonjdk.classpath.search

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.UserBintrayRepo
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGroovyWithDownloadWise;

import java.util.logging.Logger;

@CompileStatic
class BintraySearch {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    boolean ignoreBadPath = true;


    String urlPrefix = 'https://api.bintray.com/search/file?sha1='

    List<BintraySearchResult> searchBySha1(File file) {
        String hash = ClassPathCalculatorGroovyWithDownloadWise.calcSha1ForFile(file);
        return searchBySha1(hash)
    }

    List<BintraySearchResult> searchBySha1(String sha1) {
        URL u = new URL(urlPrefix + sha1)
        return parseReply(u.text);

    }

    List<BintraySearchResult> parseReply(String text) {
        try {
            List<Map> parse = new JsonSlurper().parseText(text) as List
            return parse.collect { parseElement((Map) it) }.findAll { it != null }
        } catch (Exception e) {
            throw new Exception("Failed parse : ${text}", e)
        }
    }

    String getEl(Map<String, String> allEls, String elName) {
        String elResult = allEls.get(elName)
        if (elResult == null) {
            throw new Exception("${elName} not found : ${allEls}")
        }
        return elResult
    }

    BintraySearchResult parseElement(Map<String, String> el) {
        BintraySearchResult bintraySearchResult = new BintraySearchResult()
        String repo1 = getEl(el, 'repo');
        String owner1 = getEl(el, 'owner');
        String path1 = getEl(el, 'path');
        bintraySearchResult.bintrayRepo = new UserBintrayRepo(owner1 + '/' + repo1);
        //org/jetbrains/intellij/deps/completion/ranking/java/0.0.3/java-0.0.3.jar
        List<String> pathTokenize = path1.tokenize('/')
        if (pathTokenize.size() < 3) {
            if (ignoreBadPath) {
                log.info "bad path : ${pathTokenize} from ${el}"
                return null
            }
            throw new Exception("bad path : ${pathTokenize}")
        }
        pathTokenize.remove(pathTokenize.size() - 1)
        String version1 = pathTokenize.remove(pathTokenize.size() - 1)
        String artifact1 = pathTokenize.remove(pathTokenize.size() - 1)
        String groupId1 = pathTokenize.join('.')
        bintraySearchResult.mavenId = new MavenId(groupId1, artifact1, version1)
        bintraySearchResult.rawResult = el;
        return bintraySearchResult;
    }

}
