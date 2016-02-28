package net.sf.jremoterun.utilities.nonjdk.staticanalizer

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.expr.StringLiteralExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.JavaSourceElemntInfo

import java.util.logging.Logger

@CompileStatic
class JavaFileAnalizerVisitor extends VoidVisitorAdapter<Void> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<JavaSourceElemntInfo> els = []

    File cuPath;

    LoaderStuff loaderStuff

    HashSet<String> foundedClasses = []

    CompilationUnit cu
//    String cuClassName;
    File printablePath;

    TypeDeclaration findClass(Node node) {
        Optional<Node> parentNode = node.parentNode
        if (!parentNode.present) {
            throw new Exception("failed find class")
        }
        Node parentNode2 = parentNode.get()
        if (parentNode2 instanceof TypeDeclaration) {
            TypeDeclaration cl = (TypeDeclaration) parentNode2;
            Optional<PackageDeclaration> packageDeclaration = cu.packageDeclaration
            if(packageDeclaration.present){
                foundedClasses.add("${packageDeclaration.get().name}.${cl.name}".toString())
            }
            return cl
        }
        return findClass(parentNode2)
    }


    @Override
    void visit(final ObjectCreationExpr n, final Void arg) {
        try {
            visitImpl(n, arg)
        }catch (Exception e){
            log.info "failed analize ${cu.primaryTypeName} , line = ${n.getBegin()}, el = ${n}"
            throw e
        }
    }

    void visitImpl(final ObjectCreationExpr n, final Void arg) {
//        log.info "${n.getType()} ${n.getAllContainedComments()} ${n} ${n.getBegin()}"
//        int lineNo = n.getBegin().get().line
        FieldDeclaration fd
        VariableDeclarator vd
        Node parentNode = n.parentNode.get()
        if (parentNode instanceof VariableDeclarator) {
            vd = (VariableDeclarator) parentNode;
            Node parent2 = vd.parentNode.get()
            if (parent2 instanceof FieldDeclaration) {
                fd = (FieldDeclaration) parent2;
            }
        }
        boolean handled = false

//        if (n.type.name.asString() == 'File') {
        NodeList<Expression> arguments = n.getArguments()
        if (arguments.size() == 1) {
            Expression arg1 = arguments.get(0)
            if (arg1 instanceof StringLiteralExpr) {
                StringLiteralExpr sl = (StringLiteralExpr) arg1;
                String elementContent1 = sl.asString()
                JavaSourceElemntInfo elementMethodGroovy = new JavaSourceElemntInfo();
                elementMethodGroovy.fieldNode = vd
                elementMethodGroovy.classNode = cu
                elementMethodGroovy.printablePath2 = printablePath
                elementMethodGroovy.expression = n
                elementMethodGroovy.clOrInt = findClass(parentNode)
                switch (n.type.name.asString()) {
                    case 'File':
                        if(CurrentDirDetector. isCurrentDir(elementContent1)){

                        }else {
                            File elementContent2 = elementContent1 as File
                            elementMethodGroovy.fieldType = StaticFieldType.file;
//                        els.add(elementMethodGroovy)
                            elementMethodGroovy.isParentFile = true
                            if (elementContent2.exists()) {
                                loaderStuff.onFileFound(elementMethodGroovy, elementContent2)
                            } else {
                                loaderStuff.problemFound(elementMethodGroovy, elementContent1)
                            }
                            handled = true
                        }
                        break
                    case ClRef.simpleName:
//                        log.info "cp4 : ${fd}"
                        elementMethodGroovy.fieldType = StaticFieldType.cnr;
                        if (!loaderStuff.checkClassExists(elementContent1)) {
                            loaderStuff.problemFound(elementMethodGroovy, elementContent1)
                        }
//                        els.add(elementMethodGroovy)
                        handled = true
                        break
                    default:
                        break;
                }
//                elementMethodGroovy.fieldType = StaticFieldType.file

            }
        }
//        }
        if (!handled && fd != null) {
            JavaSourceElemntInfo elementMethodGroovy = new JavaSourceElemntInfo();
            elementMethodGroovy.fieldNode = vd
            elementMethodGroovy.classNode = cu
            elementMethodGroovy.printablePath2 = printablePath
            elementMethodGroovy.fieldType = StaticFieldType.file
            elementMethodGroovy.isParentFile = true
            elementMethodGroovy.expression = n
            elementMethodGroovy.clOrInt = findClass(parentNode)
            elementMethodGroovy.fieldDeclaration = fd
            switch (n.type.name.asString()) {
                case 'File':
                    elementMethodGroovy.fieldType = StaticFieldType.file;
                    els.add(elementMethodGroovy)
                    break
                case ClRef.simpleName:
//                    log.info "cp5 : ${fd}"
                    elementMethodGroovy.fieldType = StaticFieldType.cnr;
                    els.add(elementMethodGroovy)
                    break
                default:
                    break;
            }
        }
    }


}
