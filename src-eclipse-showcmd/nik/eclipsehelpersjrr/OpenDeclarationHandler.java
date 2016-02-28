package nik.eclipsehelpersjrr;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

import net.sf.jremoterun.utilities.JrrClassUtils;
import nik.eclipse.jrr.memberproposals.OpenDeclarationHandlerImpl;

public class OpenDeclarationHandler implements IHandler {
	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger(JrrClassUtils.getCurrentClass().getName());

	public static IHandler delegate = new OpenDeclarationHandlerImpl();

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		delegate.addHandlerListener(handlerListener);
	}

	@Override
	public void dispose() {
		// LOG.info("dispose");
		delegate.dispose();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		return delegate.execute(event);
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}

	@Override
	public boolean isHandled() {
		// LOG.info("handled");
		return delegate.isHandled();
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// LOG.info("removeHandlerListener " + handlerListener);

		delegate.removeHandlerListener(handlerListener);
	}
}
