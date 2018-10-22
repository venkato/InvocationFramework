package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.asmow2.usedclasses.UsedClasses
import org.zeroturnaround.zip.ZipEntryCallback
import org.zeroturnaround.zip.ZipUtil;

import java.util.logging.Logger
import java.util.zip.ZipEntry;

@CompileStatic
class UsedByAnalysis {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public DuplicateClassesDetector det;

    public Map<String, List<File>> classToUsedInFiles = [:];
    public Map<String, Set<String>> usesClassesFromClass = [:];
    public Map<File, Set<String>> usesClassesFromLocation = [:];

    UsedByAnalysis(DuplicateClassesDetector det) {
        this.det = det
        init()
    }


    protected void init() {
        det.classesOnLocationUsed.keySet().each { usesClassesFromLocation.put(it, getDependentClasses(it)) }
        usesClassesFromLocation.each { handleRevrseUsed(it.key, it.value) }
    }


    HashSet<File> getUsedByClassesComplex(File location) {
        location = location.getCanonicalFile().getAbsoluteFile()
        List<String> classes12 = det.classesOnLocation.get(location)
        if (classes12 == null) {
            throw new Exception("Location not found ${location}")
        }
        Set<String> classesOnLocation = det.classesOnLocationUsed.get(location)
        HashSet<File> result = []
        if (classesOnLocation != null) {
            classesOnLocation.each {
                List<File> files = classToUsedInFiles.get(it)
                if (files != null) {
                    result.addAll(files)
                }
            }
            result.remove(location)
        }
        return result;
    }

    HashSet<File> getUsedByClassesComplex(Collection<File> locations) {
        locations = locations.collect { it.getCanonicalFile().getAbsoluteFile() }
        HashSet<File> result = new HashSet<>()
        locations.each {
            result.addAll(getUsedByClassesComplex(it))
        }
        result.removeAll(locations)
        return result
    }


//    HashSet<File> getDependentClassesEasyMaven(List<ToFileRef2> locations) {
//        List<File> collect1 = locations.collect { it.resolveToFile() }
//        return getUsedByClassesComplex(collect1)
//    }

    HashSet<String> getDependentClassesEasy(Collection<File> locations) {
        locations = locations.collect { it.getCanonicalFile().getAbsoluteFile() }
        HashSet<String> result = new HashSet<>()
        locations.each {
            List<String> classes12 = det.classesOnLocation.get(it)
            if (classes12 == null) {
                throw new Exception("Location not found ${it}")
            }
            Set<String> classes = usesClassesFromLocation.get(it)
            if (classes != null) {
                result.addAll(classes)
            }
        }
        HashSet<String> result2 = new HashSet<>(result.collect { it.replace('/', '.') })
        locations.each {
            Set<String> classNamesOnLocationUsed = det.classesOnLocationUsed.get(it)
            if (classNamesOnLocationUsed != null) {
                result2.removeAll(classNamesOnLocationUsed)
            }
        }
        result2.retainAll(det.classesNames)
        return result2
    }


    protected void handleRevrseUsed(File location, Set<String> usesInThisLocation) {
        List<String> usesInThisLocation2 = new ArrayList<>(usesInThisLocation)
        usesInThisLocation2.retainAll(det.classesNames);
        usesInThisLocation2.each {
            List<File> usedIn = classToUsedInFiles.get(it)
            if (usedIn == null) {
                usedIn = []
                classToUsedInFiles.put(it, usedIn)
            }
            usedIn.add(location)
        }

    }


//    HashSet<String> getDependentClassesR(List<ToFileRef2> locations) {
//        return getDependentClasses(locations.collect {it.resolveToFile()})
//    }
//


    HashSet<File> getDependentClassesHumanFile(String toAnalize, int maxDepth) {
        return det.convertClassesToLocation(getDependentClassesHuman(toAnalize, maxDepth));
    }

    HashSet<String> getDependentClassesHuman(String toAnalize, int maxDepth) {
        HashSet<String> needed = new HashSet<>()
        HashSet<String> skipped = new HashSet<>()
        getDependentClasses(toAnalize, needed, skipped, maxDepth);
        return needed;
    }

    protected void getDependentClasses(String toAnalize, HashSet<String> needed, HashSet<String> skipped, int maxDepth) {
        if (!det.classesNames.contains(toAnalize)) {
            throw new Exception("Not found : ${toAnalize}")
        }
        needed.add(toAnalize);
        List<File> files = det.classesOnLocationReverse.get(toAnalize);
        if (files == null || files.size() == 0) {
            throw new Exception("not class on location : ${toAnalize}")
        }
        String classNameToPath = convertClassNameToPath(toAnalize)
        File location = files[0]
        HashSet<String> result = new HashSet<>()
        if (location.isDirectory()) {
            File classFile = location.child(classNameToPath)
            if (!classFile.exists()) {
                throw new FileNotFoundException(classFile.getAbsolutePath())
            }
            HashSet<String> usedTypes = UsedClasses.remapClassNoRedefine(classFile.bytes).usedTypes
            result.addAll(usedTypes)
        } else {
            assert location.isFile()
            ZipEntryCallback zipEntryCallback = new ZipEntryCallback() {
                @Override
                void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
                    if (zipEntry.getName() == classNameToPath) {
                        byte[] bytes = inputStream.bytes
                        HashSet usedTypes = UsedClasses.remapClassNoRedefine(bytes).usedTypes
                        result.addAll(usedTypes)
                    }
                }
            }
            ZipUtil.iterate(location, zipEntryCallback)
        }
        HashSet<String> result2 = new HashSet<>(result.collect { it.replace('/', '.') })
        result2.each {
            if (needed.contains(it)) {

            } else {
                if (det.classesNames.contains(it)) {
                    needed.add(it);
                    if (maxDepth != 0) {
                        getDependentClasses(it, needed, skipped, maxDepth - 1);
                    }
                } else {
                    skipped.add(it);
                }
            }
        }
    }

    protected HashSet<String> getDependentClasses(File location) {
        location = location.getCanonicalFile().getAbsoluteFile()
        Set<String> classNamesOnLocationUsed = det.classesOnLocationUsed.get(location)
        if (det.classesOnLocationUsed == null) {
            throw new Exception("location not found ${location}")
        }
        if (classNamesOnLocationUsed.size() == 0) {
            return new HashSet<String>()
        }
        HashSet<String> classNamesOnLocationUsed22 = new HashSet<>(classNamesOnLocationUsed.collect { convertClassNameToPath(it) })
        HashSet<String> result = new HashSet<>()
        if (location.isDirectory()) {
            classNamesOnLocationUsed22.each {
                String fileName = it;
                File classFile = location.child(fileName)
                if (!classFile.exists()) {
                    throw new FileNotFoundException(classFile.getAbsolutePath())
                }
                HashSet<String> usedTypes = UsedClasses.remapClassNoRedefine(classFile.bytes).usedTypes
                usesClassesFromClass.put(fileName, usedTypes)
                result.addAll(usedTypes)

            }
        } else {
            assert location.isFile()
            ZipEntryCallback zipEntryCallback = new ZipEntryCallback() {
                @Override
                void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
                    if (classNamesOnLocationUsed22.contains(zipEntry.getName())) {
                        byte[] bytes = inputStream.bytes
                        HashSet usedTypes = UsedClasses.remapClassNoRedefine(bytes).usedTypes
                        usesClassesFromClass.put(zipEntry.getName(), usedTypes)
                        result.addAll(usedTypes)
                    }
                }
            }
            ZipUtil.iterate(location, zipEntryCallback)
        }
        HashSet<String> result2 = new HashSet<>(result.collect { it.replace('/', '.') })
        result2.removeAll(classNamesOnLocationUsed)
        return result2

    }

    static String convertClassNameToPath(String className) {
        return className.replace('.', '/') + '.class';
    }

}
