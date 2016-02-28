package net.sf.jremoterun.utilities.javassist.codeinjector;

import groovy.transform.CompileStatic
import net.sf.jremoterun.RemoteRunner
import net.sf.jremoterun.SharedObjectsUtils
import net.sf.jremoterun.utilities.JrrUtilities;

import javax.management.ObjectName
import java.lang.management.ManagementFactory

@CompileStatic
public class CodeInjector {

    public static String codeModificationHooks = "codeModificationHooks";
	public static String myHookVar  = '_myHook'
	public static String sharedObjectsVar  = '_sharedObjects'
	public static String injectorVar  = '_injector'

    public static String sharedObjectsVars =
        """
            java.util.Map ${sharedObjectsVar} = 
            (java.util.Map) ${ManagementFactory.name}.getPlatformMBeanServer().getAttribute(new ${ObjectName.name}("${
                            RemoteRunner.runner
                        }"), "SharedObjects");
			if(${sharedObjectsVar} == null ){
				throw new NullPointerException("Failed received SharedObjects");
			}		
            java.util.Map ${injectorVar} =  (java.util.Map) ${sharedObjectsVar}.get("${codeModificationHooks}");
			if(${injectorVar} == null ){
				throw new NullPointerException("Failed received hook service ${codeModificationHooks}");
			}
        """

    static String createHookVar(String name) {		
        return """
            java.util.Map ${myHookVar} = ${injectorVar}.get("${name}");
			if(${myHookVar} == null ){
				throw new NullPointerException("Failed received hook : ${name}");
			}
        """;
    }

    static String createSharedObjectsHookVar(String name) {
        return """
            ${sharedObjectsVars}
            ${createHookVar(name)};
        """;
    }

	static String createSharedObjectsHookVar2(Class modifClass) {
		return createSharedObjectsHookVar(modifClass.name);
	}


	static void putInector1(String name, InjectedCode code) {
		Map globalMap = SharedObjectsUtils.getGlobalMap();
		Map buildObject = (Map) JrrUtilities.buildObject(globalMap, codeModificationHooks, JrrUtilities.constructorConcurrentHashMap);
		buildObject.put(name, code);
	}


	static void putInector2(Class modifClass, InjectedCode code) {
		putInector1(modifClass.name,code);
	}


}
