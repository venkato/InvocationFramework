package nik.eclipsehelpersjrr;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

import net.sf.jremoterun.utilities.JrrClassUtils;
import nik.eclipse.jrr.memberproposals.CreateVarHandlerImpl;

public class CreateVarHandler implements IHandler {
	private static final java.util.logging.Logger LOG =JrrClassUtils.getJdkLogForCurrentClass();

	public static IHandler delegate = new CreateVarHandlerImpl();

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		delegate.addHandlerListener(handlerListener);

	}

	@Override
	public void dispose() {
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
		return delegate.isHandled();
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		delegate.removeHandlerListener(handlerListener);
	}
}
