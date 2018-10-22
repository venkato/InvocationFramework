package net.sf.jremoterun.utilities.javassist

import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.UrlToFileConverter
import net.sf.jremoterun.utilities.classpath.ClRef

import javax.management.ObjectName
import java.io.File
import java.lang.instrument.ClassDefinition;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger

import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException
import javassist.runtime.Desc;
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.SimpleJvmTiAgent;
import net.sf.jremoterun.utilities.JrrClassUtils;

@CompileStatic
public class JrrJavassistUtils {

	private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

	private static Map<String, CtClass> cachedClasses;
	
	public static String constrMethod = '<init>';

	public static String LogVarName = '$log'
	public static String mapVarName = '$map12'

	// following can be used in groovy code, without escaping
	public static String LogVarName2 = 'jrrlog'
	
	public static String createLogVar =
		"""
			java.util.logging.Logger ${LogVarName} = java.util.logging.Logger.getLogger(\$class.getName());
			java.util.logging.Logger ${LogVarName2} = ${LogVarName};
		"""

	public static String createGlobalServicesMapVar =
			'''
java.util.Map $map12;
try {
	$map12 = (java.util.Map) java.lang.management.ManagementFactory.getPlatformMBeanServer().getAttribute(new javax.management.ObjectName("JRemoteRun:type=Runner"), "SharedObjects");
}catch(Exception e){
	throw new RuntimeException(e);
}


'''


	public static volatile boolean initDone = false;

	public static void init() throws Exception {
		if(initDone) {
			return
		}
		initDone = true;
		ClassPool.doPruning = false;
		Desc.useContextClassLoader = true;
		ClassPool pool = ClassPool.getDefault();
		ClassPath classPath = new ClassClassPath(JrrJavassistUtils);
		pool.appendClassPath(classPath);
	}
				
		
			
	public static CtBehavior findMethodOrConstructor(final CtClass cc, final String methodName, final int numberParams)
			throws NoSuchMethodException, NotFoundException {
		if (constrMethod.equals(methodName)) {
			return findConstructor(cc, numberParams);
		} else {
			return findMethod(cc, methodName, numberParams);
		}
	}

	public static CtMethod findMethod(CtClass classss, String methodName, Class[] params) throws NotFoundException {
		CtClass[] params2 = new CtClass[params.length];
		int i = 0;
		for (Class class2 : params) {
			params2[i] = classss.getClassPool().get(class2.getName());
			i++;
		}
		return classss.getDeclaredMethod(methodName, params2);
	}

	public static CtMethod findMethod(final CtClass cc, final String methodName, final int numberParams)
			throws NoSuchMethodException, NotFoundException {
		final CtMethod[] ctMethods = cc.getDeclaredMethods();
		for (final CtMethod ctMethod : ctMethods) {
			if (methodName.equals(ctMethod.getName())) {
				if (ctMethod.getParameterTypes().length == numberParams) {
					return ctMethod;
				}
			}
		}
		throw new NoSuchMethodException(cc.getName() + " " + methodName);
	}

			
	static CtMethod findMethodG(final CtClass cc,
		@ClosureParams(value=SimpleType.class, options="javassist.CtMethod")
		  Closure<Boolean> matcher)
			throws NoSuchMethodException, NotFoundException {
		final CtMethod[] ctMethods = cc.getDeclaredMethods();
		for (final CtMethod ctMethod : ctMethods) {
			if (matcher(ctMethod)) {				
					return ctMethod;				
			}
		}
		throw new NoSuchMethodException(cc.getName());
	}


	static CtMethod findMethod(final Class clazz ,final CtClass cc, final String methodName, final int numberParams)
			throws NoSuchMethodException, NotFoundException {
		if(!clazz.getName().equals(cc.getName())) {
			throw new IllegalArgumentException("class names mismacthes : "+clazz.getName()+" , "+cc.getName());
		}
		final CtMethod[] ctMethods = cc.getDeclaredMethods();
		for (final CtMethod ctMethod : ctMethods) {
			if (methodName.equals(ctMethod.getName())) {
				if (ctMethod.getParameterTypes().length == numberParams) {
					return ctMethod;
				}
			}
		}
		throw new NoSuchMethodException("Class: "+cc.getName()+", method name: "+ methodName+", params count: "+numberParams);
	}

	public static CtMethod findMethod(final ClRef clazz, final CtClass cc, final String methodName, final int numberParams)
			throws NoSuchMethodException, NotFoundException {
		if(!clazz.getClassName().equals(cc.getName())) {
			throw new IllegalArgumentException("class names mismacthes : "+clazz.getClassName()+" , "+cc.getName());
		}
		final CtMethod[] ctMethods = cc.getDeclaredMethods();
		for (final CtMethod ctMethod : ctMethods) {
			if (methodName.equals(ctMethod.getName())) {
				if (ctMethod.getParameterTypes().length == numberParams) {
					return ctMethod;
				}
			}
		}
		throw new NoSuchMethodException("Class: "+cc.getName()+", method name: "+ methodName+", params count: "+numberParams);
	}

	public static CtConstructor findConstructor(final CtClass cc, final int numberParams)
			throws NoSuchMethodException, NotFoundException {
		final CtConstructor[] ctMethods = cc.getDeclaredConstructors();
		for (final CtConstructor ctMethod : ctMethods) {
			if (ctMethod.getParameterTypes().length == numberParams) {
				return ctMethod;
			}
		}
		throw new NoSuchMethodException(cc.getName() + ", params count: "+numberParams);
	}

	static void appendForEachConstructorThreadDump(ClRef clazz){
		Class<?> clazz1 = clazz.loadClass2()
		appendForEachConstructorThreadDump(clazz1)
	}

	static void appendForEachConstructorThreadDump(Class clazz){
		appendForEachConstructor(clazz,'{Thread.dumpStack();}')
	}

	static void appendForEachConstructor(Class clazz,String code){
		CtClass ctClass = getClassFromDefaultPool(clazz);
		ctClass.getConstructors().toList().each {
			it.insertAfter(code);
		}
		redefineClass(ctClass,clazz);
	}
			
	static CtConstructor findConstructorG(final CtClass cc,
		@ClosureParams(value=SimpleType.class, options="javassist.CtConstructor")
		Closure<Boolean> matcher)
			throws NoSuchMethodException, NotFoundException {
		final CtConstructor[] ctMethods = cc.getDeclaredConstructors();
		for (final CtConstructor ctMethod : ctMethods) {
			if (matcher(ctMethod)) {
				return ctMethod;
			}
		}
		throw new NoSuchMethodException(cc.getName());
	}
	
	public static void updateCache() {
		if (cachedClasses == null) {
			ClassPool default1 = ClassPool.getDefault();
			try {
				cachedClasses = (Map) JrrClassUtils.getFieldValue(default1, "classes");
			} catch (Exception e) {
				throw new Error(e);
			}
		}
	}
	
	

	public static CtClass getClassFromDefaultPool(Class className) throws NotFoundException {
		return getClassFromDefaultPool(className.getName());
	}

	public static CtClass getClassFromDefaultPool(String className) throws NotFoundException {
		final ClassPool cp = ClassPool.getDefault();
		updateCache();		
		cachedClasses.remove(className);
		final CtClass cc = cp.get(className);
		cc.defrost();
		return cc;
	}

	/**
	 * Doesn't catch : pck.Class1$22$44
	 */
	@Deprecated
	public static List<Class> getRelatedClasses1(final Class class1) throws Exception {
		final ArrayList<Class> classes = new ArrayList();
		classes.add(class1);
		final ClassLoader classLoader = class1.getClassLoader();
		int i = 1;
		while (true) {
			try {
				final Class class2 = classLoader.loadClass("${class1.getName()}\$${i}");
				classes.add(class2);
				i++;
			} catch (final ClassNotFoundException e) {
				log.info(i+"");
				break;
			}
		}
		return classes;
	}


	public static List<Class> getRelatedClasses2(final Class class1) throws Exception {
		final ClassLoader classLoader = class1.getClassLoader();
		URL url77=JrrUtils.getClassFileLocation(class1);
		if(url77==null){
			throw new Exception("failed detect url for class : "+class1.getName())
		}
		File file = UrlToFileConverter.c.convert(url77);
		JrrUtilities.checkFileExist(file)
		String prefix = getBaseName(file);
		List<File> childs = []
		file.parentFile.eachFile {
			if(getBaseName(it).startsWith(prefix)) {
				childs.add(it);
			}
		}		
		List<Class> classes = childs.
			collect {class1.getPackage().name + '.'+getBaseName(it)}.
			collect{classLoader.loadClass(it)}.
			asList();
		return classes; 
	}

	static String getBaseName(File f ){
		String name = f.getName()
		int dot = name.lastIndexOf('.')
		if(dot==-1){
			log.info "dot not found : ${f}"
			return name
		}
		String baseName = name.substring(0,dot)
		return baseName;
	}

	
	public static void reloadClassAndAnonClasses(final Class class1) throws Exception {
		List<Class> classes = getRelatedClasses2(class1);
		SimpleJvmTiAgent.redefineClasses(classes.toArray(new Class[0]));
		log.info "reloaded ${classes.size()} classes from ${class1.name}"
	}

	
	public static void redefineClass(final CtClass ctClass, Class class1)
			throws Exception {
		assert ctClass.name == class1.name
		final ClassDefinition classDefinition = new ClassDefinition(class1, ctClass.toBytecode());
		if (ctClass.isFrozen()) {
			log.info("defrost " + ctClass.getName());
			ctClass.defrost();
		}
		final ClassDefinition[] classDefinitions = [ classDefinition ];
		if (SimpleJvmTiAgent.instrumentation == null) {
			throw new NullPointerException("SimpleJvmTiAgent.instrumentation is null");
		}
		SimpleJvmTiAgent.instrumentation.redefineClasses(classDefinitions);
	}

}
