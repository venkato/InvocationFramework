package net.sf.jremoterun.utilities.nonjdk.sfdownloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.TwoResult
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.logging.Logger

@CompileStatic
class SfLinkBuilder {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static URL page = new URL("https://sourceforge.net/p/forge/documentation/Mirrors/")

    static String buildEnumValues() {
        List<TwoResult<String, String>> enumValues = downloadAndParse()
        List<String> strings = enumValues.collect { " ${it.first} ( '${it.second}' ) , ".toString() }
        String result = strings.join('\n')
        return result
    }

    static List<TwoResult<String, String>> downloadAndParse() {
        Document document = Jsoup.parse(page.text)
        Element tableEl = document.select("table").get(0);
        Elements trs = tableEl.select("tr");
        assert trs.size() > 5
        List<TwoResult<String, String>> enumValues = trs.collect { handleTr(it) }
        enumValues = enumValues.findAll { it != null }
        assert enumValues.size() > 5
        return enumValues
    }

    static TwoResult<String, String> handleTr(Element trEl) {
        List l = trEl.select('td');
        if (l.size() != 4) {
            return null;
        }
        Element dnsEL = l[1]
        String dnsName = dnsEL.text();

        Element descEL = l[3]
        String desc = descEL.text()
        return new TwoResult<String, String>(dnsName, desc)

    }


}
