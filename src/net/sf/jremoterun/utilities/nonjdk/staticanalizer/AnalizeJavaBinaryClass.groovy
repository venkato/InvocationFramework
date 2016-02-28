package net.sf.jremoterun.utilities.nonjdk.staticanalizer

import groovy.transform.CompileStatic
import javassist.CtConstructor
import javassist.bytecode.ClassFile
import javassist.bytecode.FieldInfo
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.StaticElementInfoJavassist
import org.apache.commons.io.IOUtils

import java.lang.reflect.Modifier
import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@CompileStatic
class AnalizeJavaBinaryClass extends AnalizeCommon<StaticElementInfoJavassist> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    static String cnrName = "L${ClRef.name.replace('.', '/')};";

    static int FIELD_NOT_FOUND = -3;


    AnalizeJavaBinaryClass(LoaderStuff loaderStuff) {
        this.loaderStuff = loaderStuff
    }

    @Override
    boolean analizeElement3(StaticElementInfoJavassist el) {
        el.ctClass = loaderStuff.loadCtClass(el.className)
        int lineNo = findLineNumber(el)
        if (lineNo == FIELD_NOT_FOUND) {
//                log.info "not found : ${el.className} ${el.fieldName} "
            return false
        } else {
            el.lineNumber = lineNo
//            log.info "${el.fieldName} ${lineNo}"
            return true
        }

    }


    int findLineNumber(StaticElementInfoJavassist el) {
        CtConstructor ctConstructor;
        if (el.isStatic()) {
            ctConstructor = el.ctClass.getClassInitializer()
            if (ctConstructor == null) {
                log.info "static init not found for : ${el.className}"
                return FIELD_NOT_FOUND
            }

        } else {
            if (el.ctClass.isInterface()) {
                return FIELD_NOT_FOUND
            }
            if (Modifier.isAbstract(el.ctClass.getModifiers())) {
                return FIELD_NOT_FOUND
            }
            ctConstructor = el.ctClass.getDeclaredConstructors().find { it.parameterTypes.length == 0 }
            if (ctConstructor == null) {
                return FIELD_NOT_FOUND;
            }
        }
        ExprEditorFindField ac = new ExprEditorFindField(el.fieldName)
        ctConstructor.instrument(ac)
        if (ac.fieldAccess == null) {
            return FIELD_NOT_FOUND;
        }
        return ac.fieldAccess.lineNumber

    }


    List<StaticElementInfoJavassist> analizeDir2(File dir) {
        List<StaticElementInfoJavassist> res = []
        analizeDir(dir, res)
        return res
    }

    void analizeDir(File dir, List<StaticElementInfoJavassist> res) {
        assert dir.exists()
        assert dir.directory
        dir.listFiles().toList().each {
            File f = it;
            try {
                if (it.isDirectory()) {
                    analizeDir(f, res)
                } else {
                    assert f.file
                    if (!f.name.contains('$') && f.name.endsWith('.class')) {
                        res.addAll analizeFile(f)
                    }
                }
            } catch (Throwable e) {
                loaderStuff.onException(e, f)
            }
        }
    }

    @Override
    List<StaticElementInfoJavassist> analizeFile(File f) {
        assert f.name.endsWith('.class')
        DataInputStream inputStream = f.newDataInputStream()
        try {
            ClassFile cf = new ClassFile(inputStream)
            return analizeClass(cf)
        } finally {
            IOUtils.closeQuietly(inputStream)
        }
//        return super.analizeFile(f)
    }

    List<StaticElementInfoJavassist> analizeJar(File jarFile) {
        List<StaticElementInfoJavassist> res = []
        assert jarFile.exists()
        assert jarFile.file
        ZipFile zipFile = new ZipFile(jarFile);
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                System.out.println(zipEntry.getName());
                String zipEnName = zipEntry.name
                try {
                    if (!zipEnName.contains('$') && zipEnName.endsWith('.class')) {
                        DataInputStream ins = new DataInputStream(zipFile.getInputStream(zipEntry));
                        try {
                            ClassFile cf = new ClassFile(ins)
                            res.addAll analizeClass(cf)
                        } finally {
                            IOUtils.closeQuietly(ins)
                        }
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


    List<StaticElementInfoJavassist> analizeClass(ClassFile classFile) {
        List<StaticElementInfoJavassist> result = []
        List<FieldInfo> fields = classFile.getFields() as List
        fields.each {
            FieldInfo fi = it;
            boolean isFile = fi.descriptor == 'Ljava/io/File;';
            boolean isCnr = fi.descriptor == cnrName;
            if (isFile || isCnr) {
                StaticElementInfoJavassist el = new StaticElementInfoJavassist();
                el.classFile = classFile;
                el.fieldInfo = fi;
                el.fieldType = isFile ? StaticFieldType.file : StaticFieldType.cnr
                result.add(el)
            }
        }
        return result;

    }


}
