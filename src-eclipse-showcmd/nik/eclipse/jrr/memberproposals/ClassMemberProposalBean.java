package nik.eclipse.jrr.memberproposals;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

public class ClassMemberProposalBean {

    public volatile int selctedRange;
    StyledText styledText;
    CompilationUnitEditor cue;
    CompilationUnit cu;
    protected volatile int textLength;
//    public IType type;
    public ReflectionElement reflectionElement;
    public Collection<IField> fields=new ArrayList();
    public Collection<IMethod> methods=new ArrayList();
    public int diff;
	public int documentOffset;
//	public KeyCompetionEnum keyStoke;
	public StyleRange[] ranges;
	public StringLiteral stringLiteral;
	public IType typeOfFirstArgs;
	public boolean staticKnow=false;

	public int firestSelecttionInStyledText;

//	public boolean open;
//
//	public IMember exactMatch;

}
