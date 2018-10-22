package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace;

import java.util.logging.Logger;

@CompileStatic
class AllClasspathAnalysis {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public AddedLocationDetector adder = new AddedLocationDetector();
    public GetClassesFromLocation classesFromLocation;
    public DuplicateClassesDetector duplicateClassesDetector;
    public Map<File, List<JustStackTrace>> dupsFilesMap = [:]
    public Map<String, List<JustStackTrace>> dupsClassMap = [:]
    public Map<File, JustStackTrace> locationMap12 = [:]
    public HashSet<String> classes = [];
    public HashSet<File> nonUsedLocations;
    public volatile UsedByAnalysis usedByAnalysis;

    void addClassesFromCurrentClassLoader() {
        addClassesFromClassLoader(AllClasspathAnalysis.getClassLoader())
    }

    void addClassesFromClassLoader(ClassLoader classLoader) {
        classes.addAll(DumpLoadedClasses.dumpLoadedClassesNames(classLoader))
    }

    UsedByAnalysis createUsedByAnalysis() {
        if (usedByAnalysis == null) {
            usedByAnalysis = new UsedByAnalysis(duplicateClassesDetector)
        }
        return usedByAnalysis;
    }

    void analise() {
        if (classes.size() == 0) {
            throw new Exception("No classes")
        }
        if (adder.locationMap.size() == 0) {
            throw new Exception("No files")
        }
        dupsFilesMap = adder.findDuplicates()
        classesFromLocation = new GetClassesFromLocation()

        adder.locationMap.each { locationMap12.put(it.key, it.value[0]) }
        Map<File, List<String>> files = classesFromLocation.loadClassesOnLocation(adder.getAddedFiles4())
        duplicateClassesDetector = new DuplicateClassesDetector(classes, files);
        duplicateClassesDetector.problemClasses.each { dupsClassMap.put(it.key, getLocations(it.value)) }
        nonUsedLocations = duplicateClassesDetector.getNonUsedLocations()
    }

    List<JustStackTrace> getLocations(List<File> files) {
        return files.collect { locationMap12.get(it) }
    }


    Map<String, List<JustStackTrace>> getFilteredDupsClassMap() {
        return dupsClassMap.findAll { return needShowDupClass(it.key) };
    }

    Collection<String> getFilteredMissesClasses() {
        return new TreeSet<String>(duplicateClassesDetector.missedClasses.findAll { return needShowMissedClass(it) })
    }

    boolean needShowDupClass(String className) {
        if (className.contains('$')) {
            return false;
        }
        String startWithFound = dupClassesIgnore.find { it.length() > 0 && className.startsWith(it) }
        if (startWithFound != null) {
            return false
        }
        return true
    }

    boolean needShowMissedClass(String className) {
        if (className.contains('$')) {
            return false;
        }
        String startWithFound = missedIgnore.find { it.length() > 0 && className.startsWith(it) }
        if (startWithFound != null) {
            return false
        }
        return true
    }

    public List<String> dupClassesIgnore = []

    public List<String> missedIgnore = []

}
