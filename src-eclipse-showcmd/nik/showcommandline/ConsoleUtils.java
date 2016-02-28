package nik.showcommandline;

import java.util.logging.Logger;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.internal.console.ConsoleView;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class ConsoleUtils {

	private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

	public static final String cmdS = "cmd";

	public static final String exitCodeS = "exit code";

	public static final String launchNameS = "Launch name";

	public static final String notTerminatedS = "Not terminated";

	public static final String consoleViewId = "org.eclipse.ui.console.ConsoleView";

	/**
	 * @since 3.0
	 */
	public static IConsole findConsole(final String containsName)
			throws Exception {
		final ConsoleView consoleView = (ConsoleView) 				getWorkbenchPage().findView(consoleViewId);
		final IConsoleManager consoleManager = ConsolePlugin.getDefault()
				.getConsoleManager();
		final IConsole[] consoles = consoleManager.getConsoles();
		if (consoles.length == 0) {
			throw new Exception("No consoles");
		}
		if (containsName == null || containsName.length() == 0) {
			return consoles[0];
		}
		for (int i = 0; i < consoles.length; i++) {
			if (consoles[i].getName().contains(containsName)) {
				return consoles[i];
			}
		}
		throw new Exception("Console whose name contains " + containsName
				+ " not found");
	}







	public static IWorkbenchWindow getWorkbenchWindow() {
		return PlatformUI.getWorkbench().getWorkbenchWindows()[0];
	}




	public static IWorkbenchPage getWorkbenchPage() {
		return getWorkbenchWindow().getActivePage();
	}

}
