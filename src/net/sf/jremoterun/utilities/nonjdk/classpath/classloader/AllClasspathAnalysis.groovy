package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorAbstract
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorWithAdder
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.AddDirectoryWithFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerImpl
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace
import org.apache.commons.collections4.MapUtils

import java.util.logging.Logger;

@CompileStatic
class AllClasspathAnalysis {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public AddedLocationDetector adder = new AddedLocationDetector();
    public GetClassesFromLocation classesFromLocation;
    public DuplicateClassesDetector duplicateClassesDetector;
    public Map<File, Object> file2HumanMap = [:]
    public Map<File, List<JustStackTrace>> dupsFilesMap = [:]
    public Map<String, List<JustStackTrace>> dupsClassMap = [:]
    public Map<File, JustStackTrace> locationMap12 = [:]
    public HashSet<String> classes = [];
    public HashSet<File> nonUsedLocations;
    public HashSet<File> usedLocations;
    public HashSet<String> stackTraceIgnoreClassName = new HashSet<>()
    public volatile UsedByAnalysis usedByAnalysis;
    public final Date startDate = new Date();
    public boolean humanReturnStackTraceElement = true

    AllClasspathAnalysis() {
        addClassToStackTraceIgnoreClassName(AddedLocationDetector)
        addClassToStackTraceIgnoreClassName(AddFilesToClassLoaderCommon)
        addClassToStackTraceIgnoreClassName(CustomObjectHandlerImpl)
        addClassToStackTraceIgnoreClassName(AddDirectoryWithFiles)
        stackTraceIgnoreClassName.add('org.codehaus.groovy.runtime.')
    }

    List<String> easyCalcUsed(){
        addClassesFromCurrentClassLoader()
        adder.addClassPathFromURLClassLoader(JrrClassUtils.getCurrentClassLoaderUrl())
        analise();
        Set<Object> usedd = getUsedLocationsHuman().keySet()
        List<String> sorted = usedd.collect { it.toString() }.sort()
        return sorted

    }

    void addClassToStackTraceIgnoreClassName(Class clazz){
        stackTraceIgnoreClassName.add(clazz.getName())
    }

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
        resolveHumanLocation()
        nonUsedLocations = duplicateClassesDetector.getNonUsedLocations()
        usedLocations = duplicateClassesDetector.getUsedLocations()
    }

    StackTraceElement resolveLocationByStackTrace(JustStackTrace justStackTrace){
        StackTraceElement[] trace = justStackTrace.getStackTrace()
        return trace.toList().find{isGoodStackElement(it)}
    }

    boolean isGoodStackElement(StackTraceElement el){
        String find1 = stackTraceIgnoreClassName.find {
            return el.getClassName().startsWith(it)
        }
        return find1==null
    }

    HashSet<File> getFilesFromHuman(Collection locations) {
        Map<Object, File> reverted = MapUtils.invertMap(file2HumanMap)
        List<File> collect1 = locations.collect {
            File get1 = reverted.get(it)
            if(get1==null){
                throw new Exception("Not found ${it}")
            }
            return get1
        }
        return new HashSet<File>(collect1)
    }

    void resolveHumanLocation(){
        ClassPathCalculatorAbstract calculator = createCalculator()
        adder.locationMap.each {
            Object object = calculator.convertFileToObject(it.key)
            file2HumanMap.put(it.key, object)
        }
    }

    Map<Object,Object> getUsedLocationsHuman(){
        return convertListToHuman(usedLocations);
    }

    Map<Object,Object> getNonUsedLocationsHuman(){
        return convertListToHuman(nonUsedLocations);
    }

    Map<Object,Object> convertListToHuman(Collection<File>  list){
        Map<Object,Object> result= [:]
        list.each {
            Object key1 = file2HumanMap.get(it)
            if(key1==null){
                log.info "${it} not found in file2HumanMap"
                key1 = it;
            }
            JustStackTrace justStackTrace = locationMap12.get(it)
            Object value2
            if(humanReturnStackTraceElement){
                value2 = resolveLocationByStackTrace(justStackTrace)
            }else {
                value2 = justStackTrace
            }
            result.put(key1, value2);
        }
        return result
    }

    ClassPathCalculatorAbstract createCalculator(){
        return new ClassPathCalculatorWithAdder()
    }

    List<JustStackTrace> getLocations(List<File> files) {
        return files.collect { locationMap12.get(it) }
    }


    Map getFilteredDupsClassMap() {
        Map<String, List<JustStackTrace>> all1 = dupsClassMap.findAll { return needShowDupClass(it.key) };
        if(humanReturnStackTraceElement){
            Map<String,List<StackTraceElement>> rr =[:]
            all1.each {
                List<StackTraceElement> collect1 = it.value.collect { ss -> resolveLocationByStackTrace(ss) }
                rr.put(it.key,collect1)
            }
            return rr
        }
        return all1
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
