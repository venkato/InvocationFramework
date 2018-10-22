package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBuildRunnerSettings
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.LauncherImpl

import java.util.logging.Logger

@CompileStatic
class IdeaBRunner33 implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile AddFilesToClassLoaderGroovy adder2;

    public static enum A implements ClRefRef {
        addMavenIds(new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2.AddMavenIds')),
        ivyDepSetter(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.IvyDepResolverSetter')),
        redir(new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2.Redirector')),
        runnerImpl(new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2.IdeaBRunner34'))
        ;

        ClRef clRef;

        A(ClRef clRef) { this.clRef = clRef }
    }


    @Override
    void run() {
        f1()
    }


    static void f1() {
        log.info "loading framework2"
        RunnableFactory.runRunner A.addMavenIds
        RunnableFactory.runRunner A.ivyDepSetter
        RunnableFactory.runRunner A.redir
        log.info "redirector set 2"
        RunnableFactory.runRunner A.runnerImpl

        new ClRef('org.jetbrains.jps.incremental.groovy.GroovycOutputParser');
        new ClRef('org.jetbrains.jps.incremental.groovy.GreclipseBuilder');
        // goovyc compiler
        new ClRef('org.jetbrains.groovy.compiler.rt.GroovyCompilerWrapper');
        new ClRef('org.jetbrains.groovy.compiler.rt.DependentGroovycRunner');

    }
}


/*
SEVERE failed load org.codehaus.groovy.runtime.SqlGroovyMethods java.lang.ClassNotFoundException: org.codehaus.groovy.runtime.SqlGroovyMethods
	at java.net.URLClassLoader.findClass(URLClassLoader.java:382)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418)
	at groovy.lang.GroovyClassLoader.loadClass(GroovyClassLoader.java:869)
	at groovy.lang.GroovyClassLoader.loadClass(GroovyClassLoader.java:979)
	at groovy.lang.GroovyClassLoader.loadClass(GroovyClassLoader.java:967)
	at org.codehaus.groovy.runtime.m12n.MetaInfExtensionModule.loadExtensionClass(MetaInfExtensionModule.java:88)
	at org.codehaus.groovy.runtime.m12n.MetaInfExtensionModule.newModule(MetaInfExtensionModule.java:73)
	at org.codehaus.groovy.runtime.m12n.StandardPropertiesModuleFactory.newModule(StandardPropertiesModuleFactory.java:50)
	at org.codehaus.groovy.runtime.m12n.ExtensionModuleScanner.scanExtensionModuleFromProperties(ExtensionModuleScanner.java:86)
	at org.codehaus.groovy.runtime.m12n.ExtensionModuleScanner.scanExtensionModuleFromMetaInf(ExtensionModuleScanner.java:81)
	at org.codehaus.groovy.runtime.m12n.ExtensionModuleScanner.scanClasspathModulesFrom(ExtensionModuleScanner.java:63)
	at org.codehaus.groovy.runtime.m12n.ExtensionModuleScanner.scanClasspathModules(ExtensionModuleScanner.java:55)
	at org.codehaus.groovy.transform.stc.AbstractExtensionMethodCache.getMethodsFromClassLoader(AbstractExtensionMethodCache.java:71)
	at org.codehaus.groovy.runtime.memoize.StampedCommonCache.compute(StampedCommonCache.java:163)
	at org.codehaus.groovy.runtime.memoize.StampedCommonCache.getAndPut(StampedCommonCache.java:154)
	at org.codehaus.groovy.runtime.memoize.StampedCommonCache.getAndPut(StampedCommonCache.java:115)
	at org.codehaus.groovy.transform.stc.AbstractExtensionMethodCache.get(AbstractExtensionMethodCache.java:51)
	at org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport.findDGMMethodsForClassNode(StaticTypeCheckingSupport.java:309)
	at org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport.findDGMMethodsForClassNode(StaticTypeCheckingSupport.java:296)
	at org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor.visitMethodPointerExpression(StaticTypeCheckingVisitor.java:2405)
	at org.codehaus.groovy.ast.expr.MethodPointerExpression.visit(MethodPointerExpression.java:55)
	at org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor.visitMethodCallArguments(StaticTypeCheckingVisitor.java:2717)
	at org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor.visitMethodCallExpression(StaticTypeCheckingVisitor.java:3335)
	at org.codehaus.groovy.transform.sc.StaticCompilationVisitor.visitMethodCallExpression(StaticCompilationVisitor.java:411)
	at org.codehaus.groovy.ast.expr.MethodCallExpression.visit(MethodCallExpression.java:77)
	at org.codehaus.groovy.ast.CodeVisitorSupport.visitExpressionStatement(CodeVisitorSupport.java:117)
	at org.codehaus.groovy.ast.ClassCodeVisitorSupport.visitExpressionStatement(ClassCodeVisitorSupport.java:252)
	at org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor.visitExpressionStatement(StaticTypeCheckingVisitor.java:2118)
	at org.codehaus.groovy.ast.stmt.ExpressionStatement.visit(ExpressionStatement.java:40)
	at org.codehaus.groovy.ast.CodeVisitorSupport.visitBlockStatement(CodeVisitorSupport.java:86)
	at org.codehaus.groovy.ast.ClassCodeVisitorSupport.visitBlockStatement(ClassCodeVisitorSupport.java:216)
	at org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor.visitBlockStatement(StaticTypeCheckingVisitor.java:3931)
	at org.codehaus.groovy.ast.stmt.BlockStatement.visit(BlockStatement.java:69)
	at org.codehaus.groovy.ast.ClassCodeVisitorSupport.visitClassCodeContainer(ClassCodeVisitorSupport.java:165)
	at org.codehaus.groovy.ast.ClassCodeVisitorSupport.visitConstructorOrMethod(ClassCodeVisitorSupport.java:138)
	at org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor.visitConstructorOrMethod(StaticTypeCheckingVisitor.java:2107)
	at org.codehaus.groovy.ast.ClassCodeVisitorSupport.visitMethod(ClassCodeVisitorSupport.java:133)
	at org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor.startMethodInference(StaticTypeCheckingVisitor.java:2539)
	at org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor.visitMethod(StaticTypeCheckingVisitor.java:2497)
	at org.codehaus.groovy.transform.sc.StaticCompilationVisitor.visitMethod(StaticCompilationVisitor.java:236)
	at org.codehaus.groovy.ast.ClassNode.visitMethods(ClassNode.java:1164)
	at org.codehaus.groovy.ast.ClassNode.visitContents(ClassNode.java:1157)
	at org.codehaus.groovy.ast.ClassCodeVisitorSupport.visitClass(ClassCodeVisitorSupport.java:56)
	at org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor.visitClass(StaticTypeCheckingVisitor.java:406)
	at org.codehaus.groovy.transform.sc.StaticCompilationVisitor.visitClass(StaticCompilationVisitor.java:194)
	at org.codehaus.groovy.transform.sc.StaticCompileTransformation.visit(StaticCompileTransformation.java:65)
	at org.codehaus.groovy.transform.ASTTransformationVisitor.visitClass(ASTTransformationVisitor.java:188)
	at org.codehaus.groovy.transform.ASTTransformationVisitor.lambda$2(ASTTransformationVisitor.java:282)
	at org.codehaus.groovy.control.CompilationUnit$IPrimaryClassNodeOperation.doPhaseOperation(CompilationUnit.java:980)
	at org.codehaus.groovy.control.CompilationUnit.processPhaseOperations(CompilationUnit.java:689)
	at org.codehaus.groovy.control.CompilationUnit.compile(CompilationUnit.java:651)
	at org.codehaus.jdt.groovy.internal.compiler.ast.GroovyCompilationUnitDeclaration.processToPhase(GroovyCompilationUnitDeclaration.java:225)
	at org.codehaus.jdt.groovy.internal.compiler.ast.GroovyCompilationUnitDeclaration.generateCode(GroovyCompilationUnitDeclaration.java:312)
	at org.eclipse.jdt.internal.compiler.Compiler.process(Compiler.java:925)
	at org.eclipse.jdt.internal.compiler.Compiler.processCompiledUnits(Compiler.java:585)
	at org.eclipse.jdt.internal.compiler.Compiler.compile(Compiler.java:485)
	at org.eclipse.jdt.internal.compiler.Compiler.compile(Compiler.java:436)
	at org.eclipse.jdt.internal.compiler.batch.Main.performCompilation(Main.java:4801)
	at org.eclipse.jdt.internal.compiler.batch.Main.compile(Main.java:1801)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.jetbrains.jps.incremental.groovy.GreclipseBuilder.performCompilationInner(GreclipseBuilder.java:258)
	at org.jetbrains.jps.incremental.groovy.GreclipseBuilder.performCompilation(GreclipseBuilder.java:215)
	at org.jetbrains.jps.incremental.groovy.GreclipseBuilder.build(GreclipseBuilder.java:169)
	at org.jetbrains.jps.incremental.IncProjectBuilder.runModuleLevelBuilders(IncProjectBuilder.java:1414)
	at org.jetbrains.jps.incremental.IncProjectBuilder.runBuildersForChunk(IncProjectBuilder.java:1092)
	at org.jetbrains.jps.incremental.IncProjectBuilder.buildTargetsChunk(IncProjectBuilder.java:1159)
	at org.jetbrains.jps.incremental.IncProjectBuilder.buildChunkIfAffected(IncProjectBuilder.java:1053)
	at org.jetbrains.jps.incremental.IncProjectBuilder.buildChunks(IncProjectBuilder.java:882)
	at org.jetbrains.jps.incremental.IncProjectBuilder.runBuild(IncProjectBuilder.java:449)
	at org.jetbrains.jps.incremental.IncProjectBuilder.build(IncProjectBuilder.java:190)
	at org.jetbrains.jps.cmdline.BuildRunner.runBuild(BuildRunner.java:138)
	at org.jetbrains.jps.cmdline.BuildSession.runBuild(BuildSession.java:297)
	at org.jetbrains.jps.cmdline.BuildSession.run(BuildSession.java:130)
	at org.jetbrains.jps.cmdline.BuildMain$MyMessageHandler.lambda$channelRead0$0(BuildMain.java:218)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)


































net.sf.jremoterun.utilities.nonjdk.log.JdkLoggerExtentionClass java.lang.ClassNotFoundException: net.sf.jremoterun.utilities.nonjdk.log.JdkLoggerExtentionClass
	at com.intellij.util.lang.UrlClassLoader.findClass(UrlClassLoader.java:328)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:351)
	at org.codehaus.groovy.runtime.m12n.MetaInfExtensionModule.loadExtensionClass(MetaInfExtensionModule.java:88)
	at org.codehaus.groovy.runtime.m12n.MetaInfExtensionModule.newModule(MetaInfExtensionModule.java:73)
	at org.codehaus.groovy.runtime.m12n.StandardPropertiesModuleFactory.newModule(StandardPropertiesModuleFactory.java:50)
	at org.codehaus.groovy.runtime.m12n.ExtensionModuleScanner.scanExtensionModuleFromProperties(ExtensionModuleScanner.java:86)
	at org.codehaus.groovy.runtime.m12n.ExtensionModuleScanner.scanExtensionModuleFromMetaInf(ExtensionModuleScanner.java:81)
	at org.codehaus.groovy.runtime.m12n.ExtensionModuleScanner.scanClasspathModulesFrom(ExtensionModuleScanner.java:63)
	at org.codehaus.groovy.runtime.m12n.ExtensionModuleScanner.scanClasspathModules(ExtensionModuleScanner.java:55)
	at org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl.<init>(MetaClassRegistryImpl.java:125)
	at org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl.<init>(MetaClassRegistryImpl.java:86)
	at groovy.lang.GroovySystem.<clinit>(GroovySystem.java:37)
	at org.codehaus.groovy.runtime.InvokerHelper.<clinit>(InvokerHelper.java:88)
	at groovy.lang.GroovyObjectSupport.getDefaultMetaClass(GroovyObjectSupport.java:46)
	at groovy.lang.GroovyObjectSupport.<init>(GroovyObjectSupport.java:32)
	at groovy.lang.Closure.<init>(Closure.java:211)
	at groovy.lang.Closure.<init>(Closure.java:228)
	at groovy.lang.Closure$1.<init>(Closure.java:193)
	at groovy.lang.Closure.<clinit>(Closure.java:193)
	at org.apache.groovy.parser.antlr4.util.StringUtils.replaceLineEscape(StringUtils.java:143)
	at org.apache.groovy.parser.antlr4.util.StringUtils.replaceEscapes(StringUtils.java:133)
	at org.apache.groovy.parser.antlr4.util.StringUtils.replaceEscapes(StringUtils.java:118)
	at org.apache.groovy.parser.antlr4.AstBuilder.parseStringLiteral(AstBuilder.java:2664)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitStringLiteral(AstBuilder.java:2635)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitStringLiteral(AstBuilder.java:341)
	at org.apache.groovy.parser.antlr4.GroovyParser$StringLiteralContext.accept(GroovyParser.java:12180)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visitChildren(AbstractParseTreeVisitor.java:48)
	at org.apache.groovy.parser.antlr4.GroovyParserBaseVisitor.visitStringLiteralAlt(GroovyParserBaseVisitor.java:130)
	at org.apache.groovy.parser.antlr4.GroovyParser$StringLiteralAltContext.accept(GroovyParser.java:4137)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visitChildren(AbstractParseTreeVisitor.java:48)
	at org.apache.groovy.parser.antlr4.GroovyParserBaseVisitor.visitLiteralPrmrAlt(GroovyParserBaseVisitor.java:34)
	at org.apache.groovy.parser.antlr4.GroovyParser$LiteralPrmrAltContext.accept(GroovyParser.java:10117)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visit(AbstractParseTreeVisitor.java:20)
	at org.apache.groovy.parser.antlr4.AstBuilder.visit(AstBuilder.java:4218)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitPathExpression(AstBuilder.java:2251)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitPostfixExpression(AstBuilder.java:2733)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitPostfixExpression(AstBuilder.java:341)
	at org.apache.groovy.parser.antlr4.GroovyParser$PostfixExpressionContext.accept(GroovyParser.java:8092)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visitChildren(AbstractParseTreeVisitor.java:48)
	at org.apache.groovy.parser.antlr4.GroovyParserBaseVisitor.visitPostfixExprAlt(GroovyParserBaseVisitor.java:162)
	at org.apache.groovy.parser.antlr4.GroovyParser$PostfixExprAltContext.accept(GroovyParser.java:8173)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visit(AbstractParseTreeVisitor.java:20)
	at org.apache.groovy.parser.antlr4.AstBuilder.visit(AstBuilder.java:4218)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitExpressionListElement(AstBuilder.java:3397)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitEnhancedArgumentListElement(AstBuilder.java:2619)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitEnhancedArgumentListInPar(AstBuilder.java:2556)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitArguments(AstBuilder.java:2542)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitPathElement(AstBuilder.java:2340)
	at org.apache.groovy.parser.antlr4.AstBuilder.lambda$createPathExpression$34(AstBuilder.java:4308)
	at java.util.stream.ReduceOps$1ReducingSink.accept(ReduceOps.java:80)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.reduce(ReferencePipeline.java:541)
	at org.apache.groovy.parser.antlr4.AstBuilder.createPathExpression(AstBuilder.java:4304)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitPathExpression(AstBuilder.java:2254)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitPostfixExpression(AstBuilder.java:2733)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitPostfixExpression(AstBuilder.java:341)
	at org.apache.groovy.parser.antlr4.GroovyParser$PostfixExpressionContext.accept(GroovyParser.java:8092)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visitChildren(AbstractParseTreeVisitor.java:48)
	at org.apache.groovy.parser.antlr4.GroovyParserBaseVisitor.visitPostfixExprAlt(GroovyParserBaseVisitor.java:162)
	at org.apache.groovy.parser.antlr4.GroovyParser$PostfixExprAltContext.accept(GroovyParser.java:8173)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visit(AbstractParseTreeVisitor.java:20)
	at org.apache.groovy.parser.antlr4.AstBuilder.visit(AstBuilder.java:4218)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitCommandExpression(AstBuilder.java:2043)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitCommandExprAlt(AstBuilder.java:2028)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitCommandExprAlt(AstBuilder.java:341)
	at org.apache.groovy.parser.antlr4.GroovyParser$CommandExprAltContext.accept(GroovyParser.java:8051)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visit(AbstractParseTreeVisitor.java:20)
	at org.apache.groovy.parser.antlr4.AstBuilder.visit(AstBuilder.java:4218)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitEnhancedStatementExpression(AstBuilder.java:2234)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitVariableInitializer(AstBuilder.java:1984)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitVariableDeclarator(AstBuilder.java:1973)
	at org.apache.groovy.parser.antlr4.AstBuilder.lambda$visitVariableDeclarators$17(AstBuilder.java:1946)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:566)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitVariableDeclarators(AstBuilder.java:1949)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitVariableDeclaration(AstBuilder.java:1783)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitLocalVariableDeclaration(AstBuilder.java:1738)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitBlockStatement(AstBuilder.java:4013)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:566)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitBlockStatements(AstBuilder.java:4006)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitBlockStatementsOpt(AstBuilder.java:3993)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitClosure(AstBuilder.java:3655)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitClosureOrLambdaExpression(AstBuilder.java:3981)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitPathElement(AstBuilder.java:2404)
	at org.apache.groovy.parser.antlr4.AstBuilder.lambda$createPathExpression$34(AstBuilder.java:4308)
	at java.util.stream.ReduceOps$1ReducingSink.accept(ReduceOps.java:80)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.reduce(ReferencePipeline.java:541)
	at org.apache.groovy.parser.antlr4.AstBuilder.createPathExpression(AstBuilder.java:4304)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitPathExpression(AstBuilder.java:2254)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitPostfixExpression(AstBuilder.java:2733)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitPostfixExpression(AstBuilder.java:341)
	at org.apache.groovy.parser.antlr4.GroovyParser$PostfixExpressionContext.accept(GroovyParser.java:8092)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visitChildren(AbstractParseTreeVisitor.java:48)
	at org.apache.groovy.parser.antlr4.GroovyParserBaseVisitor.visitPostfixExprAlt(GroovyParserBaseVisitor.java:162)
	at org.apache.groovy.parser.antlr4.GroovyParser$PostfixExprAltContext.accept(GroovyParser.java:8173)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visit(AbstractParseTreeVisitor.java:20)
	at org.apache.groovy.parser.antlr4.AstBuilder.visit(AstBuilder.java:4218)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitCommandExpression(AstBuilder.java:2043)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitCommandExprAlt(AstBuilder.java:2028)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitCommandExprAlt(AstBuilder.java:341)
	at org.apache.groovy.parser.antlr4.GroovyParser$CommandExprAltContext.accept(GroovyParser.java:8051)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visit(AbstractParseTreeVisitor.java:20)
	at org.apache.groovy.parser.antlr4.AstBuilder.visit(AstBuilder.java:4218)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitEnhancedStatementExpression(AstBuilder.java:2234)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitVariableInitializer(AstBuilder.java:1984)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitVariableDeclarator(AstBuilder.java:1973)
	at org.apache.groovy.parser.antlr4.AstBuilder.lambda$visitVariableDeclarators$17(AstBuilder.java:1946)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:566)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitVariableDeclarators(AstBuilder.java:1949)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitVariableDeclaration(AstBuilder.java:1783)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitLocalVariableDeclaration(AstBuilder.java:1738)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitBlockStatement(AstBuilder.java:4013)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:566)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitBlockStatements(AstBuilder.java:4006)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitBlockStatementsOpt(AstBuilder.java:3993)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitBlock(AstBuilder.java:2022)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitMethodBody(AstBuilder.java:1733)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitMethodDeclaration(AstBuilder.java:1525)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitMemberDeclaration(AstBuilder.java:1411)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitClassBodyDeclaration(AstBuilder.java:1387)
	at org.apache.groovy.parser.antlr4.AstBuilder.lambda$visitClassBody$9(AstBuilder.java:1260)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitClassBody(AstBuilder.java:1258)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitClassDeclaration(AstBuilder.java:1216)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitTypeDeclaration(AstBuilder.java:1056)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitTypeDeclaration(AstBuilder.java:341)
	at org.apache.groovy.parser.antlr4.GroovyParser$TypeDeclarationContext.accept(GroovyParser.java:678)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visitChildren(AbstractParseTreeVisitor.java:48)
	at org.apache.groovy.parser.antlr4.GroovyParserBaseVisitor.visitScriptStatement(GroovyParserBaseVisitor.java:466)
	at org.apache.groovy.parser.antlr4.GroovyParser$ScriptStatementContext.accept(GroovyParser.java:471)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visit(AbstractParseTreeVisitor.java:20)
	at org.apache.groovy.parser.antlr4.AstBuilder.visit(AstBuilder.java:4218)
	at org.apache.groovy.parser.antlr4.AstBuilder.lambda$visitScriptStatements$0(AstBuilder.java:476)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:566)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitScriptStatements(AstBuilder.java:477)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitCompilationUnit(AstBuilder.java:434)
	at org.apache.groovy.parser.antlr4.AstBuilder.visitCompilationUnit(AstBuilder.java:341)
	at org.apache.groovy.parser.antlr4.GroovyParser$CompilationUnitContext.accept(GroovyParser.java:317)
	at groovyjarjarantlr4.v4.runtime.tree.AbstractParseTreeVisitor.visit(AbstractParseTreeVisitor.java:20)
	at org.apache.groovy.parser.antlr4.AstBuilder.visit(AstBuilder.java:4218)
	at org.apache.groovy.parser.antlr4.AstBuilder.buildAST(AstBuilder.java:424)
	at org.apache.groovy.parser.antlr4.Antlr4ParserPlugin.buildAST(Antlr4ParserPlugin.java:58)
	at org.codehaus.groovy.control.SourceUnit.buildAST(SourceUnit.java:257)
	at java.util.Iterator.forEachRemaining(Iterator.java:116)
	at java.util.Spliterators$IteratorSpliterator.forEachRemaining(Spliterators.java:1801)
	at java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:647)
	at org.codehaus.groovy.control.CompilationUnit.buildASTs(CompilationUnit.java:666)
	at org.codehaus.groovy.control.CompilationUnit.compile(CompilationUnit.java:632)
	at org.jetbrains.groovy.compiler.rt.GroovyCompilerWrapper.compile(GroovyCompilerWrapper.java:62)
	at org.jetbrains.groovy.compiler.rt.DependentGroovycRunner.runGroovyc(DependentGroovycRunner.java:119)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.jetbrains.groovy.compiler.rt.GroovycRunner.intMain2(GroovycRunner.java:81)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.jetbrains.jps.incremental.groovy.InProcessGroovyc.runGroovycInThisProcess(InProcessGroovyc.java:167)
	at org.jetbrains.jps.incremental.groovy.InProcessGroovyc.lambda$runGroovyc$0(InProcessGroovyc.java:77)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)


 */