package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.control.CompilePhase

import java.util.logging.Logger

@CompileStatic
class FindGradleDependencies extends CodeVisitorSupport {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<MavenId> foundDeps = [];
    boolean analize = false;


    static List<MavenId> findDeps(String groovyText) {
        AstBuilder astBuilder = new AstBuilder();
        List<ASTNode> astNodes = astBuilder.buildFromString(CompilePhase.CONVERSION,true,groovyText);
        FindGradleDependencies findGradleDependencies = new FindGradleDependencies();
        astNodes.each {
            it.visit(findGradleDependencies)
        }
        return findGradleDependencies.foundDeps
    }

    @Override
    void visitArgumentlistExpression(ArgumentListExpression ale) {
        if (analize) {
//            log.info "cp1"
            List<Expression> expressions = ale.getExpressions();

            if ((expressions.size() == 1) && (expressions.get(0) instanceof ConstantExpression)) {
                String depStr = expressions.get(0).getText();
                List<String> strings = depStr.tokenize(':')
                if (strings.size() == 3) {
                    foundDeps.add(new MavenId(depStr));
                } else {
                    log.info "skip dep ${depStr} at line ${ale.lineNumber}"
                }
            }
        }
        super.visitArgumentlistExpression(ale);
    }

    @Override
    void visitMapExpression(MapExpression expression) {
        if (analize) {
//            log.info "cp2"
            Map<String, String> dependencyMap = [:];
            for (MapEntryExpression mapEntryExpression : expression.getMapEntryExpressions()) {
                String entryKey = mapEntryExpression.getKeyExpression().getText();
                String entryValue = mapEntryExpression.getValueExpression().getText();
                dependencyMap.put(entryKey, entryValue);
            }
            if (dependencyMap.size() == 3) {
                MavenId mavenId = new MavenId(dependencyMap.get('group'), dependencyMap.get('name'), dependencyMap.get('version'))
                foundDeps.add(mavenId);
            } else {
                log.info "strange deps : ${dependencyMap} at line ${expression.lineNumber}, skip it"
            }
        }
        super.visitMapExpression(expression);
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        String methodName = call.getMethodAsString()
//        log.info "${methodName}"
        if (methodName != "buildscript") {
            boolean inCsope = false
            if (methodName == "dependencies") {
                analize = true
                inCsope = true
            }
            super.visitMethodCallExpression(call);
            if(inCsope){
                analize = false
            }

        }
    }



}