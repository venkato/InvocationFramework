package net.sf.jremoterun.utilities.nonjdk.staticanalizer

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.ElementInfoGroovy
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.control.CompilePhase

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@CompileStatic
class AnalizeGroovyFile extends AnalizeCommon<ElementInfoGroovy> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    static String cnrName = "L${ClRef.name.replace('.', '/')};";


    AnalizeGroovyFile(LoaderStuff loaderStuff) {
        this.loaderStuff = loaderStuff
    }

    List<ElementInfoGroovy> analizeDir2(File dir) {
        List<ElementInfoGroovy> res = []
        analizeDir(dir, res)
        return res
    }

    void analizeDir(File dir, List<ElementInfoGroovy> res) {
        assert dir.exists()
        assert dir.directory
        dir.listFiles().toList().each {
            File f = it;
            try {
                if (it.isDirectory()) {
                    analizeDir(f, res)
                } else {
                    assert f.file
                    if (f.name.endsWith('.groovy')) {
                        if (f.length() == 0) {
                            log.info "empty file ${f}"
                        } else {
                            res.addAll analizeFile(f)
                        }
                    }
                }
            } catch (Throwable e) {
                loaderStuff.onException(e, f)
            }
        }
    }

    @Override
    boolean analizeElement3(ElementInfoGroovy el) {
        boolean b = false
        if (el.fieldNode.hasInitialExpression()) {
            b = analize3(el)
        }
        return b
    }

    List<ElementInfoGroovy> analizeJar(File jarFile) {
        List<ElementInfoGroovy> res = []
        assert jarFile.exists()
        assert jarFile.file
        ZipFile zipFile = new ZipFile(jarFile);
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                try {
                    if (zipEntry.name.endsWith('.groovy')) {
                        InputStream ins = zipFile.getInputStream(zipEntry);
                        String text = ins.text
                        res.addAll analizeClass(text, zipEntry.name)
                    }
                } catch (Throwable e) {
                    loaderStuff.onException(e, zipEntry)
                }
            }
        } finally {
            IOUtils.closeQuietly(zipFile)
        }
        return res;
    }


    @Override
    List<ElementInfoGroovy> analizeFile(File f) {
        assert f.name.endsWith('.groovy')
        return analizeClass(f.text, f.absolutePath)
    }

    List<ElementInfoGroovy> analizeClass(String groovyText, String fileName) {
        List<ElementInfoGroovy> result = []
        AstBuilder astBuilder = new AstBuilder();
        List<ASTNode> astNodes23 = astBuilder.buildFromString(CompilePhase.CONVERSION, true, groovyText);
        List<ASTNode> astNodes = astNodes23.findAll { it instanceof ClassNode }
        int nodeSize = astNodes.size();
        List<Integer> founedLine = []
        if (nodeSize == 0) {
        } else {
            ClassNode first = astNodes.first() as ClassNode;
            analizedGroovyFiles.add(first.getName())

            // ==== 1 begin ===
            ExpressionFinder expressionFinder = new ExpressionFinder();
            expressionFinder.loaderStuff = loaderStuff
            expressionFinder.ignoreLines = founedLine.toSet()



            ClassNode cn = first;
            expressionFinder.classNode = cn
            expressionFinder.elName = cn.name
            File f = fileName as File
            if (f.file) {
                expressionFinder.printablePath = fileName
            } else {
                expressionFinder.printablePath = cn.name
            }
// ==== 1 end ===

            first.fields.each {
                FieldNode fieldNode = it
                ClassNode type = fieldNode.type
                if (type != null) {

                    String withoutPackage = type.nameWithoutPackage;
                    boolean isFile = withoutPackage == 'File';
                    boolean isCnr = withoutPackage == ClRef.simpleName
                    if (isFile || isCnr) {
                        ElementInfoGroovy elementInfoGroovy = new ElementInfoGroovy();
                        elementInfoGroovy.classNode = first
                        elementInfoGroovy.printablePath = fileName
                        elementInfoGroovy.fieldNode = fieldNode
                        elementInfoGroovy.fieldType = isFile ? StaticFieldType.file : StaticFieldType.cnr
                        founedLine.add elementInfoGroovy.lineNumber
                        boolean handeled = false
                        if (fieldNode.hasInitialExpression()) {
                            Expression initialValueExpression = fieldNode.initialValueExpression
                            if (initialValueExpression instanceof CastExpression) {
                                CastExpression castExpression = (CastExpression) initialValueExpression;
                                handeled = expressionFinder.visitCastExpression1(castExpression)
                            }
                            if (initialValueExpression instanceof ConstructorCallExpression) {
                                ConstructorCallExpression constructorCallExpression = (ConstructorCallExpression) initialValueExpression;
                                handeled = expressionFinder.visitConstructorCallExpression2(constructorCallExpression)
                            }
                        }
                        if (!handeled) {
                            result.add(elementInfoGroovy)
                        }
                    }
                }
            }
        }

        // ==== 1 begin ===
        ExpressionFinder expressionFinder = new ExpressionFinder();
        expressionFinder.loaderStuff = loaderStuff
        expressionFinder.ignoreLines = founedLine.toSet()
        astNodes23.each {
            if (it instanceof ClassNode) {
                ClassNode cn = (ClassNode) it;
                expressionFinder.classNode = cn
                expressionFinder.elName = cn.name
                File f = fileName as File
                if (f.file) {
                    expressionFinder.printablePath = fileName
                } else {
                    expressionFinder.printablePath = cn.name
                }
                // ==== 1 end ===

                cn.visitContents(expressionFinder)
            } else {
                expressionFinder.elName = fileName
                expressionFinder.printablePath = fileName
//                log.info "${it.class.name}"
                it.visit(expressionFinder)
            }
        }
        return result;

    }


    String extractFromInitializer2(Expression expression) {
        if (expression instanceof ConstantExpression) {
            ConstantExpression ce = (ConstantExpression) expression;
            return ce.text
        }
        if (expression instanceof CastExpression) {
            CastExpression ce = (CastExpression) expression;
            return extractFromInitializer2(ce.expression);
        }
        if (expression instanceof ConstructorCallExpression) {
            ConstructorCallExpression ce = (ConstructorCallExpression) expression;
            Expression arguments = ce.arguments;
            if (arguments == null) {
                return null
            }
            if (arguments instanceof ArgumentListExpression) {
                ArgumentListExpression al = (ArgumentListExpression) arguments;
                List<Expression> expressions = al.expressions
                if (expressions == null || expressions.size() != 1) {
                    return null
                }
                return extractFromInitializer2(expressions.first())
            }
            return null
        }
        return null
    }

    String extractFromInitializer1(FieldNode fieldNode) {
        Expression expression = fieldNode.initialValueExpression
        if (expression == null) {
            return null
        }
        return extractFromInitializer2(expression)
    }

    /**
     * Return false - means stop analisys. <br/>
     * Return true - means continue analisys
     *
     */
    boolean analize3(ElementInfoGroovy el) {
        String initializer1 = extractFromInitializer1(el.fieldNode)
        if (initializer1 == null) {
//            String msg = "${buildLocaltion(el)} - ${initializer1}"
//            println(msg)
//            boolean evaluateInRunTime = false
            if (el.isStatic()) {
                return true
            }
            List<ConstructorNode> declaredConstructors = el.classNode.declaredConstructors
            if (declaredConstructors.size() == 0) {
                return true
            }
            if (declaredConstructors.find { it.parameters.length == 0 }) {
                return true
            }
            return false
        }
        if (el.fieldType == StaticFieldType.file) {
            if (CurrentDirDetector.isCurrentDir(initializer1)) {

            } else {
                File f3 = initializer1 as File
                if (f3.exists()) {
                    loaderStuff.onFileFound(el, f3)
                } else {
                    loaderStuff.problemFound(el, initializer1)
                }
            }
        } else {
            try {
                if (!loaderStuff.checkClassExists(initializer1)) {
                    loaderStuff.problemFound(el, initializer1)
                }
//                loaderStuff.loadClass(initializer1)
//            } catch (ClassNotFoundException cnfe) {
//                loaderStuff.problemFound(el, initializer1)
            } catch (Throwable e) {
                loaderStuff.onException(e, el)
            }
        }
        return false

    }


}
