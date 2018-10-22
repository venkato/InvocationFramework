package net.sf.jremoterun.utilities.nonjdk.eclipse.classpathchecker;

import java.util.logging.Logger;

import org.apache.commons.logging.LogFactory;
import org.eclipse.osgi.internal.loader.EquinoxClassLoader;
import org.slf4j.LoggerFactory;

import groovy.lang.GroovyObject;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.ClRef;
import net.sf.jremoterun.utilities.nonjdk.classpath.tester.ClassPathTesterHelper2;
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollector;
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollectorI;

public class EclipseClassPathChecker {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public ClassPathTesterHelper2 helper;

	public EclipseClassPathChecker(ClassPathTesterHelper2 helper) {
		this.helper = helper;
	}

	public EclipseClassPathChecker(ProblemCollectorI problemCollector) {
		helper = new ClassPathTesterHelper2(problemCollector);
	}

	public static void runChecks() {
		ProblemCollector problemCollector = new ProblemCollector();
		EclipseClassPathChecker tester = new EclipseClassPathChecker(problemCollector);
		tester.check();
		boolean allGood = problemCollector.checkIfProblemExistAndShowException();
		log.info("all good ? : " + allGood);
	}

	public void check() {
		EquinoxClassLoader currentClassLoader = (EquinoxClassLoader) JrrClassUtils.getCurrentClassLoader();
		checkImpl(currentClassLoader);
	}
	
	ClRef jna = new ClRef("com.sun.jna.Callback"); 
	ClRef egitActivator = new ClRef("org.eclipse.egit.core.Activator"); 
	ClRef jgitActivator = new ClRef("org.eclipse.jgit.api.CloneCommand"); 
	ClRef svnOpActivator = new ClRef("org.tigris.subversion.subclipse.ui.operations.UpdateOperation"); 
	
	
	public void checkImpl(EquinoxClassLoader currentClassLoader) {
		
		helper.checkTheSameClassLoader5(org.apache.log4j.Logger.class, currentClassLoader);
		helper.checkTheSameClassLoader5(LogFactory.class, currentClassLoader);
		helper.checkTheSameClassLoader5(LoggerFactory.class, currentClassLoader);
		helper.checkTheSameClassLoader5(javassist.ClassPath.class, currentClassLoader);
//        helper.checkTheSameClassLoader5(sun.jvmstat.monitor.HostIdentifier.class, classLoaderParent, currentClassLoader);

		helper.checkNotSameClassLoader5(jna, currentClassLoader);
		helper.checkNotSameClassLoader5(GroovyObject.class, currentClassLoader);
		

		
		
		helper.checkNotSameClassLoader5(egitActivator, currentClassLoader);
		helper.checkNotSameClassLoader5(jgitActivator, currentClassLoader);
		helper.checkNotSameClassLoader5(svnOpActivator, currentClassLoader);
		
	}
}
