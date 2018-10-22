package net.sf.jremoterun.utilities.nonjdk.classpath.console

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseResult
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.classpath.UrlCLassLoaderUtils2
import net.sf.jremoterun.utilities.nonjdk.decompiler.FernflowerDecompiler2
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler

import java.util.logging.Logger


@CompileStatic
class JrrConsoleDecompiler implements ClassNameSynonym{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    void decompileDirect(File from, File toDir) {
        String[] args = [from.getAbsolutePath(), toDir.getAbsolutePath()]
        ConsoleDecompiler.main(args)
    }

    void decompile(String className,File jarFile) {
        decompile(className, [:],jarFile)
    }

    void decompile2(String className) {
        decompile(className,findJar(className))
    }

    void decompile2(String className, String methodName, int argCount) {
        decompile(className, methodName, argCount,findJar(className))
    }

    File findJar(String className) {
        String className2 = ClassAnalyze.normalizeClassName(className)
        ClassLoader classLoader = getClass().getClassLoader()
        Enumeration<URL> resources = classLoader.getResources(className2)
        List<URL> list = resources.toList()
        int size = list.size()
        if (size == 0) {
            throw new ClassNotFoundException(className2)
        }
        if (size > 1) {
            log.info "found ${size} : ${list}"
        }
        File jarFile = UrlCLassLoaderUtils2.convertClassLocationToPathToJar(list[0], className2)
        assert jarFile.exists()
        log.info "found in ${jarFile}"
        return jarFile
    }


    void decompile(String className, Map options, File jarFile) {
        FernflowerDecompiler2 fernflowerDecompiler = new FernflowerDecompiler2(options)
        fernflowerDecompiler.addFiles.addRtJar()
        fernflowerDecompiler.addFiles.addJfrJarsIfExists()
        String decompile = fernflowerDecompiler.decompile(jarFile, className)
        log.info "${decompile}"
    }

    void decompileM(MavenId mavenId, String className, String methodName, int argCounts) {
        AddFileToClassloaderDummy fileToClassloaderDummy = new AddFileToClassloaderDummy()
        fileToClassloaderDummy.add mavenId
        File file = fileToClassloaderDummy.addedFiles2[0]
        decompile(className, methodName, argCounts,file)
    }

    void decompile(String className, String methodName, int argCounts,File jarFile) {
        FernflowerDecompiler2 fernflowerDecompiler = new FernflowerDecompiler2()
        String decompile = fernflowerDecompiler.decompile(jarFile, className)
        f3(decompile, methodName, argCounts)
    }

    void f3(String classContent, String methodName, int argCounts) {
        assert classContent != null
//        CompilationUnit cu = JavaParser.parse(classContent);
        CompilationUnit cu = new JavaParser().parse(classContent).result.get();
        int size = cu.getTypes().size()
        if (size == 0) {
            throw new IllegalStateException("no types found : ${classContent}")
        }
        if (size > 1) {
            log.info "found many types : ${size}"
        }
        TypeDeclaration<?> type = cu.getType(0)
        List<MethodDeclaration> methods = type.getMethodsByName(methodName)
        if (methods.size() == 0) {
            List<String> methods23 = type.getMethods().collect { it.getNameAsString() }.unique().sort()
            String join = methods23.join(',')
            throw new Exception("no method : ${methodName}, available : ${join}")
        }
        List<MethodDeclaration> methods2 = methods.findAll { it.parameters.size() == argCounts }
        int size1 = methods2.size()
        if (size1 == 0) {
            List<Integer> params = methods.collect { it.parameters.size() }
            throw new Exception("no such methods : ${methodName} with args count ${argCounts}, available : ${params} ")
        }
        if (size1 > 1) {
            log.info "found many methods : ${size1}"
        }
        methods2.each {
            MethodDeclaration methodDeclaration = it
            String string = methodDeclaration.toString()
            log.info "\n${string}"
        }


    }


}
