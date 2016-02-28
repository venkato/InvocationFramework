package net.sf.jremoterun.utilities.nonjdk.classpath.filter

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef

import java.util.logging.Logger

@CompileStatic
class ClassPathFilter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List list

    List doFilter(final List list2) {
        this.list = list2
        list = list2.collect { onElement(it) }
        normalize()
        return list;
    }

    void normalize() {
        list = list.findAll { it != null }
        list = list.unique()
    }

    Object onElement(Object obj) {
        if (obj instanceof MavenId) {
            return onMavenId(obj)
        }
        if (obj instanceof MavenIdContains) {
            return onMavenId(obj.m);
        }
        if (obj instanceof File) {
            String fullName = obj.absolutePath.toLowerCase().replace('\\','/')
            String justName = obj.name.toLowerCase()
            return onFile(obj, fullName, justName)
        }
        if (obj instanceof BinaryWithSource) {
            String fullName = obj.binary.absolutePath.toLowerCase().replace('\\','/')
            String justName = obj.binary.name.toLowerCase()
            return onBinaryWithSource(obj, fullName, justName)
        }
        if (obj instanceof GitBinaryAndSourceRef) {
            return onGitBinaryAndSourceRef(obj)
        }

        if (obj instanceof GitRef) {
            return onGitRef(obj)
        }
        return obj
    }

    Object onGitBinaryAndSourceRef(GitBinaryAndSourceRef gitRef) {
        return gitRef;
    }

    Object onGitRef(GitRef gitRef) {
        return gitRef;
    }

    Object onBinaryWithSource(BinaryWithSource b, String full, String name) {
        return b;
    }


//    Object onFileCommon(File f, String full, String name) {
//        return f;
//    }

    Object onFile(File f, String full, String name) {
        return f
    }

    Object onMavenId(MavenId m) {
        return m
    }

}
