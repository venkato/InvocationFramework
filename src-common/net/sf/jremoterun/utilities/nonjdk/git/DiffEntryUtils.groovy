package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.diff.DiffEntry;

import java.util.logging.Logger;

@CompileStatic
class DiffEntryUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();




    static void addBothEntryPaths(DiffEntry entry, Collection<String> paths) {
        String oldPath = entry.getOldPath()
        if(oldPath !=null){
            paths.add(oldPath)
        }
        String newPath = entry.getNewPath()
        if(newPath !=null){
            paths.add(newPath)
        }
    }

    static boolean isAnyPathMatched(DiffEntry entry, String path) {
        return entry.getOldPath() == path || entry.getNewPath() == path
    }

    static boolean isAnyPathStartWith(DiffEntry entry, Collection<String> paths) {
        String find1 = paths.find { isAnyPathStartWith(entry, it) }
        return find1!=null
    }

    static boolean isAnyPathStartWith(DiffEntry entry, String path) {
        String oldPath = entry.getOldPath()
        if(oldPath !=null){
            if(oldPath.startsWith(path)){
                return true
            }
        }
        String newPath = entry.getNewPath()
        if(newPath !=null){
            if(newPath.startsWith(path)){
                return true
            }
        }
        return false
    }

    static boolean isAnyPathMatched(DiffEntry entry, Collection<String> paths) {
        String matcheddd = paths.find { isAnyPathMatched(entry, it) }
        return matcheddd != null
    }
}
