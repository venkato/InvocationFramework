package net.sf.jremoterun.utilities.nonjdk.idea;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.nonjdk.classpath.MavenRepositoriesEnum
import net.sf.jremoterun.utilities.nonjdk.ivy.IvyDepResolver3
import net.sf.jremoterun.utilities.nonjdk.ivy.ManyReposDownloaderImpl
import org.apache.ivy.core.report.ResolveReport
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements;

import java.util.logging.Logger;

@CompileStatic
class IdeaMavenRepoParser {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static URL ideaRepo = new URL(MavenRepositoriesEnum.jetbrainsIdea.url)

    public String searchAllWithSources = 'a[href$=-sources.jar]'
    public String searchAllPoms = 'a[href$=.pom]'
    public String defaultSearchPattern = searchAllPoms
    public String pageText;
    public List<MavenId> foundMavenIds;

    IdeaMavenRepoParser() {
        this(ideaRepo.text)
    }

    IdeaMavenRepoParser(String pageText) {
        this.pageText = pageText
    }

    static MavenIdAndRepo buildIdeaSourcesMavenId(String ideaVersion) {
        MavenId m = new MavenId('com.jetbrains.intellij.idea', 'ideaIC', ideaVersion);
        return new MavenIdAndRepo(m, MavenRepositoriesEnum.jetbrainsIdea)
    }

    List<MavenId> findCurrentVersions() {
        String extended = CurrentIdeaVersionUtils.findCurrentIdeaVersionExtended()
        return foundMavenIds.findAll { it.version == extended }
    }

    /**
     * @param ideaVersion like 2020.1
     */
    static File downloadIdeaSource(String ideaVersion) {
        if (ideaVersion == null) {
            ideaVersion = CurrentIdeaVersionUtils.findCurrentIdeaVersion()
        }
        IvyDepResolver3 repo = ManyReposDownloaderImpl.createRepo(MavenRepositoriesEnum.jetbrainsIdea) as IvyDepResolver3
        MavenId m = new MavenId('com.jetbrains.intellij.idea', 'ideaIC', ideaVersion);
        ResolveReport rr = repo.downloadCustomPackage(m, IvyDepResolver3.sourcesS)
        List<File> files = repo.extractFilesFromReport(rr)
        if (files.size() == 0) {
            throw new FileNotFoundException("failed download source for ${m}")
        }
        return files[0];
    }

    Elements findEls(Document doc1) {
        return doc1.select(defaultSearchPattern)
    }

    void findMavenIds() {
        Document doc = Jsoup.parse(pageText);
        Elements els = findEls(doc)
        String repoS = ideaRepo.toString();
        if (!repoS.endsWith('/')) {
            repoS += '/'
        }
        List<String> ll = els.collect { it.attr('href') }.collect { it.replace(repoS, '') }
        foundMavenIds = ll.collect { buildId(it) }
    }

    MavenId buildId(String s) {
        List<String> tokenize1 = s.tokenize('/')
        tokenize1.remove(tokenize1.size() - 1);
        String version = tokenize1.remove(tokenize1.size() - 1);
        String artifact = tokenize1.remove(tokenize1.size() - 1);
        String group = tokenize1.join('.');
        return new MavenId(group, artifact, version)
    }

    List<MavenIdAndRepo> convertMavenIdsToRepo(List<MavenId> mavenIds) {
        List<MavenIdAndRepo> mavenIdAndRepos = mavenIds.collect {
            new MavenIdAndRepo(it, MavenRepositoriesEnum.jetbrainsIdea)
        }
        return mavenIdAndRepos
    }

}
