package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.UrlToFileConverter
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.javaservice.CallProxy
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs

import java.util.logging.Logger

@CompileStatic
class CompileRequestClient implements CompilerRequest {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public  static ClRef clRefSetCallerClass = new ClRef('net.sf.jremoterun.utilities.nonjdk.compiler3.SetCallerClass');
    public volatile static AddToClassloader addToClassloaderStatic;
    URLClassLoader loader;
    AddFilesToUrlClassLoaderGroovy adder;
    AddFilesToUrlClassLoaderGroovy adderParent;
    File ifDir
    File extMethodsCompiledClasses

    public static File ifDirDefault

    static void detectIfDir() {
        Enumeration<URL> resources = JrrClassUtils.currentClassLoader.getResources('icon/idea/custom_runners.png')
        List<URL> list = resources.toList()
        log.info "${list}"
        for (URL resource : list) {
            File f = UrlToFileConverter.c.convert resource
            assert f.exists()
            File baseDir = f.parentFile.parentFile.parentFile.parentFile
            if (new File(baseDir, 'resources-groovy').exists()) {
                ifDirDefault = baseDir
                return
            }
        }
        ifDirDefault = GitSomeRefs.ifFramework.resolveToFile()
        throw new Exception("Not found ifDir from ${list}")
    }

    //
    CompileRequestClient() {
        if (ifDir == null) {
            if (ifDirDefault == null) {
                detectIfDir()
            }
            ifDir = ifDirDefault
        }
        init()
    }

    CompileRequestClient(File ifDir) {
        this.ifDir = ifDir
        init()
    }


    void init() {
        ClassLoader extClassLoader = CreateGroovyClassLoader.findExtClassLoader()
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[0], extClassLoader)
        adderParent = new AddFilesToUrlClassLoaderGroovy(urlClassLoader)

        addJarsToParentClassloader()
//        log.info "extClassLoader : ${extClassLoader.class.name}"
        loader = CreateGroovyClassLoader.createGroovyClassLoader2(urlClassLoader)
        adder = new AddFilesToUrlClassLoaderGroovy(loader)
        URLClassLoader classLoaderParent = (URLClassLoader) loader.getParent()
        assert classLoaderParent.getClass() == URLClassLoader
//        adderParent = new AddFilesToUrlClassLoaderGroovy(classLoaderParent)
        extMethodsCompiledClasses = new File(ifDir, "build/logger-ext-methods");
        if(addToClassloaderStatic!=null){
            addToClassloaderStatic.add(adder)
        }
        addEclipseCompiler()
    }

    void addJarsToParentClassloader(){
        AddGroovyToParentCl.defaultAddtoParentCl.addGroovyJarToParentClassLoader(adderParent)
    }


    void addEclipseCompiler() {
        adder.add CustObjMavenIds.eclipseJavaCompiler
        adder.add CustObjMavenIds.eclipseJavaAstParser
    }

    void addExtMethodsDir() {
        if (!extMethodsCompiledClasses.exists() || extMethodsCompiledClasses.listFiles().length == 0) {
            CompileGroovyExtMethod compileGroovyExtMethod = new CompileGroovyExtMethod(ifDir)
            compileGroovyExtMethod.compileExtMethods()
            assert extMethodsCompiledClasses.listFiles().length > 0
        }
        adder.addF extMethodsCompiledClasses
        adder.add IfFrameworkResourceDirs.resources_groovy;
        //adder.addF new File(ifDir, "resources-groovy");
    }

    void addDirs() {
//        adder.addF new File(ifDir,"src-logger-ext-methods");
        adder.add IfFrameworkSrcDirs.src_groovycompiler
        //adder.addF new File(ifDir, "src-groovycompiler");

    }

    void checks(GroovyCompilerParams params) {
        assert params.outputDir != null
        if (params.files.size() == 0 && params.dirs.size() == 0) {
            throw new Exception("need set either files or dirs")
        }
        params.files.each { assert it.exists() }
//        params.dirs.each { assert it.directory }
    }


    void doJointCompilationOptions(GroovyCompilerParams params){
        File stubDir2 = params.stubDir
        if (stubDir2 == null) {
            stubDir2 = params.outputDir
            new ClRef('org.codehaus.groovy.tools.javac.JavaStubGenerator');
        }
        if (params.keepStubs) {
            new ClRef('org.codehaus.groovy.tools.javac.JavaAwareCompilationUnit');
            params.jointCompilationOptions.put("keepStubs", Boolean.TRUE);
        }
        params.jointCompilationOptions.put("stubDir", stubDir2);
    }

    void compile(GroovyCompilerParams params) {
        if(!params.eclipseCompiler){
            adder.add new MavenCommonUtils().getToolsJarFile()
            adder.add IfFrameworkSrcDirs.src_compiler_jdk
        }
        doJointCompilationOptions(params)
        checks(params)
        addDirs()
        if(params.addExtentionJrrMethods) {
            addExtMethodsDir()
        }
        adder.addFileWhereClassLocated JrrClassUtils
        adder.addFileWhereClassLocated JrrUtils
        params.addTestClassLoaded JrrClassUtils
        params.addTestClassLoaded CompileRequestRemote
        params.outputDir.deleteDir()
        params.outputDir.mkdir()
        if (!params.outputDir.exists()) {
            throw new FileNotFoundException("failed create ${params.outputDir}")
        }
        assert params.outputDir.listFiles().length == 0
        if (!params.printWarning) {
            params.additionalFlags.add('-nowarn')
        }

        if (params.eclipseCompiler && params.javaVersion == null) {
            throw new IllegalArgumentException('specify javaVersion')
        }
        doStdChecks(params)
        ContextClassLoaderWrapper.wrap2(loader, {
            if(params.setCallerClassJava11!=null){
                Map setCallerClassClass = clRefSetCallerClass.newInstance(loader) as Map
                setCallerClassClass.get(params.setCallerClassJava11);
            }
            params.testClassLoaded.each {
                try {
                    loader.loadClass(it.className)
                } catch (Throwable e) {
                    log.info "failed load class ${it} due to : ${e}"
                    throw e
                }
            }
            params.testClassLoadedSameClassLoader.each {
                Class loadedClass = loader.loadClass(it.className)
                ClassLoader loader3 = loadedClass.getClassLoader()
                if (loader3 != loader) {
                    File location = UrlCLassLoaderUtils.getClassLocation(loadedClass)
                    throw new Exception("class ${it} with location ${location} loaded by ${loader3}")
                }
            }
            Class compileRequestRemoteClass = loader.loadClass(CompileRequestRemote.getName());
//            assert compileRequestRemoteClass.classLoader == loader
            Object service = compileRequestRemoteClass.newInstance()
            CompilerRequest service2 = (CompilerRequest) CallProxy.makeProxy2(CompilerRequest, service);
            service2.compile(params)
        })
        if (params.outputDir.listFiles().length == 0) {
            throw new Exception("No compiled files in out dir : ${params.outputDir}")
        }

    }

    void doStdChecks(GroovyCompilerParams params){
        //ClRef clRef = new ClRef('org.codehaus.groovy.tools.shell.IO')
        params.testNotFoundInParentClassLoaded.each {doStdChecksForClass(it)}
    }

    void doStdChecksForClass(ClRef clRef){
        try {
            URLClassLoader classloaderParent = adderParent.classloader
            Class clazz = clRef.loadClass(classloaderParent);
            ClassLoader classLoaderActual = clazz.getClassLoader()
            boolean b = classLoaderActual ==classloaderParent
            if(!b){
                String addtional = ''
                if (classLoaderActual instanceof URLClassLoader) {
                    URLClassLoader u = (URLClassLoader) classLoaderActual;
                    addtional = u.getURLs().toList()
                }
                throw new Exception("class ${clRef} found by ${classLoaderActual} ${addtional}")
            }
            List<URL> uRLS = classloaderParent.getURLs().toList()
            throw new Exception("class ${clRef} found by parent classLoader : ${uRLS}")
        }catch(ClassNotFoundException e){
            log.fine("class not found as expected",e)
        }
    }


}
