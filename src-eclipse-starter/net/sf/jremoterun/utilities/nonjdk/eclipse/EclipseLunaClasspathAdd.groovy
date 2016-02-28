package net.sf.jremoterun.utilities.nonjdk.eclipse

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.JrrUtilities3
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunner
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import org.codehaus.groovy.control.CompilationFailedException
import org.eclipse.osgi.internal.debug.Debug
import org.eclipse.osgi.internal.framework.EquinoxContainer
import org.eclipse.osgi.internal.loader.EquinoxClassLoader
import org.eclipse.osgi.internal.loader.classpath.ClasspathEntry
import org.eclipse.osgi.internal.loader.classpath.ClasspathManager

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
public class EclipseLunaClasspathAdd {

    public static volatile boolean inited = false
	public static volatile Thread initializerThread;
    static final Object lock1 = new Object()
	public static org.eclipse.osgi.internal.loader.EquinoxClassLoader clP; 

	
	private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

    static {
        log.info("JrrUtils inited");
		
    }

    public static volatile AddFilesToClassLoaderGroovy addCl;
    public static volatile Throwable startupError;

	

    public static ClasspathManager findClasspathManager() {
        clP = (EquinoxClassLoader) JrrClassUtils
                .getCurrentClassLoader();
        log.info("cl = " + clP);
        log.info("bundle name = " + clP.getBundle().getSymbolicName());
        ClasspathManager classpathManager = clP.getClasspathManager();
        return classpathManager;
    }
	
	static void enableBootDeligation() {
		EquinoxContainer eq =  clP.getBundleLoader().@container;
		eq.@bootDelegateAll = true;
	}

    public static ArrayList<ClasspathEntry> al;
    public static ClasspathManager classpathManager;

    public static void propaget() throws Exception {
        JrrClassUtils.setFieldValue(classpathManager, "entries", al.toArray(new ClasspathEntry[0]));
    }


    public static boolean init() {
        synchronized (lock1) {
            if (inited) {
                log.info("already inited")
                return true
            } else {
                inited = true
                try {
                    initImpl();
                    return true
                } catch (Throwable e) {
                    startupError = e
                    log.log(Level.SEVERE, null, e);
                    JrrUtilities3.showException("Failed init eclipse", e);
                    return false
                }
            }
        }
    }

    public static void initImpl() throws Exception {
		initializerThread = Thread.currentThread();
        SetConsoleOut2.setConsoleOutIfNotInited();
        initImpl2();
    }
	
	static void enableParalellLoading(boolean enable) {
		JrrClassUtils.setFieldValue(clP, "isRegisteredAsParallel", enable);
	}

    public static void initImpl2() throws Exception {
        log.info("starting added cutom classed");

        log.info("thread details : ${initializerThread.name} ${initializerThread.id}");
        classpathManager = findClasspathManager();
		enableParalellLoading(false);
        

        // al.add(classpathEntry);
        ClasspathEntry[] classpathEntries = (ClasspathEntry[]) JrrClassUtils.getFieldValue(classpathManager, "entries");
        al = new ArrayList<ClasspathEntry>(Arrays.asList(classpathEntries));
        // classpathManager.addClassPathEntry(al, clName, classpathManager,
        // null);

        // FileInputStream fis=new FileInputStream(file);
        addCl = new EclipseInitAddFilesToClassLoaderGroovy();
        // addCl.addFromGroovyFile(EclipseSettings.fileWithCLassPath2);
        runScriptInUserDir();
        runCustom();
        //		addCl.addFromGroovyFile(EclipseSettings.fileWithCLassPath4);
        log.info("trying adding :  " + addCl.getAddedFiles2().size());
        JrrClassUtils.setFieldValue(classpathManager, "entries", al.toArray(classpathEntries));
        log.info("classes added fine");
    }

    static void runScriptInUserDir() throws CompilationFailedException, IOException {
        File userHome = new File(System.getProperty("user.home"));
        if (userHome.exists()) {
            File scriptInUserDir = new File(userHome, "jrr/configs/eclipse.groovy");
            if (scriptInUserDir.exists()) {
                log.info("running ${scriptInUserDir} ..");
                Script parse = GroovyMethodRunner.groovyScriptRunner.getGroovyShell().parse(scriptInUserDir);
                parse.run();
                log.info("finished ${scriptInUserDir}");
            }
        }
    }

    public static String customScriptProperty = "eclipse.custom.init";

    static void runCustom() throws CompilationFailedException, IOException {
        String customScript = System.getProperty(customScriptProperty);
        if (customScript == null) {
            log.info("property not set :" + customScriptProperty);
        } else {
            File customScriptF = new File(customScript);
            if (customScriptF.exists()) {
                log.info("running " + customScriptF);
                Script parse = GroovyMethodRunner.groovyScriptRunner.getGroovyShell().parse(customScriptF);
                parse.run();
                log.info("finished ${customScriptF}");
            } else {
                log.info("File not found : " + customScript);
                JrrUtilities.showException("File not found " + customScript, new FileNotFoundException(customScript));
            }
        }
    }

    public static void enableDebugClassloader() throws Exception {
        ClasspathManager classpathManager = findClasspathManager();
        Debug debug = (Debug) JrrClassUtils.getFieldValue(classpathManager, "debug");
        JrrClassUtils.setFieldValue(debug, "DEBUG_LOADER", true);
    }

}
