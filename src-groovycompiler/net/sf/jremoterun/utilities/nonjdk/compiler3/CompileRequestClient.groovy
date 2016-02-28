package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.UrlToFileConverter
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.javaservice.CallProxy
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds

import java.util.logging.Logger

@CompileStatic
class CompileRequestClient implements CompilerRequest {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

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
        ifDirDefault = GitReferences.ifFramework.resolveToFile()
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
        AddGroovyToParentCl.defaultAddtoParentCl.addGroovyJarToParentClassLoader(adderParent)

//        log.info "extClassLoader : ${extClassLoader.class.name}"
        loader = CreateGroovyClassLoader.createGroovyClassLoader2(urlClassLoader)
        adder = new AddFilesToUrlClassLoaderGroovy(loader)
        URLClassLoader classLoaderParent = (URLClassLoader) loader.parent
        assert classLoaderParent.class == URLClassLoader
//        adderParent = new AddFilesToUrlClassLoaderGroovy(classLoaderParent)
        extMethodsCompiledClasses = new File(ifDir, "build/logger-ext-methods");
        addEclipseCompiler()
    }

    void addEclipseCompiler() {
        adder.add LatestMavenIds.eclipseJavaCompiler
        adder.add LatestMavenIds.eclipseJavaAstParser
    }

    void addExtMethodsDir() {
        if (!extMethodsCompiledClasses.exists() || extMethodsCompiledClasses.listFiles().length == 0) {
            CompileGroovyExtMethod compileGroovyExtMethod = new CompileGroovyExtMethod(ifDir)
            compileGroovyExtMethod.compileExtMethods()
            assert extMethodsCompiledClasses.listFiles().length > 0
        }
        adder.addF extMethodsCompiledClasses
        adder.addF new File(ifDir, "resources-groovy");
    }

    void addDirs() {
//        adder.addF new File(ifDir,"src-logger-ext-methods");
        adder.addF new File(ifDir, "src-groovycompiler");

    }

    void checks(GroovyCompilerParams params) {
        assert params.outputDir != null
        if (params.files.size() == 0 && params.dirs.size() == 0) {
            throw new Exception("need set either files or dirs")
        }
        params.files.each { assert it.exists() }
//        params.dirs.each { assert it.directory }
    }

    void compile(GroovyCompilerParams params) {
        checks(params)
        addDirs()
        addExtMethodsDir()
        adder.addFileWhereClassLocated JrrClassUtils
        adder.addFileWhereClassLocated JrrUtils
        params.addTestClassLoaded JrrClassUtils
        params.addTestClassLoaded CompileRequestRemote
        params.outputDir.deleteDir()
        params.outputDir.mkdir()
        if (!params.outputDir.exists()) {
            throw new FileNotFoundException("failed create ${params.outputDir}")
        }
        if (!params.printWarning) {
            params.additionalFlags.add('-nowarn')
        }
        assert params.outputDir.listFiles().length == 0
        if (params.eclipseCompiler && params.javaVersion == null) {
            throw new IllegalArgumentException('specify javaVersion')
        }

        ContextClassLoaderWrapper.wrap2(loader, {
            params.testClassLoaded.each {
                try {
                    loader.loadClass(it)
                } catch (Throwable e) {
                    log.info "failed load class ${it} due to : ${e}"
                    throw e
                }
            }
            params.testClassLoadedSameClassLoader.each {
                Class loadedClass = loader.loadClass(it)
                ClassLoader loader3 = loadedClass.getClassLoader()
                if (loader3 != loader) {
                    File location = UrlCLassLoaderUtils.getClassLocation(loadedClass)
                    throw new Exception("class ${it} with location ${location} loaded by ${loader3}")
                }
            }
            Class compileRequestRemoteClass = loader.loadClass(CompileRequestRemote.name);
//            assert compileRequestRemoteClass.classLoader == loader
            Object service = compileRequestRemoteClass.newInstance()
            CompilerRequest service2 = (CompilerRequest) CallProxy.makeProxy2(CompilerRequest, service);
            service2.compile(params)
        })
        if (params.outputDir.listFiles().length == 0) {
            throw new Exception("No compiled files in out dir : ${params.outputDir}")
        }

    }

}
