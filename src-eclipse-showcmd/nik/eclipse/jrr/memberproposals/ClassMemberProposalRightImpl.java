package nik.eclipse.jrr.memberproposals;

import net.sf.jremoterun.utilities.JrrClassUtils;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.impetuouslab.eclipse.filecompletion.impl.RegExpCompletionProposal;


import java.lang.String;

/**
 * Class calculate proposals
 *
 */
public class ClassMemberProposalRightImpl implements IJavaCompletionProposalComputer {

	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger(JrrClassUtils.getCurrentClass().getName());

	public ClassMemberProposalRightImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {

		try {
			// LOG.info("starting proposal");
			ClassMemberProposalBean handleControl = JrrOpenMemberUtils.getContext();
			if (handleControl != null) {
				return createProposals(handleControl);
			}

		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
		}
		return new ArrayList<ICompletionProposal>();
	}

	public static List<ICompletionProposal> createProposals(ClassMemberProposalBean fpb) throws Exception {
		int diff = fpb.documentOffset - fpb.stringLiteral.getStartPosition();
		String s1;
		if (diff < 2) {
			s1 = null;
		} else {
			s1 = fpb.stringLiteral.getLiteralValue().substring(0, diff - 1);
			s1 = s1.toLowerCase();
		}
		LOG.info(fpb.fields + "");
		if (false) {
			JrrClassUtils.getFieldValue(String.class, "CASE_INSENSITIVE_ORDER");
			JrrClassUtils.invokeMethod(fpb.stringLiteral.getClass(), "getParent");
			JrrClassUtils.invokeMethod(String.class, "e");
//			JrrClassUtils.invokeMethod("", "hashCode");
			JrrClassUtils.invokeMethod("", "equals", 1);
			JrrClassUtils.invokeMethod(String.class, "hash");
			JrrClassUtils.invokeMethod(fpb.stringLiteral, "getLiteralValue");
			JrrClassUtils.getFieldValue(ClassMemberProposalRightImpl.class, "logger");
		}

		int i = 0;
		List<ICompletionProposal> sss = new ArrayList();
		if (JrrOpenMemberUtils.reFields.contains(fpb.reflectionElement)) {
			ImemberComparator comparator = new ImemberComparator();
			List<IField> begin2 = new ArrayList<IField>();
			{
				TreeSet<IField> begin1 = new TreeSet<IField>(comparator);
				TreeSet<IField> toEnd = new TreeSet<IField>(comparator);
				for (IField field : fpb.fields) {
					if (s1 == null || field.getElementName().toLowerCase().startsWith(s1)) {
						begin1.add(field);
					} else {
						toEnd.add(field);
					}
				}
				begin2.addAll(begin1);
				begin2.addAll(toEnd);
				Collections.reverse(begin2);
			}
			for (IField field : begin2) {
				// log.info(field);
				String importType = field.getTypeSignature();
				// sss.add(field.getElementName() + " - " + importType);
				importType = JrrOpenMemberUtils.convertJavaName2Human(importType);
				String attachedJavadoc = field.getAttachedJavadoc(null);
				if (attachedJavadoc == null) {
					attachedJavadoc = importType + " " + field.getDeclaringType().getFullyQualifiedParameterizedName()
							+ "." + field.getElementName();
					if (org.eclipse.jdt.core.Flags.isStatic(field.getFlags())) {
						attachedJavadoc = "static " + attachedJavadoc;
					}
				} else {
				}
				ICompletionProposal completionProposal = new RegExpCompletionProposal(field.getElementName(), //
						fpb.stringLiteral.getStartPosition() + 1, fpb.stringLiteral.getLength() - 2,
						field.getElementName().length(), null, field.getElementName() + " - " + importType, null,
						attachedJavadoc, i++);

				sss.add(completionProposal);
			}
			if (false) {
				JrrClassUtils.getFieldValue(fpb.cue, "BRACKETS");
				JrrClassUtils.getFieldValue(fpb.cue, "ERROR_MESSAGE_TIMEOUT");
				JrrClassUtils.getFieldValue(fpb.cue, "DEFAULT_RULER_CONTEXT_MENU_ID");
				JrrClassUtils.invokeMethod(fpb.cue, "createAnnotationAccess");
			}
		} else if (JrrOpenMemberUtils.reMethods.contains(fpb.reflectionElement)) {
			IMethodComparator2 comparator = new IMethodComparator2();
			List<IMethod> begin2 = new ArrayList<IMethod>();
			{
				TreeSet<IMethod> begin1 = new TreeSet<IMethod>(comparator);
				TreeSet<IMethod> toEnd = new TreeSet<IMethod>(comparator);
				for (IMethod field : fpb.methods) {
					if (s1 == null || field.getElementName().toLowerCase().startsWith(s1)) {
						begin1.add(field);
					} else {
						toEnd.add(field);
					}
				}
				begin2.addAll(begin1);
				begin2.addAll(toEnd);
				Collections.reverse(begin2);
			}
			for (IMethod field : begin2) {
				// log.info(field);
				String attachedJavadoc = field.getAttachedJavadoc(null);

				StringBuilder params = new StringBuilder();
				{
					params.append("(");
					boolean first = true;
					for (String parameterType : field.getParameterTypes()) {
						if (first) {
							first = false;
						} else {
							params.append(", ");
						}
						params.append(JrrOpenMemberUtils.convertJavaName2Human(parameterType));
					}
					params.append(")");
				}
				StringBuilder desc = new StringBuilder();
				{
					desc.append(field.getElementName());
					desc.append(params);
				}
				if (attachedJavadoc == null) {
					StringBuilder sb = new StringBuilder();
					if (org.eclipse.jdt.core.Flags.isStatic(field.getFlags())) {
						sb.append("static ");
					}
					sb.append(JrrOpenMemberUtils.convertJavaName2Human(field.getReturnType()));
					sb.append(" ");
					sb.append(field.getDeclaringType().getFullyQualifiedParameterizedName());
					sb.append(".");
					sb.append(field.getElementName());
					// sb.append(" ");
					sb.append(params);
					attachedJavadoc = sb.toString();
				} else {
				}
				ICompletionProposal completionProposal = new RegExpCompletionProposal(field.getElementName(), //
						fpb.stringLiteral.getStartPosition() + 1, fpb.stringLiteral.getLength() - 2,
						//
						field.getElementName().length(), null, desc.toString(), null, attachedJavadoc, i++);
				sss.add(completionProposal);
			}
		}
		return sss;
	}

	@Override
	public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public void sessionEnded() {

	}

	@Override
	public void sessionStarted() {

	}

}
