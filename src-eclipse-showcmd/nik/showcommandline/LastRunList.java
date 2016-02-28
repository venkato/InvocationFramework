package nik.showcommandline;

import java.util.logging.Level;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.ui.DebugUITools;

public class LastRunList implements IHandler {
	private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(LastRunList.class.getName());

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		LOG.info("addHandlerListener " + handlerListener);

	}

	@Override
	public void dispose() {
		LOG.info("dispose");

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LOG.info("running last run configration");
		try {
			final LaunchConfigurationManager configurationManager = DebugUIPlugin.getDefault()
					.getLaunchConfigurationManager();
			ILaunchConfiguration lastLaunch = configurationManager
					.getLastLaunch("org.eclipse.debug.ui.launchGroup.run");
			DebugUITools.launch(lastLaunch, "run");
		} catch (final Exception e) {
			LOG.log(Level.WARNING, "running last run", e);
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		LOG.info("enabled ");
		return true;
	}

	@Override
	public boolean isHandled() {
		LOG.info("handled");
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		LOG.info("removeHandlerListener " + handlerListener);

	}
}
