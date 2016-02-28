package nik.eclipse.jrr.memberproposals;

import net.sf.jremoterun.utilities.JrrClassUtils;
import nik.eclipsehelpersjrr.ClassMemberProposalRight;
import nik.eclipsehelpersjrr.CreateVarHandler;
import nik.eclipsehelpersjrr.OpenDeclarationHandler;

/**
 * This class allows inspect SWT object. To do this set value of swtMonitorKey
 * attribute to k, then click on swt object and press alt-k.
 */
public class FileComplitionProposal {

	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger(JrrClassUtils.getCurrentClass().getName());

	private static boolean init = false;

	public static void init() {
		if (!init) {
			LOG.info("nik");
			CreateVarHandler.delegate = new CreateVarHandlerImpl();
			OpenDeclarationHandler.delegate = new OpenDeclarationHandlerImpl();
			ClassMemberProposalRight.delegate = new ClassMemberProposalRightImpl();

			LOG.info("inittt FileComplitionProposal done");
			init = true;
		}
	}

	public static void deInit() {
		if (init) {

			init = false;
		}
	}

}
