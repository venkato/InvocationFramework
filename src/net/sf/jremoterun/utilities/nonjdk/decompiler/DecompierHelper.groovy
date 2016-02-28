package net.sf.jremoterun.utilities.nonjdk.decompiler

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider
import org.jetbrains.java.decompiler.main.extern.IResultSaver
import org.jetbrains.java.decompiler.util.InterpreterUtil

import java.util.jar.Manifest
import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@CompileStatic
class DecompierHelper implements IBytecodeProvider, IResultSaver {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    byte[] getBytecode(String externalPath, String internalPath) throws IOException {
//        log.info "${externalPath} ${internalPath}"
        File file = new File(externalPath);
        if (internalPath == null) {
            return InterpreterUtil.getBytes(file);
        } else {
            ZipFile archive = new ZipFile(file)
            try {
                ZipEntry entry = archive.getEntry(internalPath);
                if (entry == null) throw new IOException("Entry not found: " + internalPath);
                return InterpreterUtil.getBytes(archive, entry);
            } finally {
                archive.close()
            }
        }
    }

    @Override
    void saveFolder(String path) {
        log.info "UnsupportedOperationException"
        throw new UnsupportedOperationException()
    }

    @Override
    void copyFile(String source, String path, String entryName) {
        log.info "UnsupportedOperationException"
        throw new UnsupportedOperationException()
    }

    @Override
    void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        log.info "UnsupportedOperationException"
        throw new UnsupportedOperationException()
    }

    @Override
    void createArchive(String path, String archiveName, Manifest manifest) {
        log.info "UnsupportedOperationException"
        throw new UnsupportedOperationException()
    }

    @Override
    void saveDirEntry(String path, String archiveName, String entryName) {
        log.info "UnsupportedOperationException"
        throw new UnsupportedOperationException()
    }

    @Override
    void copyEntry(String source, String path, String archiveName, String entry) {
        log.info "UnsupportedOperationException"
        throw new UnsupportedOperationException()
    }

    @Override
    void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
        log.info "UnsupportedOperationException"
        throw new UnsupportedOperationException()
    }

    @Override
    void closeArchive(String path, String archiveName) {
        log.info "UnsupportedOperationException"
        throw new UnsupportedOperationException()
    }
}
