package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.CommonsArchiverOriginal
import org.rauschig.jarchivelib.IOUtils;

import java.util.logging.Logger;

@CompileStatic
class JrrCommonsArchiver extends CommonsArchiverOriginal {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int maxErrorFiles = 10;
    public long processedFiles = 0;
    public long uncompressedFileSize = 0;
    public boolean removeArchiveOnError = true;
    public boolean checkOnZeroFiles = true;
    public List<File> errorFiles = []
    public List<File> ignoreFiles = []
    // path separator is /
    public List<File> ignoreFilesFiles = []
    public List<String> ignoreFilesPattern = []
    public List<String> ignoreFilesStartWithPattern = []
    public File destinationFile;
    public volatile File lastFile;
    public volatile boolean needStop = false;

    JrrCommonsArchiver(ArchiveFormat archiveFormat) {
        super(archiveFormat)
    }


    @Override
    File create(String archive, File destinationDir, File... sources) throws IOException {
        try {
            File result =  super.create(archive, destinationDir, sources)
            if (checkOnZeroFiles && processedFiles == 0) {
                throw new Exception('No files in archive')
            }
            return result;
        } catch (Throwable e) {
            if (removeArchiveOnError && destinationFile != null) {
                destinationFile.delete()
            }
            throw e;
        }
    }

    @Override
    protected File createNewArchiveFile(String archive, String extension, File destination) throws IOException {
        destinationFile = super.createNewArchiveFile(archive, extension, destination)
        return destinationFile;
    }

    @Override
    protected ArchiveOutputStream createArchiveOutputStream(File archiveFile) throws IOException {
        return super.createArchiveOutputStream(archiveFile)
    }

    protected static TarArchiveOutputStream createArchiveOutputStreamTarBig(File archiveFile) throws IOException {
        TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(archiveFile));
        archiveOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
        archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
        return archiveOutputStream;
    }

    @Override
    protected void writeToArchive(File parent, File[] sources, ArchiveOutputStream archive) throws IOException {
        if(needStop){
            throw new Exception('stop requested')
        }
        for (File source : sources) {
            lastFile = source;
            String relativePath = IOUtils.relativePath(parent, source);
            boolean needed = isNeedWriteFile(source, relativePath, archive)
            if (needed) {
                createArchiveEntry(source, relativePath, archive);
                if (source.isDirectory()) {
                    File[] files = source.listFiles()
                    writeToArchive(parent, files, archive);
                }
            }
        }
    }

    boolean isNeedWriteFile(File file, String relativePath, ArchiveOutputStream archive) {
        boolean needAccept = true
        if(ignoreFilesFiles.contains(file)){
            needAccept = false
        }
        if(needAccept) {
            relativePath = relativePath.replace('\\', '/')
            String findIgnore1 = ignoreFilesPattern.find { relativePath.contains(it) }
            needAccept = findIgnore1 == null;
            if (needAccept) {
                String findIgnore2 = ignoreFilesStartWithPattern.find { relativePath.startsWith(it) }
                needAccept = findIgnore2 == null
            }
        }
        if (!needAccept) {
            ignoreFiles.add(file);
            log.info "ignoring : ${relativePath} ${file}"
        }
        return needAccept;
    }

    @Override
    protected void createArchiveEntry(File file, String entryName, ArchiveOutputStream archive) throws IOException {
        if(needStop){
            throw new Exception('stop requested')
        }
        lastFile = file;
        ArchiveEntry entry = archive.createArchiveEntry(file, entryName);
        // TODO #23: read permission from file, write it to the ArchiveEntry
        archive.putArchiveEntry(entry);

        if (!entry.isDirectory()) {
            FileInputStream input = null;
            try {
                input = new FileInputStream(file);
                uncompressedFileSize += IOUtils.copy(input, archive);
                processedFiles++;
            } catch (Throwable e) {
                //TODO write dummy bytes, so number of written bytes  matches file size
                onError(e, file, entryName, archive)
            } finally {
                IOUtils.closeQuietly(input);
            }

        }

        archive.closeArchiveEntry();
    }


    void onError(Throwable e, File file, String entryName, ArchiveOutputStream archive) {
        log.info("${entryName} ${file}", e)
        if (errorFiles.size() > maxErrorFiles) {
            throw e
        }
        errorFiles.add(file);
    }

}
