package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.io.FileVisitResult
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities3
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.AddFilesWithSourcesI
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.BinaryWithSourceI
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenFileType2
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains

import java.util.logging.Logger

@CompileStatic
abstract class AddFileWithSources extends AddFilesToClassLoaderGroovy implements AddFilesWithSourcesI {
    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

    MavenCommonUtils mavenCommonUtilsForSources = new MavenCommonUtils()
    List<File> addedSourceFiles = []

    static String eclipseSourceFileSuffix = ".source_"

//    volatile FileTransformer fileTransformer = new FileTransformer();

    boolean downloadSources = false;

    List<String> srcDirs = ['src', 'java', 'main']

    List<String> ignoredDirPrefix = ["resource", "classes", "build", "target"]

    abstract void addLibraryWithSource(File binary, List<File> source) throws Exception;

    abstract void addSourceFImpl(File source) throws Exception;

    abstract void addSourceS(String source) throws Exception;

    AddFileWithSources() {
        mavenCommonUtilsForSources.fileType = MavenFileType2.source.fileSuffix
    }

    @Override
    void addBinaryWithSource(BinaryWithSourceI fileWithSource) throws Exception {
        addLibraryWithSourceCount(fileWithSource.resolveToFile(), fileWithSource.resolveSource());
    }

    void addLibraryWithSourceCount(File binary, List<File> source) throws Exception {
        JrrUtilities3.checkFileExist(binary)
        addLibraryWithSource(binary, source);
        getAddedFiles2().add(binary);
        if (source != null) {
            source.each {
                JrrUtilities3.checkFileExist(it)

            }
            addedSourceFiles.addAll(source);
        }
    }

    @Override
    void addSourceF(File source) throws Exception {
        JrrUtilities3.checkFileExist(source)
        addSourceFImpl(source)
        addedSourceFiles.add(source)
    }

    @Override
    void addFileImpl(File file) throws Exception {
        if (file.isFile()) {
            addFileSourceHelper(file, null)
        } else {
            addFolderAndTryFindSourceDir(file)
        }

    }

    @Override
    void addSourceM(MavenId mavenId) {
        File source = mavenCommonUtilsForSources.findMavenOrGradle(mavenId)
        if (source == null) {
            source = onMissingMavenSource(mavenId)
        }
        if (source == null) {
            throw new FileNotFoundException("Failed find source for ${mavenId}");
        }
//        if (fileTransformer.acceptFile(null, source, null)) {
        addSourceF(source)
//        }
    }

    // why it is needed
    void addFileSourceHelper(File binary, File source) {
        if (binary != null) {
            JrrUtilities3.checkFileExist(binary)
        }
        if (source != null) {
            JrrUtilities3.checkFileExist(source)
        }
        String absolutePath = null;
        if (binary != null) {
            absolutePath = binary.absoluteFile.canonicalPath.replace('\\', '/')
        }
//        if (fileTransformer.acceptFile(binary, source, absolutePath)) {
        if (binary == null) {
            addSourceF(source)
        } else {
            List source3 = []
            if(source!=null){
                source3.add(source)
            }
            addLibraryWithSource(binary, source3);
            getAddedFiles2().add(binary);
            if(source!=null) {
                addedSourceFiles.add(source)
            }
        }
//        }
    }

    static Collection calcEclipseClassPath(File eclipsePluginDir) throws Exception {
        JrrUtilities3.checkFileExist(eclipsePluginDir)
        List<File> listFiles = eclipsePluginDir.listFiles().toList();
        Map<String, File> fileMap = new HashMap();
        for (File file : listFiles) {
            String name = file.getName();
            if (file.isFile() && name.contains(eclipseSourceFileSuffix)) {
                fileMap.put(name.replace(eclipseSourceFileSuffix, '_'), file);
            }
        }
        listFiles = listFiles.findAll { it.isFile() }.findAll { !it.getName().contains(".source_") }.findAll {
            it.name.endsWith('.jar')
        };
        return listFiles.collect {
            String name = it.getName();
            File source = fileMap.get(it.name);
            if (source == null) {
                return it;
            } else {
                BinaryWithSource binaryWithSource = new BinaryWithSource(it, source)
                return binaryWithSource
            }
            return null;
        }.findAll { it != null }
    }


    @Override
    void addM(MavenId artifact) throws IOException {
        File file = resolveMavenId(artifact);
        if (file == null) {
            throw new FileNotFoundException(artifact.toString());
        }
        File source = mavenCommonUtilsForSources.findMavenOrGradle(artifact);
//        log.info "artifact : ${artifact} ${source}"
        if (source == null) {
            source = onMissingMavenSource(artifact);
        }
        if (source == null) {
            log.info("no source for maven : " + artifact);
        }
        addFileSourceHelper(file, source);
    }

    File onMissingMavenSource(MavenId mavenId) {
        if (!downloadSources) {
            log.info("no source for ${mavenId}")
            return null
        }
        try {
            MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver.downloadSource(mavenId)
            File sourceFile = mavenCommonUtilsForSources.findMavenOrGradle(mavenId)
//            log.info "source for ${mavenId} found ${sourceFile != null}"
            return sourceFile
        } catch (Throwable e) {
            log.info "failed donwload source for ${mavenId} ${e}"
            return null
        }

    }

    void addFolderAndTryFindSourceDir(File file) {
        File findSourceDirForBinaryCLass = findSourceDirForBinaryClass(file);
        if (file.equals(findSourceDirForBinaryCLass)) {
            findSourceDirForBinaryCLass = null;
        }
        if (findSourceDirForBinaryCLass == null) {
            log.info("can't find sources for " + file.getAbsolutePath());
            addFileSourceHelper(file, null);
        } else {
            log.info("src folder " + findSourceDirForBinaryCLass.getAbsolutePath() + " for " + file);
            addFileSourceHelper(file, findSourceDirForBinaryCLass);
        }
    }

    File findSourceDirForBinaryClass(File file) {
        if (file.isFile()) {
            return null
        }
        File[] listFiles = file.listFiles();
        if (listFiles.length == 0) {
            return null;
        }
        File sampleClassFile2 = findSampleFile(file);
        if (sampleClassFile2 == null) {
            log.info "sample class file not found in ${file}"
            return null
        }
        String sampleClassFile = mavenCommonUtils.getPathToParent(file, sampleClassFile2.parentFile)
//        sampleClassFile = sampleClassFile.substring(0, sampleClassFile.length() - '.class'.length() + 1)
        log.fine "sample file : ${sampleClassFile}"
        File result = findSourceFolderUp(file.parentFile, 2, sampleClassFile)
        if (file == result) {
            log.info "source equals dest ${file}"
            return null;
        }
        return result
    }

    File findSampleFile(File binaryDir) {
        File foundFile;
        binaryDir.traverse {
            if (it.name.endsWith(".class") && !it.name.contains('$')) {
                foundFile = it
                return FileVisitResult.TERMINATE;
            }
        };
        return foundFile;
    }

    File findSourceFolderUp(File dir, int upDepth, String classFile) {
        if (dir == null) {
            log.fine "dir is null"
            return null
        }
        log.fine "checking ${dir}"
        List<File> listFiles2 = dir.listFiles().toList().findAll {
            it.isDirectory() && srcDirs.contains(it.name)
        };
        for (File file2 : listFiles2) {
            File fondSourceFolder = findSourceFolderDown(file2, 2, classFile);
            if (fondSourceFolder != null) {
                return fondSourceFolder;
            }
        }
        log.fine "upDepth : ${upDepth}"
        if (upDepth <= 0) {

            return null
        }
        //upDepth--;
        return findSourceFolderUp(dir.parentFile, upDepth - 1, classFile)
    }


    File findSourceFolderDown(File dir, int remainDepth, String sampleSubFolder) {
        log.fine "checking ${dir}"
        if (checkIfDirOk(dir, sampleSubFolder)) {
            return dir;
        }
        if (remainDepth <= 0) {
            return null
        }
        List<File> listFiles2 = dir.listFiles().toList().findAll { it.isDirectory() };
        for (File file2 : listFiles2) {
            if (ignoredDirPrefix.find { file2.name.startsWith(it) } != null) {

            } else {

                File fileee = findSourceFolderDown(file2, remainDepth - 1, sampleSubFolder);
                if (fileee != null) {
                    return fileee;
                }
            }
        }
        return null;

    }

    boolean checkIfDirOk(File file, String child) {
        File f = new File(file, child)
        if (f.exists() && f.directory) {
            File[] listFiles = f.listFiles();
            for (File file2 : listFiles) {
                if (file2.getName().endsWith(".class")) {
                    log.fine "ignore : found class file : ${file2}"
                    return false;
                }
            }
            return true
        }
        return false
    }


    @Override
    void addSourceGenericAll(List objects) {
        if (objects.size() == 0) {
            throw new IllegalArgumentException("collection is empty")
        }
        objects.each {
            try {
                addSourceGeneric(it)
            }catch (Throwable e){
                logAdder.info "failed add ${it} ${e}"
                throw e
            }
        }
    }

    @Override
    void addSourceGeneric(Object object) {
        switch (object) {
            case { object == null }:
                throw new NullPointerException("object is null")
            case { object instanceof Collection }:
                throw new IllegalArgumentException("Collection : ${object}")
            case { object instanceof String }:
                String s = (String) object;
                addSourceS(s)
                break;
            case { object instanceof File }:
                File f = (File) object;
                addSourceF(f)
                break;

            case { object instanceof MavenId }:
                MavenId m = (MavenId) object;
                addSourceM(m)
                break;
            case { object instanceof MavenIdContains }:
                MavenIdContains mavenId3 = object as MavenIdContains
                addSourceGeneric mavenId3.getM()
                break;
            default:
                CustomObjectHandler customObjectHandler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
                if (customObjectHandler == null) {
                    throw new IllegalArgumentException("${object}");
                } else {
                    addSourceF customObjectHandler.resolveToFile(object)
                }

        // throw new UnsupportedOperationException("Not supported : ${object}")
        }

    }
}
