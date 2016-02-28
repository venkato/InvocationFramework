package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.BinaryWithSource2
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenPath
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.RefLink
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef

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
            case { obj instanceof MavenId }:
                writer3.addImport(MavenId)
                return " new MavenId('${obj}')"
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
                return "'${obj}'"
            case { obj instanceof File }:
                File f = obj as File
                return "'${f.canonicalFile.absoluteFile.absolutePath.replace('\\', '/')}' as File";
            case { obj instanceof Integer }:
                return "${obj}"
            case { obj instanceof List }:
                return writeList(writer3, (List) obj)
            case { obj instanceof Long }:
            case { obj instanceof Short }:
            case { obj instanceof Byte }:
                return "${obj} as ${obj.class.simpleName}"
            default:
                obj.class.getConstructor(String)
                writer3.addImport(obj.class)
                String asStr = writeObject(writer3, obj.toString());
                return " new ${obj.class.simpleName} ( ${asStr} )"
                throw new UnsupportedOperationException("${obj.class.name} ${obj}")
        }
    }

    String writeList(Writer3 writer3, List list) {
        List<String> asList = list.collect { writeObject(writer3, it) }
        return ' [ ' + asList.join(',') + ' ] '
    }

}
