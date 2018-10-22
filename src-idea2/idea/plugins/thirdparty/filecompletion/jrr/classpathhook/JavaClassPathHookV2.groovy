package idea.plugins.thirdparty.filecompletion.jrr.classpathhook

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.JavaCommandLineState
import com.intellij.execution.configurations.ParametersList
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.target.TargetedCommandLineBuilder
import com.intellij.execution.target.value.CompositeTargetValue
import com.intellij.execution.target.value.DeferredLocalTargetValue
import com.intellij.execution.target.value.DeferredTargetValue
import com.intellij.execution.target.value.TargetValue
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.share.Ideasettings.IdeaJavaRunner2Settings
import javassist.CtClass
import javassist.CtMethod
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.javassist.codeinjector.CodeInjector
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.classpath.UrlCLassLoaderUtils2
import org.apache.commons.io.FilenameUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise

import java.util.logging.Level

@CompileStatic
class JavaClassPathHookV2 extends InjectedCode {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);

    public static boolean inited = false

    static void installBothHooks() {
        if (inited) {
            log.info "already inited"
        } else {
            inited = true
            JavaClassPathHookV2.installHook()
            JavaStartConfigHook.installHook()
        }
    }

    @Override
    protected Object handleException(Object key, Throwable throwable) {
        super.handleException(key, throwable)
        JrrUtilities.showException("Can't create java start hook", throwable)
        return null
    }

    static void installHook() {
        Class clazz = com.intellij.execution.configurations.JavaCommandLineState
        CtClass ctClass = JrrJavassistUtils.getClassFromDefaultPool(clazz)
        CtMethod method = JrrJavassistUtils.findMethod(clazz, ctClass, "createTargetedCommandLine", 2);
        method.insertAfter """
            ${CodeInjector.createSharedObjectsHookVar2(clazz)}
            ${CodeInjector.myHookVar}.get(new Object[]{this,\$_});
        """
        JrrJavassistUtils.redefineClass(ctClass, clazz);
        CodeInjector.putInector2(clazz, new JavaClassPathHookV2())
    }

    static void installHookV2() {
        Class clazz = com.intellij.execution.configurations.JavaCommandLineState
        CtClass ctClass = JrrJavassistUtils.getClassFromDefaultPool(clazz)
        CtMethod method = JrrJavassistUtils.findMethod(clazz, ctClass, "prepareTargetEnvironmentRequest", 3);
        method.insertAfter """
            ${CodeInjector.createSharedObjectsHookVar2(clazz)}
            ${CodeInjector.myHookVar}.get(new Object[]{this,this.myCommandLine});
        """
        JrrJavassistUtils.redefineClass(ctClass, clazz);
        CodeInjector.putInector2(clazz, new JavaClassPathHookV2())
    }

    public static int getTimeout = 1000;
public static boolean useNew = true


    @Override
    Object getImpl(Object key) {
        java.lang.Object[] obj = (Object[]) key;
        JavaCommandLineState commandLineState = obj[0] as JavaCommandLineState;
        TargetedCommandLineBuilder generalCommandLine = obj[1] as TargetedCommandLineBuilder
        ArrayList<TargetValue<String>> parametersList = JrrClassUtils.getFieldValue(generalCommandLine, 'myParameters') as ArrayList
        //ParametersList parametersList = generalCommandLine.getParametersList()
        //log.info "parametersString : ${parametersList.getParametersString()}"
        //List<String> list = parametersList.getList()
//        list
        IdeaJavaRunner2Settings.jvmOptions.reverse(false).each {
            parametersList.add(0, new DeferredLocalTargetValue(it))
        }

        ExecutionEnvironment environment = commandLineState.getEnvironment()
        String runnerName = environment.toString()
        File runnerFile = new File(IdeaJavaRunner2Settings.runners, runnerName)
        if (!runnerFile.exists()) {
            log.info "no runner file ${runnerFile}"
            return null
        }
        File libNameAsGroovy = new File(IdeaJavaRunner2Settings.libs, runnerFile.text + '.groovy')
        if (!libNameAsGroovy.exists()) {
            log.info "file not exists : ${libNameAsGroovy}"
            return null
        }

        int classPathEl = -1;
        for (int i = 0; i < parametersList.size(); i++) {
            try {
                TargetValue<String> elI = parametersList.get(i);
                Promise<String> targetValue = elI.getTargetValue()
                String value1 = targetValue.blockingGet(getTimeout)
                if (value1 == '-classpath') {
                    classPathEl = i + 1;
                    log.info "classPathEl = ${classPathEl}"
                    break;
                }
            } catch (Exception exception) {
                log.info("failed get el ${i}", exception)
            }
        }
        if (classPathEl == -1) {
            log.info "classpath el not found"
            return null
        }
        TargetValue<String> elClasspath = parametersList.get(classPathEl)
        log.info "elClasspath name = ${elClasspath.getClass().getName()}"
        final Promise<String> classpathValue = elClasspath.getTargetValue()
        log.info("classpathValue class ${classpathValue.getClass().getName()}")
        File myLibsPrefix = createZip7(libNameAsGroovy)
        if(useNew) {
            if (elClasspath instanceof com.intellij.execution.target.value.DeferredTargetValue) {
//            at com.intellij.execution.target.value.DeferredTargetValue.resolve(DeferredTargetValue.java:21)
//            at com.intellij.openapi.projectRoots.JdkCommandLineSetup$requestUploadIntoTarget$2.fun(JdkCommandLineSetup.kt:101)
//            at org.jetbrains.concurrency.AsyncPromise$then$1.apply(AsyncPromise.kt:125)
//            at com.intellij.openapi.projectRoots.JdkCommandLineSetup.provideEnvironment(JdkCommandLineSetup.kt:168)
//            at com.intellij.execution.configurations.JavaCommandLineState.handleCreatedTargetEnvironment(JavaCommandLineState.java:141)
//            at com.intellij.execution.runners.ExecutionEnvironment.prepareTargetEnvironment(ExecutionEnvironment.java:152)
                log.info("resoling manyally")
                com.intellij.execution.target.value.DeferredTargetValue dd = elClasspath
                String strOld = dd.getLocalValue().blockingGet(getTimeout) as String
                log.info "strOld = ${strOld}"
                if (strOld.startsWith('@')) {
                    throw new Exception('Not supported')
                }
//            dd.resolve('niksomevalue')
                String classPathElValue = myLibsPrefix.getAbsolutePath() + File.pathSeparator + strOld;
                log.info "classPathElValue after = ${classPathElValue}"
                parametersList.set(classPathEl, new DeferredLocalTargetValue(classPathElValue))
            }
        }else{
            String classPathElValue = classpathValue.blockingGet(getTimeout)
            if (classPathElValue.startsWith('@')) {
                throw new Exception('Not supported')
            }
            log.info "classPathElValue before = ${classPathElValue}"
            classPathElValue = myLibsPrefix.getAbsolutePath() +  File.pathSeparator  + classPathElValue;
            log.info "classPathElValue after = ${classPathElValue}"
            parametersList.set(classPathEl, new DeferredLocalTargetValue(classPathElValue))
        }
//        int index = list.indexOf('-classpath')
//        if (index < 0) {
//            List<File> all = list.findAll { it.startsWith('@') }.collect {
//                it.substring(1) as File
//            }.findAll { it.exists() && it.isFile() }.findAll { it.text.readLines()[0] == '-classpath' }
//            if(all.size()==1){
//                log.info "seems java9"
//                addppendJava9(all.first(),libNameAsGroovy)
//            }else {
//                log.error("bad index : ${list}")
//            }
//        } else {
//            String classPathJar = list.get(index + 1)
//            File file = classPathJar as File
//            assert file.exists()
//            List<File> classpath = [];
//            File myLibsPrefix = createZip7(libNameAsGroovy)
//            classpath.add myLibsPrefix
//            classpath.add file
//            classpath.each { assert it.exists() }
//            String newClassPath = classpath.collect { it.absolutePath }.join(';')
//            parametersList.set(index + 1, newClassPath)
//        }
        return null
    }

    void addppendJava9(File ideaClassPathFile, File libNameAsGroovy) {
        List<File> classpath = [];
        AddFilesToClassLoaderGroovy addCl = new AddFilesToClassLoaderGroovy() {
            @Override
            void addFileImpl(File file33) throws Exception {
                classpath.add(file33)
            }
        }
        addCl.addFromGroovyFile(libNameAsGroovy)

        List<String> lines = ideaClassPathFile.text.readLines()
        String s = classpath.join(';') + ';' + lines[1]
        lines[1] = s
        ideaClassPathFile.text = lines.join('\n')

    }

    public static File createZip7(File libNameAsGroovy) throws Exception {
        List<File> classpath = [];
        AddFilesToClassLoaderGroovy addCl = new AddFilesToClassLoaderGroovy() {
            @Override
            void addFileImpl(File file33) throws Exception {
                classpath.add(file33)
            }
        }
        addCl.addFromGroovyFile(libNameAsGroovy)
        classpath = classpath.findAll { it != null }.unique()
        File suffix = createZip2(classpath, FilenameUtils.getBaseName(libNameAsGroovy.name))
        return suffix
    }

    public static File createZip2(List<File> files, String libName) throws Exception {
        byte[] bs = UrlCLassLoaderUtils2.createJarForLoadingClasses(files)
        assert IdeaJavaRunner2Settings.jars.exists()
        File libFile = new File(IdeaJavaRunner2Settings.jars, libName + '.jar')
        libFile.bytes = bs
        return libFile
    }


}
