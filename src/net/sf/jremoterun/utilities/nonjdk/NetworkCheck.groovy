package net.sf.jremoterun.utilities.nonjdk

import junit.framework.TestCase;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class NetworkCheck extends TestCase{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    void testCheckGithub(){
        URL url = new URL("https://github.com/")
//        String text = url.text
//        assert text.contains("GitHub, Inc.")
        Document parse = Jsoup.parse(url, 10_1000)
        Elements els = parse.select("h3")

        assert els.first()!=null
    }

    void testCheckMaven(){
        MavenId sample = new MavenId("log4j:log4j:1.2.17");
        MavenCommonUtils mavenCommonUtils = new MavenCommonUtils()
        String suffix = mavenCommonUtils.buildMavenPath(sample).replace('.jar','.pom')
        URL url = new URL(mavenCommonUtils.mavenDefaultSettings.mavenServer + '/' + suffix);
        String text = url.text;
        assert text.contains("<artifactId>log4j</artifactId>")

    }

    void testCheckMavenDownload(){
        IvyDepResolver2.setDepResolver()
        MavenId sample = new MavenId("log4j:log4j:1.2.17");
        MavenDependenciesResolver dependenciesResolver = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
        assert dependenciesResolver!=null
        dependenciesResolver.resolveAndDownloadDeepDependencies(sample,false,false);
    }



    void testCheckAll(){
        testCheckGithub()
        testCheckMaven()
        testCheckMavenDownload()
    }


}
