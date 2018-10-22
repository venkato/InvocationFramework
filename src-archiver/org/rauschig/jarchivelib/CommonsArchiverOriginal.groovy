package org.rauschig.jarchivelib

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.ArchiveOutputStream;

import java.util.logging.Logger;

@CompileStatic
public class CommonsArchiverOriginal extends CommonsArchiver{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public CommonsArchiverOriginal(ArchiveFormat archiveFormat) {
        super(archiveFormat)
    }

    @Override
    File create(String archive, File destination, File... sources) throws IOException {
        return super.create(archive, destination, sources)
    }

    @Override
    File create(String archive, File destination, File source) throws IOException {
        return super.create(archive, destination, source)
    }

    @Override
    ArchiveFormat getArchiveFormat() {
        return super.getArchiveFormat()
    }

    @Override
    void extract(File archive, File destination) throws IOException {
        super.extract(archive, destination)
    }

    @Override
    void extract(InputStream archive, File destination) throws IOException {
        super.extract(archive, destination)
    }

    @Override
    ArchiveStream stream(File archive) throws IOException {
        return super.stream(archive)
    }

    @Override
    String getFilenameExtension() {
        return super.getFilenameExtension()
    }

    @Override
    protected File createNewArchiveFile(String archive, String extension, File destination) throws IOException {
        return super.createNewArchiveFile(archive, extension, destination)
    }

    @Override
    protected ArchiveInputStream createArchiveInputStream(File archive) throws IOException {
        return super.createArchiveInputStream(archive)
    }

    @Override
    protected ArchiveInputStream createArchiveInputStream(InputStream archive) throws IOException {
        return super.createArchiveInputStream(archive)
    }

    @Override
    protected ArchiveOutputStream createArchiveOutputStream(File archiveFile) throws IOException {
        return super.createArchiveOutputStream(archiveFile)
    }

    @Override
    protected void assertExtractSource(File archive) throws FileNotFoundException, IllegalArgumentException {
        super.assertExtractSource(archive)
    }

    @Override
    protected void writeToArchive(File[] sources, ArchiveOutputStream archive) throws IOException {
        super.writeToArchive(sources, archive)
    }

    @Override
    protected void writeToArchive(File parent, File[] sources, ArchiveOutputStream archive) throws IOException {
        super.writeToArchive(parent, sources, archive)
    }

    @Override
    protected void createArchiveEntry(File file, String entryName, ArchiveOutputStream archive) throws IOException {
        super.createArchiveEntry(file, entryName, archive)
    }
}
