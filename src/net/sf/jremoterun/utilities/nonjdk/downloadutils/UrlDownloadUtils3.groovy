package net.sf.jremoterun.utilities.nonjdk.downloadutils

import com.github.junrar.Junrar
//import com.github.junrar.Archive
//import com.github.junrar.extract.ExtractArchive
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.git.UrlSymbolsReplacer
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SfLink
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.UrlProvided
import org.apache.commons.io.IOUtils
import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory
import org.rauschig.jarchivelib.FileType
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class UrlDownloadUtils3 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static String gitDirSuffix = "git_download_"
    private static String fileSuffix = "file_"

    static UrlDownloadUtils3 urlDownloadUtils;

    MavenCommonUtils mcu = new MavenCommonUtils();

    public File tmpDir = new File(MavenDefaultSettings.mavenDefaultSettings.jrrDownloadDir, "tmp")

    public File unzipDir = new File(MavenDefaultSettings.mavenDefaultSettings.jrrDownloadDir, "unzip");


    static UrlDownloadUtils3 getUrlDownloadUtils() {
        if (urlDownloadUtils == null) {
            urlDownloadUtils = new UrlDownloadUtils3();
        }
        return urlDownloadUtils;
    }

    UrlDownloadUtils3() {
        init()
    }

    void init() {
        tmpDir.mkdir()
        unzipDir.mkdir()

    }


    File downloadUrlAndUnzip(SfLink url) {
        File f = url.resolveToFile()
        return unzip(f, 'sf/' + url.path)
    }


    File downloadUrlAndUnzip(UrlProvided url) {
        return downloadUrlAndUnzip(url.convertToUrl())
    }

    File downloadUrlAndUnzip(URL url) {
        File f = downloadUrl(url)
        return unzip(f, url.toString())
    }

    File unzip(File zipFile, String fileSuffix) {
        File f2 = new File(unzipDir, UrlSymbolsReplacer.replaceBadSymbols(fileSuffix))
        if (f2.exists()) {
            log.info "already unarchived : ${f2}"
            return f2;
        }
        String fileName = zipFile.getName()
        if (fileName.endsWith('.rar')) {
            // https://github.com/edmund-wagner/junrar
            // https://github.com/jukka/java-unrar
            // https://github.com/Albertus82/JUnRAR
            // https://github.com/asm-labs/junrar
            Junrar.extract(zipFile, f2);
            return f2
        }
        FileType fileType = FileType.get(zipFile);
        if (fileType == FileType.UNKNOWN) {
            return zipFile;
        }
        Archiver archiver = ArchiverFactory.createArchiver(zipFile)

        archiver.extract(zipFile, f2)
        if(!f2.exists()){
            throw new Exception("failed unzip : ${zipFile}")
        }
        return f2
    }


    File findFreeFile() {
        int i = 10
        while (i < 100) {
            i++;
            File tmpGitDir = new File(tmpDir, fileSuffix + "${i}")
            if (!tmpGitDir.exists()) {
                return tmpGitDir;
            }
        }
        throw new Exception("can't find free file in ${tmpDir}")
    }

    File downloadUrl(URL url) {
        File f2 = mcu.buildDownloadUrl(url);
        if (f2.exists()) {
            log.info("already downloaded ${url}")
            return f2
        }
        log.info "downloading ${url} ..."
        File f = findFreeFile();
        if (f.exists()) {
            assert f.delete()
        }
        BufferedOutputStream out = f.newOutputStream()
        try {
            BufferedInputStream ins = url.newInputStream()
            IOUtils.copyLarge(ins, out)
            ins.close()
            log.info "downloaded ${url}"
        } finally {
            out.flush()
            out.close()
        }

        f2.parentFile.mkdirs()
        assert f2.parentFile.exists()
        assert f.renameTo(f2)
        return f2
    }
}
