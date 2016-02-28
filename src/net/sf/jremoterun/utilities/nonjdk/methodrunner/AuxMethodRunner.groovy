package net.sf.jremoterun.utilities.nonjdk.methodrunner;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ConsoleSymbols
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.st.GroovyMethodFinder
import net.sf.jremoterun.utilities.groovystarter.st.GroovyMethodFinderException

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class AuxMethodRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    GroovyMethodFinder groovyMethodFinder = new GroovyMethodFinder()

    void invokeMethod(Object onObject){
        List<String> args = GroovyMethodRunnerParams.gmrp.args
        Method method = findMethod(onObject)
        if(args.size()==0||args[0]==ConsoleSymbols.question.s){
            String msg = "method ${method.name} params : ${groovyMethodFinder.getMethodsDescription([method])}"
            throw new GroovyMethodFinderException(msg);
        }
        List args3 = groovyMethodFinder.parseArgs(args, method);
        args3.each {args.remove(0)}
        try {
            Object res = method.invoke(onObject, args3.toArray())
            log.fine "finished ok"
        } catch (InvocationTargetException e) {
            Throwable e2 = e.getCause()
            if (e2 == null) {
                throw e
            }
            throw e2
        }
    }

    Method findMethod(Object onObject){
        List<Method> methods0 = groovyMethodFinder.findAvailableMethods(onObject, true);
        List<String> args = GroovyMethodRunnerParams.gmrp.args
        boolean printHelp = args.size()==0 ||args[0] == ConsoleSymbols.question.s
        if(printHelp){
            String msg = """specify method
    available : ${groovyMethodFinder.convertMethodsNamesToString(methods0)}"""
            throw new GroovyMethodFinderException(msg);
        }
        String methodName = args[0];
        log.fine " running method : ${methodName}"
//        List<String> args2 = new ArrayList(args);
        args.remove(0);
        List<Method> methods = methods0.findAll { it.name == methodName };
        int size = methods.size()
        if (size == 0) {
            String msg = """method ${methodName} not found, 
    available : ${groovyMethodFinder.convertMethodsNamesToString(methods0)}"""
            throw new GroovyMethodFinderException(msg);
        }
        if (size > 1) {
            String msg = """found ${size} methods ${methodName}:
${groovyMethodFinder.getMethodsDescription(methods)}"""
            throw new GroovyMethodFinderException(msg);
        }
        return methods[0]

    }


}
