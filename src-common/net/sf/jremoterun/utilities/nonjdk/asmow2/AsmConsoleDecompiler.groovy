package net.sf.jremoterun.utilities.nonjdk.asmow2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.util.ASMifier
import org.objectweb.asm.util.Printer
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor;

import java.util.logging.Logger;

@CompileStatic
class AsmConsoleDecompiler implements ClassNameSynonym{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static int asmCode = Opcodes.ASM7
    public static int options = 0


    /**
     * options - 0 for all,
     * @see org.objectweb.asm.ClassReader#SKIP_DEBUG
     */
    void printAll(String className, boolean asAsm) {
        ClassReader cr = new ClassReader(className);
        String code2 = printAll2(cr, asAsm)
        log.info "code : \n ${code2}"
    }

    String printAll2(ClassReader cr, boolean asAsm) {
        Printer printer = asAsm ? new Textifier() : new ASMifier()
        StringWriter stringWriter = new StringWriter()
        TraceClassVisitor visitor = new TraceClassVisitor(null, printer, new PrintWriter(stringWriter, true))
        cr.accept(visitor, options);
//        String join = printer.text.join('')
        String code2 = stringWriter.toString()
        return code2
    }

    void printMethod(String className, String methodName, boolean asAsm) {
        ClassReader cr = new ClassReader(className);
        String join = printMethod2(cr, methodName, asAsm)
        log.info "code : \n ${join}"
    }

    String printMethod2(ClassReader cr, String methodName, boolean asAsm) {
        Printer printer = asAsm ? new Textifier() : new ASMifier()
        AsmConsoleDecompilerVisitor visitor = new AsmConsoleDecompilerVisitor(asmCode, null, methodName, printer)
        cr.accept(visitor, options);
        if (!visitor.found) {
            throw new NoSuchMethodException("${cr.className} ${methodName}")
        }
        String join = printer.text.join('')
        return join
    }

}
