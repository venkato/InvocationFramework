package net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities3
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorWithAdder
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.MavenPath
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.nonjdk.langutils.ObjectStringComparator
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.Writer3
import net.sf.jremoterun.utilities.nonjdk.store.Writer4Sub
import org.codehaus.groovy.runtime.MethodClosure

import java.util.logging.Logger

@CompileStatic
class ClassPathCalculatorSup2Groovy extends ClassPathCalculatorWithAdder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//	static MethodClosure addFileMethod = (MethodClosure)ClassPathCalculatorSup2Groovy.&addF
	static MethodClosure addMavenMethod = (MethodClosure) AddFilesToClassLoaderCommon.&addM

	static MethodClosure addGenerecMethod = (MethodClosure)AddFilesToClassLoaderCommon.&add

	public String varName = 'b'


	ObjectWriter objectWriter = new ObjectWriter()

	Map<MavenId,Object> mavenId2ObjectMap = [:]

	void addEnumMavenIdsMap(Class enumMavenIds){
		Object[] values = JrrClassUtils.invokeJavaMethod(enumMavenIds,'values') as Object[]
		List<MavenIdContains> list = values.toList() as List<MavenIdContains>
		list.collect{
			MavenId m = it.getM()
			mavenId2ObjectMap.put(m,it)
		}
	}

	Object convertMavenIdToObject(MavenId mavenId){
		Object mapTo = mavenId2ObjectMap.get(mavenId)
		if(mapTo==null){
			return mavenId
		}
		return mapTo;
	}

	String saveClassPath9() {
		assert filesAndMavenIds.size()>0
		String classpath2= saveClassPath7(filesAndMavenIds)
//		assert res.size()>3
//		String classpath2 = res.join('\r\n')
		return classpath2
	}

	void buildHeader(Writer3 writer3){
		writer3.addCreatedAtHeader()
	}

	void sortElements(){
		Collections.sort(filesAndMavenIds, new ObjectStringComparator());
	}


	void buildImport(Writer3 writer3){
		List<String> res = []
		writer3.addImport(MavenId)
		writer3.addImport(AddFilesToClassLoaderGroovy)
		writer3.addImport(ClasspathConfigurator)
		writer3.addImport(GroovyMethodRunnerParams)
		writer3.addImport(BinaryWithSource)
		writer3.addImport(MavenPath)
	}

//	List<String> buildVar(Writer3 writer3) {
//		List<String> res = []
//		res.add "${AddFilesToClassLoaderGroovy.simpleName} b = ${ writer3.generateGetProperty('a')} as ${AddFilesToClassLoaderGroovy.simpleName};"  as String
//		return res
//	}

	Writer3 createWriter(){
		return createWriter2();
	}

	Writer4Sub createWriter2(){
		return new Writer4Sub();
	}

    String saveClassPath7(List files) throws Exception {
		Writer3 writer3 = createWriter()
		assert files!=null
		buildHeader(writer3)
		buildImport(writer3)
//		writer3.body.addAll (buildVar(writer3))
		//writer3.body.add ""  as String
		writer3.body.addAll( files.collect {
			return convertEl(it,writer3)
        })
        return writer3.buildResult()
    }



	String convertEl(Object el,Writer3 writer3){
		switch (el) {
			case { el instanceof MavenId }:
				MavenId mavenId1 = (MavenId) el;
				return (String) "${varName}.${addMavenMethod.method} new ${MavenId.getSimpleName()} ( '${mavenId1}' )"
				break;
			default:
				String s = objectWriter.writeObject(writer3, el)
				return (String) "${varName}.${addGenerecMethod.method} ${s}"
		}
	}



	String saveClassPathFromURLClassLoader(URLClassLoader urlClassLoader) throws Exception {
		addClassPathFromURLClassLoader(urlClassLoader);
		calcClassPathFromFiles12()
		return saveClassPath9();
	}

	String saveClassPathFromJmx() throws Exception {
		addClassPathFromJmx()
		calcClassPathFromFiles12()
		return saveClassPath9()
	}


	String calcAndSave() throws Exception {
		calcClassPathFromFiles12()
		return saveClassPath9()
	}

	void saveClassPathFromUrlClassloaderToFile(File file) throws Exception {
		JrrUtilities3.checkFileExist(file.parentFile)
		file.text = saveClassPathFromURLClassLoader(JrrClassUtils.currentClassLoaderUrl)
		assert file.length() > 2
	}

	void saveClassPathFromJmx(File file) throws Exception {
		file = file.absoluteFile.canonicalFile
		JrrUtilities3.checkFileExist(file.parentFile)
		file.text = saveClassPathFromJmx()
		assert file.length() > 2
	}

	void excludeFilesWithNames(List<String> fileNamesToExclude){
		List filesAndMavenIds2 = new ArrayList()
		filesAndMavenIds.each {
			boolean needPass = false
			if (it instanceof File) {
				File  file1= (File) it;
				if(fileNamesToExclude.contains(file1.getName())){

				}else{
					needPass = true
				}
			}else {
				needPass = true
			}
			if(needPass){
				filesAndMavenIds2.add(it)
			}
		}
		filesAndMavenIds = filesAndMavenIds2
	}


}
