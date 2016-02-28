package net.sf.jremoterun.utilities.nonjdk.classpath.console

import groovy.transform.CompileStatic
import javassist.bytecode.ClassFile
import javassist.bytecode.FieldInfo
import javassist.bytecode.MethodInfo
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.JavaVersionMapping
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@CompileStatic
class ClassAnalyze {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    AddFilesToJavasistPool classPool = new AddFilesToJavasistPool(new ClassPool());

    void showFileContent(File classFile, ClassAnalyzeEnum fields) {
        ClassFile classFile1 = getClassFile(classFile)
        fileSwitch(classFile1,fields)
    }

    void fileSwitch(ClassFile classFile1, ClassAnalyzeEnum fields){
        switch (fields) {
            case ClassAnalyzeEnum.f:
                showFields(classFile1)
                break
            case ClassAnalyzeEnum.m:
                break
                showMethods(classFile1)
            case ClassAnalyzeEnum.v:
                printClassVesrion(classFile1)
                break
        }
    }


    void printClassVesrion(ClassFile classFile1 ){
        int majorVersion = classFile1.majorVersion
        int minorVersion = classFile1.minorVersion
        if (majorVersion == minorVersion ||minorVersion==0) {
            String javaHuman = JavaVersionMapping.javaMinorVersionToHuman.get(majorVersion)
            log.info "class version : ${javaHuman} , ${majorVersion}"
        } else {
            log.info "majorVersion : ${majorVersion}, minor : ${minorVersion} ; majorVersion : ${JavaVersionMapping.javaMinorVersionToHuman.get(majorVersion)}, minor : ${JavaVersionMapping.javaMinorVersionToHuman.get(minorVersion)} ; "
        }
    }

    void showClass(String className, ClassAnalyzeEnum fields) {
        className = normalizeClassName(className)
        ClassLoader classLoader = getClass().getClassLoader()
        Enumeration<URL> resources = classLoader.getResources(className)
        List<URL> list = resources.toList()
        int size = list.size()
        if (size == 0) {
            throw new ClassNotFoundException(className)
        }
        if (size > 1) {
            log.info "found ${size} : ${list}"
        }
        InputStream stream = classLoader.getResourceAsStream(className)
        if (stream == null) {
            throw new ClassNotFoundException(className)
        }
        ClassFile classFile
        try {
            DataInputStream dataInputStream = new DataInputStream(stream)
            classFile = new ClassFile(dataInputStream)
        } finally {
            stream.close()
        }
        fileSwitch(classFile,fields)

    }

    void showClassFromM(MavenId mavenId, String className, ClassAnalyzeEnum fields) {
        AddFileToClassloaderDummy fileToClassloaderDummy = new AddFileToClassloaderDummy()
                fileToClassloaderDummy.add mavenId
        File file = fileToClassloaderDummy.addedFiles2[0]
        showClassFromJar(file,className,fields)

    }

    void showClassFromJar(File jarFile, String className, ClassAnalyzeEnum fields) {
        ClassFile classFile1 = getClassFile2(jarFile, className)
        fileSwitch(classFile1,fields)
    }

    void showFields(ClassFile classFile1) {
        List<FieldInfo> fields = (List) classFile1.getFields()
        List<String> names = fields.collect { it.getName() + ' ' + it.getDescriptor() }.sort()
        log.info "Found ${names.size()} fields : \n${names.join('\n')}"
    }

    void showMethods(ClassFile classFile1) {
        List<MethodInfo> fields = (List) classFile1.getMethods()
        List<String> names = fields.collect { it.getName() + '' + it.getDescriptor() }.sort()
        log.info "Found ${names.size()} methods : \n${names.join('\n')}"
    }

    static String normalizeClassName(String className) {
        if (className.contains('\\') && className.endsWith('.class')) {
            className = className.replace('\\', '/')
        } else if (!className.contains('/') && className.contains('.')) {
            className = className.replace('.', '/') + '.class'
        }
        return className

    }

    ClassFile getClassFile2(File jarFile, String className) {
        assert jarFile.exists()
        assert jarFile.isFile()
        log.info "${jarFile}"
        className = normalizeClassName(className)
        ZipFile zipFile = new ZipFile(jarFile)
        try {
            ZipEntry ze = zipFile.getEntry(className);
            if (ze == null) {
                throw new FileNotFoundException("entry ${className} not found in ${jarFile}")
            }
            InputStream is = zipFile.getInputStream(ze);
            DataInputStream dataInputStream = new DataInputStream(is)
            ClassFile classFile = new ClassFile(dataInputStream)
            return classFile
        } finally {
            zipFile.close()
        }
    }

    ClassFile getClassFile(File classFile) {
        assert classFile.exists()
        assert classFile.isFile()
        DataInputStream dataInputStream = classFile.newDataInputStream()
        try {
            ClassFile classFile2 = new ClassFile(dataInputStream)
            return classFile2
        } finally {
            dataInputStream.close()
        }
    }


}
