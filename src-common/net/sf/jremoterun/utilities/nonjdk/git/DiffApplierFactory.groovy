package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.diff.DiffEntry;

import java.util.logging.Logger;

@CompileStatic
class DiffApplierFactory {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static DiffApplierFactory factory1 = new DiffApplierFactory()

    DiffApplier create(List<DiffEntry> entries, CommitIdCanonicalTreeParser iteratorParent, CommitIdCanonicalTreeParser iteratorChild,GitRepoUtils gitRepoUtils){
        DiffApplier applier = new DiffApplier(entries, iteratorParent, iteratorChild, gitRepoUtils) ;
        return applier
    }

}
