package nik.eclipse.jrr.memberproposals;

import java.util.EnumSet;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.LocalVariable;
import org.eclipse.jdt.internal.ui.javaeditor.JavaSourceViewer;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.PrimiteClassesUtils;

public class JrrOpenMemberUtils {

	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger(JrrClassUtils.getCurrentClass().getName());

	public static EnumSet<ReflectionElement> reFields = EnumSet.of(ReflectionElement.setFieldValue,
			ReflectionElement.getFieldValue, ReflectionElement.findField);

	public static EnumSet<ReflectionElement> reMethods = EnumSet.of(ReflectionElement.invokeMethod,
			ReflectionElement.findMethod);

	public static ClassMemberProposalBean getContext() throws Exception {
		LOG.fine("nik");
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage();
		IEditorPart compilationUnit = workbenchPage.getActiveEditor();
		IEditorInput editorInput = compilationUnit.getEditorInput();
		Object adapter = editorInput.getAdapter(IJavaElement.class);
		IEditorPart activeEditor = workbenchPage.getActiveEditor();
		if (activeEditor instanceof org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor) {
			// LOG.info("we are in java editor ");
			org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor cue = (org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor) activeEditor;
			int documentOffset = cue.getViewer().getSelectedRange().x;
			if (adapter == null) {
				LOG.info("adapter is null");
			} else if (adapter instanceof ICompilationUnit) {
				CompilationUnit cu = (CompilationUnit) adapter;

				StringLiteral stringLiteral = findStringLiteral(cu.getSource(), documentOffset);
				if (stringLiteral != null) {
					StyledText styledText = cue.getViewer().getTextWidget();
					ClassMemberProposalBean handleControl = new ClassMemberProposalBean();
					handleControl.styledText = styledText;
					handleControl.documentOffset = documentOffset;
					// handleControl.open = open;
					handleControl.cu = cu;
					handleControl.cue = cue;
					handleControl.stringLiteral = stringLiteral;
					handleControl.firestSelecttionInStyledText = handleControl.styledText.getSelectionRange().x;
					boolean found = JrrOpenMemberUtils.handleControl2(handleControl);
					if (found) {
						return handleControl;
					} else {
					}
				} else {
					// LOG.info("not in string literal");
				}
			}
		}
		return null;
	}

	private static boolean handleControl2(ClassMemberProposalBean handleControl) throws Exception {
		int i = handleControl.firestSelecttionInStyledText;
		handleControl.ranges = handleControl.styledText.getStyleRanges();

		int k = 0;
		for (StyleRange styleRange : handleControl.ranges) {
			if ((styleRange.start + styleRange.length) > i) {
				LOG.info(styleRange.start + " " + styleRange.length);
				break;
			}
			k++;
		}
		if (false) {
			JrrClassUtils.getFieldValue(handleControl.cue, "fSelectionHistory");
			JrrClassUtils.invokeMethod(handleControl.cue, "createAnnotationAccess");
		}
		handleControl.selctedRange = k;
		LOG.info("not found");
		boolean found = findMethodsAndFieldsForFirstArg(handleControl);
		return found;
	}

	public static String getPre(int k, String text, StyleRange[] ranges) {
		String sPre2 = text.substring(ranges[k].start, ranges[k].length + ranges[k].start);

		return sPre2;
	}

	private static IJavaElement finddd(ClassMemberProposalBean cmpb) throws Exception {
		JavaSourceViewer viewer = (JavaSourceViewer) cmpb.cue.getViewer();
		ITextSelection ts = (ITextSelection) viewer.getSelection();
		LOG.info(ts.getOffset() + "");
		// log.info(i);
		IJavaElement lastIJavaElement = null;
		int i = cmpb.firestSelecttionInStyledText;
		int difff = ts.getOffset() - i;
		cmpb.diff = difff;
		LOG.info(" diff = " + difff);
		int v = 1;
		int nextStatement = cmpb.styledText.getText().substring(0, i).lastIndexOf(";");
		for (; v < cmpb.selctedRange; v++) {
			LOG.info(v + "");
			String pre = JrrOpenMemberUtils.getPre(cmpb.selctedRange - v, cmpb.styledText.getText(), cmpb.ranges);
			LOG.info("v " + v + " " + pre);
			if (pre.contains("getClass()")) {
				cmpb.staticKnow = true;
				continue;
			}
			int tttt = cmpb.ranges[cmpb.selctedRange - v].start + difff + cmpb.ranges[cmpb.selctedRange - v].length - 1;
			if (tttt + difff < nextStatement) {
				LOG.info("toolow " + nextStatement + " " + tttt);
				break;
			}

			for (int j = 0; j < pre.length() - 1 || j == 0; j++) {
				LOG.info(j + "");
				char charAt = pre.charAt(pre.length() - 1 - j);
				LOG.info(charAt + "");
				if (Character.isLetter(charAt)) {
					IJavaElement[] codeResolve = cmpb.cu.codeSelect(tttt - j, 0);
					LOG.info(codeResolve + "");
					if (codeResolve.length == 0) {
						LOG.info("code length is null");
					} else {
						LOG.info(codeResolve[0] + "");
						if (lastIJavaElement == null) {
							lastIJavaElement = codeResolve[0];
							LOG.info(lastIJavaElement + "");
						} else {
							if (codeResolve[0] instanceof IMethod) {
								IMethod new_name = (IMethod) codeResolve[0];
								String fullyQualifiedName = new_name.getDeclaringType().getFullyQualifiedName();
								LOG.info(fullyQualifiedName);
								if (JrrClassUtils.class.getName().equals(fullyQualifiedName)) {
									cmpb.reflectionElement = ReflectionElement.valueOf(new_name.getElementName());
									LOG.info(cmpb.reflectionElement + "");
									return lastIJavaElement;

								}

							}
						}
					}
					break;
				}

			}
		}
		throw new Exception("bad");
	}

	private static String resolveType(String typeToResolve, IType declaringType) throws JavaModelException {
		if (typeToResolve == null) {
			LOG.warning("typeToResolve is null " + declaringType);
			throw new IllegalStateException("typeToResolve is null " + declaringType);
			// return null;
		}
		String[][] resolveType = declaringType.resolveType(typeToResolve);
		if (resolveType == null) {
			LOG.warning(typeToResolve);
			return typeToResolve;
		}
		String returnTYpe = resolveType[0][0] + "." + resolveType[0][1];
		return returnTYpe;
	}

	private static String javaClass2HumanName(String classname) {
		classname = classname.substring(1).replace(";", "");
		return classname;
	}

	private static boolean findMethodsAndFieldsForFirstArg(ClassMemberProposalBean cmpb) throws Exception {
		IJavaElement firstArgument = finddd(cmpb);
		boolean classFirstArgs = false;
		IType findType = null;
		if (firstArgument instanceof LocalVariable) {
			LOG.info("lv");
			LocalVariable lv = (LocalVariable) firstArgument;
			// "[Q"
			String returnTYpe = javaClass2HumanName(lv.getTypeSignature());
			LOG.info(returnTYpe);
			IJavaElement[] children = cmpb.cu.getChildren();
			IType declaringType = lv.getDeclaringMember().getDeclaringType();
			String[][] resolveType = declaringType.resolveType(returnTYpe);
			returnTYpe = resolveType(returnTYpe, declaringType);
			findType = declaringType.getJavaProject().findType(returnTYpe);
		}
		if (firstArgument instanceof IType) {
			LOG.info("type");
			findType = (IType) firstArgument;
			classFirstArgs = true;
		}
		if (firstArgument instanceof org.eclipse.jdt.core.IField) {
			LOG.info("field");
			org.eclipse.jdt.core.IField iff = (IField) firstArgument;
			String key = iff.getKey();
			LOG.info(key);
			int indexOf = key.indexOf(")L");
			key = key.substring(indexOf + 2);
			key = key.replace(";", "").replace('/', '.');
			LOG.info(key);
			key = resolveType(key, iff.getDeclaringType());
			findType = cmpb.cu.getJavaProject().findType(key);
		}
		if (firstArgument instanceof IMethod) {
			LOG.info("method");
			IMethod new_name = (IMethod) firstArgument;
			// "[L"
			String returnTYpe = new_name.getReturnType().substring(1).replace(";", "");
			LOG.info(returnTYpe);
			findType = cmpb.cu.getJavaProject().findType(returnTYpe);
		}

		LOG.info(findType + "");
		if (findType == null) {
			LOG.info("find type is null");
			return false;
		}
		// Searching field and methods
		cmpb.typeOfFirstArgs = findType;
		findMembers(cmpb, findType, classFirstArgs || cmpb.staticKnow);
		return true;
	}

	public static String convertJavaName2Human(String className) {
		String importType = className;
		;// = className.replace(";", "");
			// if("V".equals(className)) {
			// return "void";
			// }
		if (importType.length() < 3) {
			Class loadClass;
			// try {
			loadClass = PrimiteClassesUtils.loadPrimitiveClass(importType);
			if (loadClass != null) {
				if (loadClass.isArray()) {
					importType = loadClass.getComponentType() + "[]";
				} else {
					importType = loadClass.getName();
				}
			}
			// } catch (ClassNotFoundException e) {
			// LOG.warning(e + "");
			// }
		} else {
			importType = className.replace(";", "");
			importType = importType.substring(1);
		}
		if (importType.startsWith("L")) {
			importType = importType.substring(1) + "[]";
		}

		return importType;
	}

	private static StringLiteral findStringLiteral(String source, final int documentOffset) throws Exception {
		{
			int index = source.lastIndexOf(JrrClassUtils.class.getSimpleName(), documentOffset);
			if (index == -1) {
				return null;
			}
			String s = source.substring(index, documentOffset);
			s = s.replace("\r\n", "\n").replace("\r", "\n");
			int lengthBefore = s.length();
			int lengthAfter = s.replace("\n", "").length();
			if (lengthBefore - lengthAfter > 2) {
				return null;
			}
		}
		// TODO pass AST correspondent for project
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(source.toCharArray());
		org.eclipse.jdt.core.dom.ASTNode astNode = parser.createAST(null);
		ClassMemberClassFinder fileClassFinder = new ClassMemberClassFinder(documentOffset);
		astNode.accept(fileClassFinder);
		return fileClassFinder.getFoundedNode();
	}

	private static void findMembers(ClassMemberProposalBean cmpb, IType findType, boolean staticFields)
			throws PartInitException, JavaModelException {
		if (reFields.contains(cmpb.reflectionElement)) {
			IField[] fields = findType.getFields();
			for (IField iField : fields) {
				if (staticFields && !org.eclipse.jdt.core.Flags.isStatic(iField.getFlags())) {

				} else {
					cmpb.fields.add(iField);
				}
			}
		} else if (reMethods.contains(cmpb.reflectionElement)) {
			IMethod[] methods = findType.getMethods();
			for (IMethod iField : methods) {
				if (staticFields && !org.eclipse.jdt.core.Flags.isStatic(iField.getFlags())) {

				} else {
					cmpb.methods.add(iField);
				}
			}
		} else {
			throw new IllegalStateException(cmpb.reflectionElement + "");
		}
		String superclassName = findType.getSuperclassName();
		if (superclassName == null) {
			LOG.info("super class name is null for : " + findType.getFullyQualifiedName());
			return;
		}
		if ("java.lang.Object".equals(superclassName)) {
		} else {
			LOG.info(superclassName);
			superclassName = resolveType(superclassName, findType);
			LOG.info(superclassName);
			IType findType2 = cmpb.cu.getJavaProject().findType(superclassName);
			if (findType2 == null) {
				LOG.warning(superclassName);
			} else {
				findMembers(cmpb, findType2, staticFields);
			}
		}
		if (false) {
			try {
				JrrClassUtils.getFieldValue(String.class, "CASE_INSENSITIVE_ORDER");
				JrrClassUtils.getFieldValue(String.class, "serialPersistentFis");
				JrrClassUtils.getFieldValue(String.class, "serialPersistentFields");
				JrrClassUtils.invokeMethod(cmpb.stringLiteral, "nodeClassForType", 1);
				JrrClassUtils.invokeMethod(cmpb.stringLiteral.getClass(), "nodeClassForType", 1);
				JrrClassUtils.invokeMethod(StringLiteral.class, "nodeClassForType", 1);
				JrrClassUtils.findField(StringLiteral.class, "ANNOTATION_TYPE_DECLARATION");
				JrrClassUtils.getFieldValue(cmpb.stringLiteral, "PROPERTY_DESCRIPTORS");
				JrrClassUtils.getFieldValue(cmpb.stringLiteral, "parent");
				JrrClassUtils.invokeMethod(String.class, "length");
				// JrrClassUtils.invokeMethod(String.class, "hash");
			} catch (Exception e) {
			}
		}
	}

}
