
package nik.showcommandline;

import java.util.List;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.jdt.internal.junit.model.TestRunSession;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement.FailureTrace;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class CopyJUnitStackTraceToConsole extends TestRunListener {
	private static final java.util.logging.Logger log = java.util.logging.Logger
			.getLogger(JrrClassUtils.getCurrentClass().getName());

	public static TestRunListener testRunListener;

	public static void init()  {
		if (testRunListener == null) {
			testRunListener = new CopyJUnitStackTraceToConsole();
			org.eclipse.jdt.junit.JUnitCore.addTestRunListener(testRunListener);
			log.info("init done");
		} else {
			log.info("already inited");
		}
	}

	@Override
	public void testCaseFinished(ITestCaseElement testCaseElement) {
		l: try {
			log.info(testCaseElement + "");
			FailureTrace failureTrace = testCaseElement.getFailureTrace();
			log.info("failureTrace " + failureTrace);
			if (failureTrace != null) {
				log.info(failureTrace.getTrace());
				TestRunSession testRunSession = (TestRunSession) testCaseElement.getTestRunSession();
				IProcess[] processes = testRunSession.getLaunch().getProcesses();
				log.info(processes + "");
				log.info(processes.length + "");
				for (IProcess iProcess : processes) {
					String label = iProcess.getLabel();
					IConsole findConsole = ConsoleUtils.findConsole(label);
					ProcessConsole processConsole = (ProcessConsole) findConsole;
					// processConsole.clearConsole();
					java.util.List fieldValue = (List) JrrClassUtils.getFieldValue(processConsole, "openStreams");
					log.info(fieldValue + "");
					for (Object object : fieldValue) {
						if (object instanceof IOConsoleOutputStream) {
							IOConsoleOutputStream new_name = (IOConsoleOutputStream) object;
							new_name.write(failureTrace.getTrace());
							log.info("trace written");
							break l;
						}
					}
				}
			}
		} catch (Exception e) {
			log.log(java.util.logging.Level.SEVERE, "", e);
		}
	}

}