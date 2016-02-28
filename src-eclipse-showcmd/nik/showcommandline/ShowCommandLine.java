package nik.showcommandline;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IOConsoleOutputStream;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.io.Closeable;

public class ShowCommandLine implements IHandler {
	private static final Logger log = Logger.getLogger(ShowCommandLine.class.getName());

	public boolean inFly = false;

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		log.info("handlerListener " + handlerListener);
	}

	@Override
	public void dispose() {

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		log.info("123");
		try {
			inFly = true;
			executeImpl(event);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.log(Level.SEVERE, null, e);
		} finally {
			inFly = false;
		}
		return null;
	}

	public Object executeImpl(ExecutionEvent event) throws Exception {
		final IConsoleView consoleView = (IConsoleView) ConsoleUtils.getWorkbenchPage()
				.findView(ConsoleUtils.consoleViewId);
		IConsole console = consoleView.getConsole();
		if (console == null) {
			log.info("can't find current console");
			return null;
		}
		if (!(console instanceof ProcessConsole)) {
			return null;
		}
		final ProcessConsole console2 = (ProcessConsole) console;
		IOConsoleOutputStream new_name = null;
		String textToAdd = "";
		{
			List<Closeable> openStreams = (List<Closeable>) JrrClassUtils.getFieldValue(console2, "openStreams");
			log.info(openStreams + "");
			for (Closeable object : openStreams) {
				if (object instanceof IOConsoleOutputStream) {
					new_name = (IOConsoleOutputStream) object;
					break;
				}
			}
			if (new_name == null) {
				log.info("outpust stream not found");
				// return null;
			}
		}
		final RuntimeProcess runtimeProcess = (RuntimeProcess) console2.getProcess();
		String value = runtimeProcess.getAttribute("org.eclipse.debug.core.ATTR_CMDLINE");
		value = value.trim();
		textToAdd += "Command line : " + value;
		log.info("cmd line " + value);
		if (runtimeProcess.isTerminated()) {
			final int exitVlaue = runtimeProcess.getExitValue();
			log.info("exit code " + exitVlaue);
			textToAdd += "\nexit code " + exitVlaue;
		} else {
			// result.put(exitCodeS, notTerminatedS);
		}
		textToAdd += "\n";
		if (new_name == null) {
			console2.getDocument().replace(0, 0, textToAdd);
			;
		} else {
			new_name.write(textToAdd);
		}

		return null;
	}

	@Override
	public boolean isEnabled() {
		// log.info("isEnabled");
		return !inFly;
	}

	@Override
	public boolean isHandled() {
		// log.info("isHandled2");
		return !inFly;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		log.info("handlerListener " + handlerListener);
	}

}