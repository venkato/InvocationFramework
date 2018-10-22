package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.json.JsonOutput
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class DuplicateClassesDetector {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public HashSet<String> classesNames;
    public Map<File, List<String>> classesOnLocation = [:];
    public Map<File, Set<String>> classesOnLocationUsed = [:];
    public Map<String, List<File>> classesOnLocationReverse = [:];
    public Map<String, List<File>> problemClasses = [:];
    public List<String> missedClasses = [];
    public Map<String, File> foundClasses = [:];

    DuplicateClassesDetector(HashSet<String> classesNames, Map<File, List<String>> classesOnLocation) {
        this.classesNames = classesNames
        this.classesOnLocation = classesOnLocation
        this.classesOnLocation .each {
            reverseOneEntry(it.getKey(), it.getValue());
        }
        check()
    }


    HashSet<File> convertClassesToLocation(Collection<String> classNames1) {
        HashSet<File> result = new HashSet<>()
        classNames1.each {
            List<File> files = classesOnLocationReverse.get(it)
            result.addAll(files)
        }
        return result;
    }


    HashSet<File> getUsedLocations() {
        Map<File, Set<String>> used = classesOnLocationUsed.findAll { it.value.size() > 0 }
        HashSet<File> result = new HashSet<>()
        result.addAll (used.keySet());
        return result;
    }

    HashSet<File> getNonUsedLocations() {
        HashSet<File> result = new HashSet<>()
        Map<File, Set<String>> unused = classesOnLocationUsed.findAll { it.value.size() == 0 }
        result.addAll(unused.keySet())
        HashSet<File> files2 = new HashSet<File>(classesOnLocation.keySet())
        files2.removeAll(classesOnLocationUsed.keySet())
        result.addAll(files2)
        return result
    }


    protected void reverseOneEntry(File file, List<String> classes) {
        classes.each {
            List<File> files = classesOnLocationReverse.get(it)
            if (files == null) {
                files = []
                classesOnLocationReverse.put(it, files)
            }
            files.add(file)
        }
    }


    protected void check() {
        classesNames.each { handleOneClass(it) }
        classesOnLocation.each {
            HashSet<String> classes = new HashSet<>(it.value)
            classes.retainAll(classesNames)
            classesOnLocationUsed.put(it.key, classes)
        }
    }

    Map<String, List<String>> getClass2FilesMap() {
        Map<String, List<String>> json1 = new TreeMap<>();
        problemClasses.each {
            json1.put(it.key, convertFiles(it.value))
        }
        foundClasses.each {
            json1.put(it.key, [it.value.getAbsolutePath().replace('\\', '/')])
        }
        missedClasses.each {
            json1.put(it, [])
        }
        return json1;
    }

    String getClass2FilesJson(){
        String json = JsonOutput.toJson(getClass2FilesMap());
        json =  JsonOutput.prettyPrint(json);
        return json;
    }

    protected List<String> convertFiles(List<File> files) {
        return files.collect { it.getAbsolutePath().replace('\\', '/') }
    }

    protected void handleOneClass(String oneClass) {
        List<File> files = classesOnLocationReverse.get(oneClass)
        if (files == null) {
            missedClasses.add(oneClass)
//            classesOnLocationReverseUsed.put(oneClass,[])
        } else {
//            classesOnLocationReverseUsed.put(oneClass,files)
            if (files.size() == 1) {
                foundClasses.put(oneClass, files[0])
            } else {
                problemClasses.put(oneClass, files)
            }
        }
    }

//    HashSet<String> getDependentClasses(String className) {
//        File f = foundClasses.get(className);
//        if (f == null) {
//            List<File> files = problemClasses.get(className)
//            f = files[0]
//        }
//        if (f == null) {
//            throw new ClassNotFoundException(className)
//        }
//    }
}
