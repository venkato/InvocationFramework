package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.BinaryWithSource2
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenPath
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.GeneralBiblioRepository
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.RefLink
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.SvnRef
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec

import java.util.logging.Logger

@CompileStatic
class ObjectWriter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String writeObject(Writer3 writer3, Object obj) {
        switch (obj) {
            case null:
                return null;
            case { obj instanceof Boolean }:
                boolean b = obj as Boolean
                return "${b}"
            case { obj instanceof Enum }:
                Enum ee = obj as Enum
                writer3.addImport(obj.class)
                return "${ee.class.simpleName}.${ee.name()}"
            case { obj instanceof RefLink }:
                RefLink ee = obj as RefLink
                writer3.addImport(RefLink)
                writer3.addImport(ee.enumm.class)
                return "new RefLink( ${ee.enumm.class.simpleName}.${ee.enumm.name()} )"
            case { obj instanceof URL }:
                writer3.addImport(URL)
                return " new URL('${obj}')"
                return " new URL('${obj}')"
            case { obj instanceof MavenId }:
                writer3.addImport(MavenId)
                return " new MavenId('${obj}')"
            case { obj instanceof GeneralBiblioRepository }:
                GeneralBiblioRepository repo = obj as GeneralBiblioRepository
                return write2ArgsConstructor(writer3,GeneralBiblioRepository, repo.name(),repo.url);
            case { obj instanceof MavenIdAndRepo }:
                MavenIdAndRepo mavenIdAndRepo = obj as MavenIdAndRepo
                return write2ArgsConstructor(writer3,MavenIdAndRepo, mavenIdAndRepo.m,mavenIdAndRepo.repo);
            case { obj instanceof MavenPath }:
                writer3.addImport(MavenPath)
                return " new MavenPath('${obj}')"
            case { obj instanceof BinaryWithSource2 }:
                writer3.addImport(BinaryWithSource2)
                BinaryWithSource2 bs = obj as BinaryWithSource2
                String binary = writeObject(writer3, bs.binary)
                String source
                if (bs.source.size() == 1) {
                    source = writeObject(writer3, bs.source[0])
                } else {
                    source = writeObject(writer3, bs.source)
                }
                return " new BinaryWithSource2(${binary}, ${source}) "
            case { obj instanceof BinaryWithSource }:
                writer3.addImport(BinaryWithSource)
                BinaryWithSource bs = obj as BinaryWithSource
                String binary = writeObject(writer3, bs.binary)
                String source = writeObject(writer3, bs.source)
                return " new BinaryWithSource(${binary}, ${source}) "
            case { obj instanceof GitBinaryAndSourceRef }:
                writer3.addImport(GitBinaryAndSourceRef)
                GitBinaryAndSourceRef bs = obj as GitBinaryAndSourceRef
                String repo = writeObject(writer3, bs.repo)
                String binary = writeObject(writer3, bs.pathInRepo)
                String source = writeObject(writer3, bs.src)
                return " new GitBinaryAndSourceRef(${repo}, ${binary}, ${source}) "
            case { obj instanceof SvnRef }:
                writer3.addImport(SvnRef)
                SvnRef svnRef = obj as SvnRef
                String repo = writeObject(writer3, svnRef.repo);
                String branchS = writeObject(writer3, svnRef.branch);
                return " new ${SvnRef.getSimpleName()} (${repo}, ${branchS}) "
            case { obj instanceof SvnSpec }:
                writer3.addImport(SvnSpec)
                SvnSpec svnSpec = obj as SvnSpec
                String repo = writeObject(writer3, svnSpec.repo);
                return " new ${SvnSpec.getSimpleName()}( ${repo} ) "
            case { obj instanceof GitRef }:
                writer3.addImport(GitRef)
                GitRef gitRef = obj as GitRef
                String repo = writeObject(writer3, gitRef.repo);
                String pathInRepo = writeObject(writer3, gitRef.pathInRepo);
                return " new GitRef(${repo}, ${pathInRepo}) "
            case { obj instanceof EnumIdea }:
                writer3.addImport(obj.class)
                EnumIdea ev = obj as EnumIdea
                String value = ev.getName()
                return "${obj.class.simpleName}.getE( '${value}' )"
            case { obj instanceof String }:
                return writeString(obj as String)
            case { obj instanceof File }:
                File f = obj as File
                return "'${f.getCanonicalFile().getAbsoluteFile().getAbsolutePath().replace('\\', '/')}' as File";
            case { obj instanceof Integer }:
                return "${obj}"
            case { obj instanceof List }:
                return writeList(writer3, (List) obj)
            case { obj instanceof Long }:
            case { obj instanceof Short }:
            case { obj instanceof Byte }:
                return "${obj} as ${obj.class.simpleName}"
            case { obj instanceof OneNestedField }:
                OneNestedField oneNestedField = obj as OneNestedField
                Object nestedField = oneNestedField.getNestedField()
                writer3.addImport(obj.class)
                String asStr = writeObject(writer3, nestedField);
                return "new  ${obj.class.simpleName} ( ${asStr} )"
            case { obj instanceof JavaBean2 }:
                JavaBean2 bean2 = obj as JavaBean2;
                return JavaBeanStore2.save(bean2, writer3, this, false);
            case { obj instanceof Class }:
                return writeClass(writer3, (Class) obj)
            case { obj instanceof Map }:
                return writeSimpleMap(writer3, (Map) obj)
            case { obj instanceof FileChildLazyRef }:
                return writeFileChildLazyRef(writer3, obj as FileChildLazyRef)
            default:
                return writeUnknownObject(writer3, obj)
        }
    }

    String writeFileChildLazyRef(Writer3 writer3,FileChildLazyRef ref){
        ToFileRef2 parentRef = ref.parentRef;
        String parentRefS= writeObject(writer3, parentRef)
        String childS = writeString(ref.child);

        if (parentRef instanceof ChildFileLazy) {
            return " ${parentRefS}.childL(${childS}) "
        }
        writer3.addImport(FileChildLazyRef)
        return " new ${FileChildLazyRef.getSimpleName()} ( ${parentRefS} , ${childS} ) ";
    }

    String write2ArgsConstructor(Writer3 writer3, Class clazz,Object obj1,Object obj2) {
        writer3.addImport(clazz)
        String obj1S = writeObject(writer3, obj1)
        String obj2S = writeObject(writer3, obj2)
        return " new ${clazz.getSimpleName()}( ${obj1S}, ${obj2S} )"
    }

    String writeString(String s){
        s = s.replace("\\","\\\\")
        s = s.replace("'","\\'")
        return "'${s}'"
    }

    String writeClass(Writer3 writer3, Class clazz) {
        String name1 = clazz.getName()
//        log.info "${name1}"
        int i = name1.indexOf('$')
//        log.info "i = ${i}"
        if (i > 0) {
            int j = name1.lastIndexOf('.',i)
            int k = name1.indexOf('$',j)
            String begin = name1.substring(0, k)
//            log.info "begin = ${begin}"
            Class<?> parentClass = clazz.getClassLoader().loadClass(begin)
            writer3.addImport parentClass

            String remaining = name1.substring(j + 1).replace('$', '.')
            return remaining;
        }
        writer3.addImport(clazz)
        return clazz.getSimpleName()
    }

    String writeSimpleMapComplex(Writer3 writer3, Map<String, Object> map) {
        return writeSimpleMap(writer3, map)
    }

    String writeSimpleMap(Writer3 writer3, Map<String, Object> map) {
//        Map<Integer,Integer> aa= [(1):2,(2):2]
        Set<Map.Entry<String, Object>> entries = map.entrySet()
        String join = entries.collect {
            String key = writeMapKey(writer3, map, it.key)
            String value = writeMapKey(writer3, map, it.value)
            return "(${key}) : ${value} ".toString()
        }.join(',\n')
        return '[' + join + ']'
    }


    String writeMapKey(Writer3 writer3, Map<String, Object> map, Object key) {
        return writeObject(writer3, key)
    }


    String writeMapValue(Writer3 writer3, Map<String, Object> map, Object value) {
        return writeObject(writer3, value)
    }

    String writeUnknownObject(Writer3 writer3, Object obj) {
        obj.class.getConstructor(String)
        writer3.addImport(obj.getClass())
        String asStr = writeObject(writer3, obj.toString());
        return " new ${obj.class.simpleName} ( ${asStr} )"
    }

    String writeList(Writer3 writer3, List list) {
        List<String> asList = list.collect { writeObject(writer3, it) }
        return ' [ ' + asList.join(',') + ' ] '
    }

}
