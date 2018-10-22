package net.sf.jremoterun.utilities.nonjdk.classuggest

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ConsoleSymbols
import net.sf.jremoterun.utilities.nonjdk.classpath.UrlCLassLoaderUtils2
import org.zeroturnaround.zip.ZipInfoCallback
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger
import java.util.zip.ZipEntry

@CompileStatic
class GetListOfClasses {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    Object loadClassFromPackage2(Class sampleClass, String className) {
          return loadClassFromPackage(sampleClass,className).newInstance()
    }

    Class loadClassFromPackage(Class sampleClass, String className) {
        if (className == ConsoleSymbols.question.s) {
            List<String> classes = getListOfClasses(sampleClass)
            throw new IllegalArgumentException("List of classes : ${classes}")
        }
        String fullClassName = sampleClass.getPackage().getName()+'.' + className
        try {
            return sampleClass.getClassLoader().loadClass(fullClassName)
        } catch (ClassNotFoundException e) {
            List<String> classes = getListOfClasses(sampleClass)
            log.info "Failed load ${className}, available ${classes}"
//            throw new ClassNotFoundException("Failed load ${className}, available ${classes}", e)
            throw e
        }

    }

    List<String> getListOfClasses(Class sampleClass) {
        File baseDir = UrlCLassLoaderUtils2.getClassLocationFirst(sampleClass)
        String packName = sampleClass.getPackage().getName()
        return getListOfClasses(baseDir, packName)
    }

    List<String> getListOfClasses(File baseDir, String packageName) {
        packageName = packageName.replace('.', '/')
        if (!packageName.endsWith('/')) {
            packageName += '/'
        }
        if (baseDir.isDirectory()) {
            File subDir = baseDir.child(packageName)
            if (!subDir.exists()) {
                throw new FileNotFoundException(subDir.getAbsolutePath())
            }
            if (!subDir.isDirectory()) {
                throw new IllegalArgumentException("not a dir : ${subDir}")
            }
            List<File> names = subDir.listFiles().toList()
            names = names.findAll { it.isFile() }
            names = names.findAll { !it.getName().contains('$') }
            names = names.findAll { it.getName().endsWith('.groovy') || it.getName().endsWith('.class') }
            List<String> res = names.collect { it.getName().replace('.groovy', '').replace('.class', '') }
            res = res.findAll { it != null && it.length() > 0 }
            if (res.size() == 0) {
                throw new IllegalStateException("Not entries found for ${subDir}")
            }
            res = res.sort()
            return res
        }
        if (!baseDir.getName().endsWith('.jar')) {
            throw new IllegalArgumentException("should be jar file : ${baseDir}")
        }
        List<String> res = []
        ZipInfoCallback zipInfoCallback = {
            ZipEntry it ->
                if (it.getName().startsWith(packageName)) {
                    res.add(it.getName().replace(packageName, ''))
                }
        }
        ZipUtil.iterate(baseDir, zipInfoCallback)
        res = res.findAll { !it.contains('/') }
        res = res.collect { it.replace('.groovy', '').replace('.class', '') }
        res = res.findAll { it != null && it.length() > 0 && !it.contains('$') }
        if (res.size() == 0) {
            throw new IllegalStateException("Not entries found for ${baseDir} ${packageName}")
        }
        res = res.sort()
        return res;
    }

}
