package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileRotate;

import java.util.logging.Logger;

@CompileStatic
class DumpLoadedClasses {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File file;
    public long periodInSec;
    public ClassLoader classLoader1;

    public boolean sort = true;
    public volatile boolean needRun = true;
    public final Object lock = new Object();
    public Date lastDumped;
    public Thread thread;

    DumpLoadedClasses(File dumpToFile, long periodInSec, ClassLoader classLoader1) {
        this.file = dumpToFile
        this.periodInSec = periodInSec
        this.classLoader1 = classLoader1
        if(classLoader1==null){
            throw new NullPointerException('Classloader is null')
        }
    }

    static DumpLoadedClasses startDumpingClassloader(File dumpToFile, long periodInSec, int rotateCount, ClassLoader classLoader1) {
        DumpLoadedClasses dumpLoadedClasses = new DumpLoadedClasses(dumpToFile, periodInSec, classLoader1);
        FileRotate.rotateFile(dumpToFile, rotateCount);
        dumpLoadedClasses.start();
        return dumpLoadedClasses

    }

    static DumpLoadedClasses startDumpingCurrentClassloader(File dumpToFile, long periodInSec, int rotateCount) {
        return startDumpingClassloader(dumpToFile, periodInSec, rotateCount, JrrClassUtils.getCurrentClassLoader())
    }

    static List<String> dumpLoadedClassesNames(ClassLoader cl) {
        return new ArrayList<Class>(dumpLoadedClasses(cl)).collect { it.getName() }
    }

    static List<Class> dumpLoadedClasses(ClassLoader cl) {
        return JrrClassUtils.getFieldValue(cl, 'classes') as List;
    }

    void dumpToFile() {
        List<String> classes = dumpLoadedClassesNames(classLoader1)
        if (sort) {
            classes = classes.sort()
        }
        PrintWriter writer = file.newPrintWriter()
        classes.each {
            writer.println(it)
        }
        writer.flush()
        writer.close()
        lastDumped = new Date();
    }

    void start() {
        Runnable r = {
            log.info "class dumper started ${file}"
            try {
                while (needRun) {
                    dumpToFile()
                    synchronized (lock) {
                        lock.wait(periodInSec * 1000)
                    }
                }
            } finally {
                log.info "stopped"
            }
        }
        thread = new Thread(r, 'Loaded classes dumper');
        thread.start()
    }


}
