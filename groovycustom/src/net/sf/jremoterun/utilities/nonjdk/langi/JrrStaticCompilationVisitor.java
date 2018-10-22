package net.sf.jremoterun.utilities.nonjdk.langi;

import groovy.transform.CompileStatic;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.syntax.Types;
import org.codehaus.groovy.transform.sc.StaticCompilationVisitor;
import org.codehaus.groovy.transform.stc.Receiver;

import java.util.*;
import java.util.logging.Logger;

@CompileStatic
public class JrrStaticCompilationVisitor extends StaticCompilationVisitor {
    public JrrStaticCompilationVisitor(SourceUnit unit, ClassNode node) {
        super(unit, node);
    }


    private static final Logger log = Logger.getLogger(JrrStaticCompilationVisitor.class.getName());

    @Override
    public void visitStaticMethodCallExpression(final StaticMethodCallExpression call) {
        super.visitStaticMethodCallExpression(call);
    }

    @Override
    protected void addReceivers(List<Receiver<String>> receivers, Collection<Receiver<String>> owners, boolean implicitThis) {
        super.addReceivers(receivers, owners, implicitThis);
//        log.info "receivers : ${receivers} , owners : ${owners}"
    }

    @Override
    protected List<MethodNode> findMethod(ClassNode receiver, String name, ClassNode... args) {
        return super.findMethod(receiver, name, args);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        super.visitBinaryExpression(expression);
//        Token operation = expression.operation
//        int op = expression.getOperation().getType()
//        if (op == KEYWORD_INSTANCEOF) {
//
//            pushInstanceOfTypeInfo(leftExpression, rightExpression);
//        }
    }

    @Override
    public void visitMethod(MethodNode node) {
        super.visitMethod(node);

    }


    protected IdentityHashMap<BlockStatement, Map<VariableExpression, List<ClassNode>>> blockStatements2Types = new IdentityHashMap();


    protected LinkedList<BlockStatement> enclosingBlocks = new LinkedList();

    @Override
    public void visitBlockStatement(BlockStatement block) {
        if (block != null) {
            enclosingBlocks.addFirst(block);
        }
        super.visitBlockStatement(block);
        if (block != null) {
            visitClosingBlockJrr(block);
        }
    }

    public void visitClosingBlockJrr(BlockStatement block) {
        if(enclosingBlocks.size()==0){
            throw new NoSuchElementException("collection is empty");
        }
        BlockStatement first = enclosingBlocks.removeFirst();
        boolean found = blockStatements2Types.containsKey(first);
        if (found) {
            Map<VariableExpression, List<ClassNode>> oldTracker = blockStatements2Types.get(first);
            getTypeCheckingContext().popTemporaryTypeInfo();
            popAssignmentTracking(oldTracker);
            blockStatements2Types.remove(first);
        }
    }

    /**
     * Check IfStatement matched pattern :
     * Object var1;
     * if (!(var1 instanceOf Runnable)){
     * return
     * }
     * // Here var1 instance of Runnable
     *
     * @return expression , which contains instanceOf (without not)
     */
    public BinaryExpression findInstanceOfNotReturnExpression(IfStatement ifElse) {
        Statement elseBlock = ifElse.getElseBlock();
        if (!(elseBlock instanceof EmptyStatement)) {
            return null;
        }
        Expression conditionExpression = ifElse.getBooleanExpression().getExpression();
        if (!(conditionExpression instanceof NotExpression)) {
            return null;
        }
        NotExpression notExpression = (NotExpression) conditionExpression;
        Expression expression = notExpression.getExpression();
        if (!(expression instanceof BinaryExpression)) {
            return null;
        }
        BinaryExpression instanceOfExpression = (BinaryExpression) expression;
        int op = instanceOfExpression.getOperation().getType();
        if (op != Types.KEYWORD_INSTANCEOF) {
            return null;
        }
        Statement block = ifElse.getIfBlock();
        if (!(block instanceof BlockStatement)) {
            return null;
        }
        BlockStatement bs = (BlockStatement) block;
        if (bs.getStatements().size() == 0) {
            return null;
        }
        Statement last = DefaultGroovyMethods.last(bs.getStatements());
        if (last instanceof ReturnStatement) {
            return instanceOfExpression;
        }
        if (last instanceof ThrowStatement) {
            return instanceOfExpression;
        }
        return null;
    }


    @Override
    public void visitIfElse(final IfStatement ifElse) {
        super.visitIfElse(ifElse);
        BinaryExpression instanceOfExpression = findInstanceOfNotReturnExpression(ifElse);
        if (instanceOfExpression == null) {
        } else {
            visitInstanceofNot(instanceOfExpression);
        }

    }

    public void visitInstanceofNot(BinaryExpression be) {
        final BlockStatement currentBlock = enclosingBlocks.getFirst();
        assert currentBlock != null;
        if (blockStatements2Types.containsKey(currentBlock)) {
            // another instanceOf not was before
        } else {
            Map<VariableExpression, List<ClassNode>> oldTracker = pushAssignmentTracking();
            getTypeCheckingContext().pushTemporaryTypeInfo();
            blockStatements2Types.put(currentBlock, oldTracker);
        }
        pushInstanceOfTypeInfo(be.getLeftExpression(), be.getRightExpression());
    }


}
