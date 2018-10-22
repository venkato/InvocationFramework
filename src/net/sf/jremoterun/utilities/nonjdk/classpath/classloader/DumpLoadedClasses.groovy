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
    public ClassLoader classLoader;

    public boolean sort = true;
    public volatile boolean needRun = true;
    public final Object lock = new Object();
    public long lastDumped;
    public Thread thread;

    DumpLoadedClasses(File dumpToFile, long periodInSec, ClassLoader classLoader) {
        this.file = dumpToFile
        this.periodInSec = periodInSec
        this.classLoader = classLoader
    }

    static DumpLoadedClasses startDumpingClassloader(File dumpToFile, long periodInSec, int rotateCount, ClassLoader classLoader1) {
        FileRotate.rotateFile(dumpToFile, rotateCount);
        DumpLoadedClasses dumpLoadedClasses = new DumpLoadedClasses(dumpToFile, periodInSec, classLoader1);
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
        List<String> classes = dumpLoadedClassesNames(classLoader)
        if (sort) {
            classes = classes.sort()
        }
        PrintWriter writer = file.newPrintWriter()
        classes.each {
            writer.println(it)
        }
        writer.flush()
        writer.close()
        lastDumped = System.currentTimeMillis();
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
