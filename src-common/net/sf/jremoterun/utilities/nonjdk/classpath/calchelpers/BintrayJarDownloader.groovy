package net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.TwoResult
import net.sf.jremoterun.utilities.nonjdk.classpath.search.BintraySearch
import net.sf.jremoterun.utilities.nonjdk.classpath.search.BintraySearchResult
import net.sf.jremoterun.utilities.nonjdk.ivy.ManyReposDownloaderImpl

import java.util.logging.Logger;

@CompileStatic
class BintrayJarDownloader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ManyReposDownloaderImpl mavenDownloader = new ManyReposDownloaderImpl(null);

    public BintraySearch bintraySearch = new BintraySearch()
    public List<File> files;
    public List<TwoResult<BintraySearchResult, Throwable>> exceptions = [];
    public List<BintraySearchResult> resolved = [];

    void doStuff() {
        readUnresolvedFiles()
        List<BintraySearchResult> all1 = files.collect { bintraySearch.searchBySha1(it) }.collect { convert1(it) }


        all1.each {
            downloadResult(it)
            resolved.add(it)
        }
    }

    void readUnresolvedFiles() {
        Map<File, String> filesMap = ClassPathCalculatorGroovyWithDownloadWise.readNoMavenIdsFile()
        files = filesMap.keySet().toList()
        files = files.findAll { needResolveFiles(it) }
    }

    boolean needResolveFiles(File f) {
        return f.exists();
    }


    BintraySearchResult convert1(List<BintraySearchResult> list1) {
        if (list1.size() == 1) {
            return list1[0]
        }
    }



    void downloadResult(BintraySearchResult result) {
        try {
            mavenDownloader.resolveAndDownloadDeepDependencies(result.mavenId, true, false, result.bintrayRepo)
        } catch (Exception e) {
            onException(result, e)
        }
    }

    void onException(BintraySearchResult result, Throwable e) {
        exceptions.add(new TwoResult<BintraySearchResult, Throwable>(result, e))
    }


}
