package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.CompileUnit
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.ErrorCollector
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.sc.StaticCompileTransformation
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor
import org.codehaus.groovy.transform.stc.TypeCheckingContext

import java.util.logging.Logger

@CompileStatic
class GroovyFileChecker {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void analize(String groovyText) {
        ClassChecker2.classChecker2.analize2(groovyText)
    }

    static void analize2(String groovyText) {

        analize(groovyText, CompilePhase.CANONICALIZATION, true, true)
    }


    static List<ASTNode> analize(String script, CompilePhase compilePhase, boolean statementsOnly, boolean doStaticAnalisys) {
        if(script.trim().length()<1){
            throw new IllegalArgumentException("script is empty")
        }
        String scriptClassName = 'script' + System.currentTimeMillis()
        ClassLoader currentClassLoader = JrrClassUtils.currentClassLoader
        GroovyClassLoader classLoader;
        if (currentClassLoader instanceof GroovyClassLoader) {
            classLoader = (GroovyClassLoader) currentClassLoader;
        } else {
            classLoader = new GroovyClassLoader(currentClassLoader);
        }

        GroovyCodeSource codeSource = new GroovyCodeSource(script, scriptClassName + ".groovy", "/groovy/script")
        CompilationUnit cu = new CompilationUnit(CompilerConfiguration.DEFAULT, codeSource.@codeSource, classLoader)
        SourceUnit sourceUnit = cu.addSource(codeSource.getName(), script);
        cu.compile(compilePhase.getPhaseNumber())
        // collect all the ASTNodes into the result, possibly ignoring the script body if desired
        CompileUnit compileUnit = cu.getAST();
        List<ModuleNode> modules = compileUnit.modules;
        List<ASTNode> acc = []
        modules.each { ModuleNode node ->
            if (node.statementBlock) acc.add(node.statementBlock)
            node.classes?.each {
                if (!(it.name == scriptClassName && statementsOnly)) {
                    acc << it
                }
            }
            acc
        }
        acc.each {
            log.info "${it.class.name}"
        }
        ClassNode cl = acc.find { it instanceof ClassNode } as ClassNode
//        CompileUnit unit = cl.getCompileUnit()
        StaticCompileTransformation compileTransformation = new StaticCompileTransformation()
//        compileTransformation.visit([cl],sourceUnit)
        if (doStaticAnalisys) {
            if (cl == null) {
                throw new IllegalStateException("failed find class in ${acc}")
            }
            StaticTypeCheckingVisitor visitor = new StaticTypeCheckingVisitor(sourceUnit, cl)
            visitor.setCompilationUnit(cu);
            visitor.initialize();
            visitor.visitClass(cl);
            TypeCheckingContext context = visitor.getTypeCheckingContext()
            ErrorCollector collector = context.errorCollector
            if (collector.hasErrors()) {
                throw new MultipleCompilationErrorsException(collector);
            }
            log.info "${collector.hasErrors()}"
            log.info "static analisys passed"
        }

//        visitor.visitClass(cl)
        return acc
    }


}
