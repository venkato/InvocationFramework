package nik.eclipsehelpersjrr;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

import net.sf.jremoterun.utilities.JrrClassUtils;
import nik.eclipse.jrr.memberproposals.ClassMemberProposalRightImpl;

/**
 * Class calculate proposals
 *
 */
public class ClassMemberProposalRight implements IJavaCompletionProposalComputer {

	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger(JrrClassUtils.getCurrentClass().getName());

	public static IJavaCompletionProposalComputer delegate = new ClassMemberProposalRightImpl();

	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		try {
			LOG.fine("starting proposal");
			return delegate.computeCompletionProposals(context,
					monitor);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
		}
		return new ArrayList<ICompletionProposal>();
	}

	@Override
	public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		return delegate.computeContextInformation(context, monitor);
	}

	@Override
	public String getErrorMessage() {
		return delegate.getErrorMessage();
	}

	@Override
	public void sessionEnded() {
		delegate.sessionEnded();
	}

	@Override
	public void sessionStarted() {
		delegate.sessionStarted();
	}

}
