package net.sf.jremoterun.utilities.nonjdk.shell.core

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import org.codehaus.groovy.tools.shell.IO
import org.codehaus.groovy.tools.shell.Interpreter
import org.codehaus.groovy.tools.shell.util.CachedPackage
import org.codehaus.groovy.tools.shell.util.DefaultCommandsRegistrar
import org.codehaus.groovy.tools.shell.util.PackageHelperImpl
import org.codehaus.groovy.tools.shell.util.Preferences

import java.lang.reflect.Field
import java.util.logging.Logger

@CompileStatic
abstract class GroovyShellRunner2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static GroovyShellRunner2 shellStarted

    public IO io
    public GroovyshAux sh

    static ClassLoader classLoaderDefault = JrrClassUtils.currentClassLoader
    ClassLoader classLoader3 = classLoaderDefault

    Binding binding

    static DummyPrefs overridenProps;
//    static  prefOld;

    static boolean importPackagesSwitchOff = true;

    static volatile Map<String, CachedPackage> initializePackages;

    static Object lock = new Object()

    GroovyShellRunner2(Binding binding) {
        this.binding = binding
        setDefaultVars()
    }

    void setDefaultVars() {
        binding.setVariable('so', this)
    }


    static void customCreateListPackages() {
        if (checkConditionsCreateListPackages()) {
            synchronized (lock) {
                if (checkConditionsCreateListPackages()) {
                    customCreateListPackagesImpl()
                }
            }
        }
    }

    static boolean checkConditionsCreateListPackages() {
        if (!importPackagesSwitchOff) {
            log.info "importPackagesSwitchOff is switched off"
            return false
        }
        if (initializePackages != null) {
            log.info "packages found already"
            return false
        }
        return true

    }

    static void customCreateListPackagesImpl() {
        Field field = JrrClassUtils.findField(Preferences, "STORE")
        java.util.prefs.Preferences prefOld = field.get(null) as java.util.prefs.Preferences
        overridenProps = new DummyPrefs()
        overridenProps.nativePrefs = prefOld
        JrrClassUtils.setFieldValue(Preferences, "STORE", overridenProps)
        overridenProps.props.put(PackageHelperImpl.IMPORT_COMPLETION_PREFERENCE_KEY, Boolean.TRUE.toString())
        log.info "initialiing packages .."
        //initializePackages = PackageHelperImpl.initializePackages(classLoaderDefault)
        log.info "packages inited"

    }

//    abstract void runConsole();

    void flushHistory() {
        sh.history.flush()
    }


    void setDebug() {
        io.setVerbosity(IO.Verbosity.DEBUG);
    }

    void createGroovyShell() {
        sh = new GroovyshAux(classLoader3, binding, io, this.&createDefaultRegistrar5);
        sh.newEx= new NewValueListener<Throwable>() {
            @Override
            void newValue(Throwable throwable) {
                handleException2(throwable)
            }
        }
        if (importPackagesSwitchOff) {
            assert getPackageHelper().rootPackages == null
            getPackageHelper().rootPackages = initializePackages
        }
        sh.afterInit = this.&displayWelcomeBanner2

    }


    void handleException2(Throwable cause) {
        if (sh.errorHook == null) {
            throw new IllegalStateException('Error hook is not set')
        }
        if (cause instanceof MissingPropertyException) {
            if (cause.type && cause.type.canonicalName == Interpreter.SCRIPT_FILENAME) {
                io.err.println("@|bold,red Unknown property|@: " + cause.property)
                return
            }
        }

        sh.errorHook.call(cause)
    }

    PackageHelperImpl getPackageHelper() {
        return (PackageHelperImpl) sh.getPackageHelper()
    }


    void createDefaultRegistrar5(GroovyshAux sh3) {
        DefaultCommandsRegistrar registrar = new DefaultCommandsRegistrar(sh3)
        registrar.register()
    }

    void displayWelcomeBanner2() {
    }


}
