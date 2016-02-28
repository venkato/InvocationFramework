package nik.eclipse.jrr.memberproposals;

import java.util.ArrayList;
import java.util.logging.Level;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PartInitException;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class OpenDeclarationHandlerImpl implements IHandler {
	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger(JrrClassUtils.getCurrentClass().getName());

	public volatile boolean handleed = false;

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		LOG.info("addHandlerListener " + handlerListener);
		// JrrClassMemberProposalBean.bean.getOpenVarHandleListener().add(handlerListener);

	}

	@Override
	public void dispose() {
		LOG.info("dispose");

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LOG.info("execute " + event);
		handleed = false;
		try {
			LOG.fine("stating proposal");
			Event event2 = (Event) event.getTrigger();
			LOG.info("widget" + event2.widget);
			ClassMemberProposalBean handleControl = JrrOpenMemberUtils.getContext();
			if (handleControl != null) {
				IMember iJavaElement = findMember(handleControl);
				if (iJavaElement == null) {
					MessageDialog.openError(null, "java element not found",
							"Java element not found : " + handleControl.typeOfFirstArgs.getFullyQualifiedName() + " "
									+ handleControl.stringLiteral.getLiteralValue());
					return null;
				}
				final JavaEditor editorPart1 = (JavaEditor) JavaUI.openInEditor(iJavaElement.getDeclaringType());
				LOG.info("cp2");
				EditorUtility.revealInEditor(editorPart1, iJavaElement);
				LOG.info("cp3");
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "error during opening file", e);
			MessageDialog.openError(null, "File open error", e + "");
		} finally {
			handleed = true;
			// ArrayList<IHandlerListener> openVarHandleListener =
			// JrrClassMemberProposalBean.bean
			// .getOpenVarHandleListener();
			// org.eclipse.core.commands.HandlerEvent event=new
			// HandlerEvent(this, handleed, handleed);
			// for (IHandlerListener iHandlerListener : openVarHandleListener) {
			// iHandlerListener.
			// }
		}
		return null;
	}

	public static IMember findMember(ClassMemberProposalBean cmpb) throws PartInitException, JavaModelException {
		String s1 = cmpb.stringLiteral.getLiteralValue();
		IMember iJavaElement = null;
		if (JrrOpenMemberUtils.reFields.contains(cmpb.reflectionElement)) {
			for (IField field : cmpb.fields) {
				LOG.info(field.getElementName());
				if (field.getElementName().equals(s1)) {
					// LOG.info(field + "");
					iJavaElement = field;
					break;
				}
			}
		} else if (JrrOpenMemberUtils.reMethods.contains(cmpb.reflectionElement)) {
			int countParams = countMethodParams(cmpb);
			if (countParams == -1) {
				return null;
			}
			LOG.info(countParams + "");
			for (IMethod field : cmpb.methods) {
				// LOG.info(field.getElementName());
				if (field.getElementName().equals(s1)) {
					LOG.info(field + "");
					if (field.getParameterTypes().length == countParams) {
						LOG.info("matched " + field);
						iJavaElement = field;
						break;
					} else {
						LOG.info("not matched " + field.getParameterTypes().length + " != " + countParams + ", fields "
								+ field);
					}
				}
			}

		}
		return iJavaElement;
	}

	private static int countMethodParams(ClassMemberProposalBean cmpb) {
		String text = cmpb.styledText.getText();
		int maxIndex = text.indexOf(";", cmpb.firestSelecttionInStyledText);
		String s = text.substring(cmpb.firestSelecttionInStyledText, maxIndex);
		LOG.info(s);
		int length = s.length();
		s = s.replace(",", "");
		LOG.info(s);
		int lengthAfter = s.length();
		int counttt = length - lengthAfter;
		// log.in
		return counttt;
	}

	@Override
	public boolean isEnabled() {
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
