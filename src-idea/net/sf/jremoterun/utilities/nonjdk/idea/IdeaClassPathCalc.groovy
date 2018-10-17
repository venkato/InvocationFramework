package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.filter.ClassPathFilter
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JeditermBinRefs
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef

import java.util.logging.Logger

@CompileStatic
class IdeaClassPathCalc extends ClassPathFilter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    Object onBinaryWithSource(BinaryWithSource b, String full, String name) {
        switch (name) {
            case { name.startsWith('jediterm-pty-') }:
                return null
        }
        return super.onBinaryWithSource(b, full, name)
    }

    @Override
    Object onGitBinaryAndSourceRef(GitBinaryAndSourceRef gitRef) {
//        if(gitRef == JeditermBinRefs.jtermPty.ref){
//            return null
//        }
        return super.onGitBinaryAndSourceRef(gitRef)
    }

    @Override
    Object onMavenId(MavenId mavenId) {
        String group = mavenId.groupId;
        String artifact = mavenId.artifactId
        switch (mavenId) {
            case { artifact == 'slf4j-api' }:
            case { group == 'log4j' }:
            case { group == 'net.java.dev.jna' }:
            case { group == 'commons-logging' }:
                return null
            default:
                break
        }
        return super.onMavenId(mavenId)
    }

    @Override
    Object onFile(File f, String full, String name) {
        if (full.endsWith('/lib/tools.jar')) {
            return null
        }
        return f

    }
}
