package net.sf.jremoterun.utilities.nonjdk.sfdownloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.git.CloneGitRepo4
import net.sf.jremoterun.utilities.nonjdk.git.UrlSymbolsReplacer

import java.util.logging.Logger

@CompileStatic
class SourceForgeDownloader {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File baseDir;

//    https://netix.dl.sourceforge.net/project/pyscripter/PyScripter-v3.3/PyScripter-v3.3.2-Setup.exe

    SourceForgeDownloader(File baseDir) {
        this.baseDir = baseDir
    }

    File download(SfLink sfLink) {
        String path2 = UrlSymbolsReplacer.replaceBadSymbols(sfLink.path)
        File f = new File(baseDir, path2)
        if (f.exists()) {
            log.info "sf link ${sfLink} already downloaded"
            return f
        }
        log.info "downloading ${sfLink} ..."
        f.parentFile.mkdirs()
        assert f.parentFile.exists()

        Servers.all.find {
            try {
                download(it.name(), sfLink.path, f)
                log.info "downloaded ${sfLink} from ${it}"
                return true
            }catch (Exception e){
                log.info("failed download from ${it}",e)
                return false
            }
        }
        if(f.exists()){
            return f
        }
        throw new IOException("failed download : ${sfLink}")
    }


    File download(String dnsName, String path, File f) {
        URL url = buildUrl(dnsName, path)
        byte[] bytes = url.bytes
        f.bytes = bytes
        return f
    }

    static URL buildUrl(String dnsName, String path) {
        return new URL("https://${dnsName}.dl.sourceforge.net/project/${path}")
    }


}
