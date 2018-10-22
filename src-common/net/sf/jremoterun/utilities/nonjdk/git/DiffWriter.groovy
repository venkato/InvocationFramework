package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.util.logging.Logger

@CompileStatic
class DiffWriter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<DiffEntry.ChangeType> excludeChangeTypes = [];
    public List<DiffEntry> entries
    public GitRepoUtils gitRepoUtils;
    public Collection<String> includePaths;
    public Collection<String> excludePathsStartWith = [];
    public Collection<String> headers = [];
    public Collection<String> footers = [];
    public TreeSet<String> ignored = [];

    DiffWriter(List<DiffEntry> entries, GitRepoUtils gitRepoUtils) {
        this.entries = entries
        this.gitRepoUtils = gitRepoUtils
    }

    boolean isEntryMatch(DiffEntry diffEntry) {
        if (isEntryMatchImpl(diffEntry)) {
            return true
        }
        DiffEntryUtils.addBothEntryPaths(diffEntry, ignored)
        return false
    }

    boolean isEntryMatchImpl(DiffEntry diffEntry) {
        if (excludeChangeTypes.contains(diffEntry.getChangeType())) {
            return false
        }
        if (DiffEntryUtils.isAnyPathStartWith(diffEntry, excludePathsStartWith)) {
            return false
        }
        if (includePaths != null) {
            if (DiffEntryUtils.isAnyPathMatched(diffEntry, includePaths)) {
                return true
            }
            return false
        }
        return true
    }

    void convertDiffToText(File file) {
        BufferedOutputStream outputStream = file.newOutputStream()
        DiffFormatterJrr formatter = createDiffDefaultFormatter(outputStream)
        try {
            writeHeader(outputStream)
            convertDiffToText(formatter)
            writeFooter(outputStream)
            outputStream.flush()
        } finally {
            formatter.close()
            outputStream.close()
        }
    }

    void writeHeader(OutputStream out1) {
        headers.each { out1.write(it.getBytes());out1.write(DiffFormatterJrr.newLineBytes) }
    }

    void writeFooter(OutputStream out1) {
        if(ignored.size()>0){
            out1.write DiffFormatterJrr.gitDiffBytes
            out1.write "ignored ${ignored.size()}\n".getBytes()
            ignored.each {out1.write "${it}\n".getBytes()}
            out1.write(DiffFormatterJrr.newLineBytes)
        }
        footers.each { out1.write(it.getBytes());out1.write(DiffFormatterJrr.newLineBytes) }
    }

    DiffFormatterJrr createDiffDefaultFormatter(OutputStream out1) {
        DiffFormatterJrr diffFormatter = new DiffFormatterJrr(out1)
        diffFormatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL)
        diffFormatter.setProgressMonitor(gitRepoUtils.progressMonitor)
        diffFormatter.setPathFilter(TreeFilter.ALL)
        diffFormatter.setRepository(gitRepoUtils.gitRepository)
        return diffFormatter
    }

    List<DiffEntry> findMatchedEntries() {
        return entries.findAll { isEntryMatch(it) }
    }

    void convertDiffToText(DiffFormatter diffFormatter) {
        diffFormatter.format(findMatchedEntries())
        diffFormatter.flush()
    }
}
