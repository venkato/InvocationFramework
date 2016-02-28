package net.sf.jremoterun.utilities.nonjdk.langi

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.sc.StaticCompileTransformation
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor

import java.util.logging.Logger

@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.INSTRUCTION_SELECTION)
public class JrrStaticCompileTransformation extends StaticCompileTransformation {

    private static final Logger log = Logger.getLogger(JrrStaticCompileTransformation.name);

    public
    static JrrGroovyStaticCompilationVisitorFactory compilationVisitorFactory = new JrrGroovyStaticCompilationVisitorFactory();

    public static String useImprovedCastProp = "groovy.useImprovedCast"

    public static List<ASTTransformation> astTransformationsBefore = []
    public static List<ASTTransformation> astTransformationsAfter = []


    public static boolean useImprovedCast

    static {
        String propsValue = System.getProperty(useImprovedCastProp);
        boolean value4;
        if (propsValue == null) {
            value4 = true
        } else {
            value4 = "true".equalsIgnoreCase(propsValue)
        }
        useImprovedCast = value4
    }


    @Override
    public void visit(final ASTNode[] nodes, final SourceUnit source) {
//        System.out.println("hi cp1")
        if (useImprovedCast) {
            //if (!source.getErrorCollector().hasErrors()) {
            astTransformationsBefore.each {
                it.visit(nodes, source)
            }
        }
        super.visit(nodes, source)
        if (useImprovedCast) {
            astTransformationsAfter.each {
                it.visit(nodes, source)
            }
        }

    }


    @Override
    protected StaticTypeCheckingVisitor newVisitor(final SourceUnit unit, final ClassNode node) {
        return compilationVisitorFactory.create(this, unit, node);
    }

}
