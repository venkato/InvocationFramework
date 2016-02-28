package nik.eclipse.jrr.memberproposals;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.StringLiteral;

import net.sf.jremoterun.utilities.JrrClassUtils;

/**
 * Aux class to define if cursor now in File constructor and regexp patterns
 *
 */
final class ClassMemberClassFinder extends ASTVisitor {
	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger(JrrClassUtils.getCurrentClass().getName());

	private final int documentOffset;

	/**
	 * Reference to string object, where now cusror located
	 */
	private StringLiteral foundeNoded;

	ClassMemberClassFinder(int documentOffset) {
		super(false);
		this.documentOffset = documentOffset;
	}

	@Override
	public boolean visit(StringLiteral node) {
		// Verifying that cursor in this string node
		if (node.getStartPosition() < documentOffset && (node.getLength() + node.getStartPosition()) > documentOffset) {
			LOG.info(" found StringLiteral " + node);
//			if (isStringNodeInFileElement(node)) {
				foundeNoded = node;
//				file = true;
//			} else if (isStringNodeInPatternElement(node)) {
//				file = false;
//				foundeNoded = node;
//			}else {
//				//LOG.info("seems not a file");
//			}
		}
		return true;
	}

	public StringLiteral getFoundedNode() {
		return foundeNoded;
	}


}