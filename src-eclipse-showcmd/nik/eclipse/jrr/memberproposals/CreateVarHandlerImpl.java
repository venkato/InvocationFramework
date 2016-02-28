package nik.eclipse.jrr.memberproposals;

import java.util.logging.Level;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.lang.String;

public class CreateVarHandlerImpl implements IHandler {
	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger(JrrClassUtils.getCurrentClass().getName());

	public CreateVarHandlerImpl() {
		LOG.info("nik3");
	}

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
		LOG.info("execute " + event);
		try {
			ClassMemberProposalBean handleControl = JrrOpenMemberUtils.getContext();
			if (handleControl != null) {
				IMember iJavaElement = OpenDeclarationHandlerImpl.findMember(handleControl);
				if (iJavaElement == null) {
					MessageDialog.openError(null, "java element not found",
							"Java element not found : " + handleControl.typeOfFirstArgs.getFullyQualifiedName() + " "
									+ handleControl.stringLiteral.getLiteralValue());
					return null;
				}
				createVar(handleControl, iJavaElement);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "error during opening file", e);
			MessageDialog.openError(null, "File open error", e + "");
		}
		return null;

	}

	public static void createVar(ClassMemberProposalBean cmpb, IMember iJavaElement) throws Exception {
		// if (substring.contains("=")) {
		// LOG.info("contains = " + substring);
		// } else {
		// cmpb.cue.
		String importType;
		if (iJavaElement instanceof IMethod) {
			IMethod new_name2 = (IMethod) iJavaElement;
			importType = new_name2.getReturnType();
		} else {
			IField field = (IField) iJavaElement;
			importType = field.getTypeSignature();
		}
		LOG.info(importType);
		String ss;
		String importType3 = resolveImport(importType, iJavaElement, cmpb);
		ss = importType3 + " " + iJavaElement.getElementName() + " = (" + importType3 + ")";
		ss += " ";
		LOG.info(ss);
		int nextStatement = cmpb.styledText.getText().substring(0, cmpb.firestSelecttionInStyledText)
				.lastIndexOf("JrrClassUtils");
		// LOG.info("insertiing text at " + nextStatement + " i=" + i);
		cmpb.styledText.setSelection(nextStatement);
		cmpb.styledText.insert(ss);
		LOG.info("cp999 2");

		// }
	}

	static String resolveImport(String importType, IMember iJavaElement, ClassMemberProposalBean cmpb)
			throws JavaModelException {
		String res = null;
		String ss;
		if (importType.length() == 2 && importType.startsWith("[")) {
			res = JrrOpenMemberUtils.convertJavaName2Human(importType);
		} else if (importType.length() == 1) {
			res = JrrOpenMemberUtils.convertJavaName2Human(importType);
		} else {
			importType = importType.substring(1);
			LOG.info(importType);
			importType = importType.replace(";", "");
			String importType3;// = importType;
			IType declaringType = iJavaElement.getDeclaringType();
			LOG.info(declaringType + "");
			boolean array;
			{
				String importType2 = importType.replaceFirst("<.+>", "");
				LOG.info(importType2);
				array = importType2.startsWith("L");
				if (array) {
					importType2 = importType2.substring(1);
				}
				String importType4 = importType2;
				importType2 = resolveType(importType2, declaringType);
				LOG.info(importType2);
				IImportDeclaration createImport = cmpb.cu.createImport(importType2, null, null);
				importType3 = importType4.replaceAll(".+\\.([^\\.]+)", "$1");
			}
			if (importType.contains("<")) {
				LOG.info(importType);
				String s = importType.replaceFirst(".+<(.+)>", "$1");
				LOG.info(s);
				String resolveImport = resolveImport(s, iJavaElement, cmpb);
				importType3 += "<" + resolveImport + ">";
			}
			if (array) {
				importType3 += "[]";
			}
			LOG.info(importType3);
			res = importType3;
		}
		LOG.info(res);
		return res;
	}

	static String resolveType(String typeToResolve, IType declaringType) throws JavaModelException {
		if (typeToResolve == null) {
			LOG.severe("typeToResolve is null " + declaringType);
			return null;
		}
		String[][] resolveType = declaringType.resolveType(typeToResolve);
		if (resolveType == null) {
			LOG.warning(typeToResolve);
			return typeToResolve;
		}
		String returnTYpe = resolveType[0][0] + "." + resolveType[0][1];
		return returnTYpe;
	}

	public static String txtEditorId;

	public static void inittt() {
		if (txtEditorId == null) {
			txtEditorId = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor("a.txt").getId();
		}
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
		if (false) {
			try {
				JrrClassUtils.getFieldValue(String.class, "CASE_INSENSITIVE_ORDER");
				JrrClassUtils.getFieldValue(String.class, "serialPersistentFis");
				JrrClassUtils.getFieldValue(String.class, "serialPersistentFields");
				JrrClassUtils.invokeMethod(String.class, "length");
				// JrrClassUtils.invokeMethod(String.class, "hash");
			} catch (Exception e) {
			}
		}
	}
}
