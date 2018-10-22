package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.api.AddCommand
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.StatusCommand
import org.eclipse.jgit.diff.DiffAlgorithm
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.dircache.DirCache
import org.eclipse.jgit.dircache.DirCacheEntry
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectDatabase
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectLoader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.merge.MergeAlgorithm
import org.eclipse.jgit.merge.MergeFormatter
import org.eclipse.jgit.merge.MergeResult
import org.eclipse.jgit.util.io.AutoCRLFOutputStream

import java.nio.charset.StandardCharsets
import java.util.logging.Logger;

@CompileStatic
class DiffApplier {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<DiffEntry> entriesOriginal;
    public CommitIdCanonicalTreeParser iterator1;
    public CommitIdCanonicalTreeParser iterator2;
    public GitRepoUtils gitRepoUtils;

//    public List<DiffEntry> modified;
//    public List<DiffEntry> added;
//    public List<DiffEntry> copied;
//    public List<DiffEntry> deleted;
//    public List<DiffEntry> renamed;

//    public List<DiffEntry> consider = []
//    public List<DiffEntry> conflicted = []
//    public List<DiffEntry> skipped = [];
    public IdentityHashMap<DiffEntry, DiffStatusEnum> diffStatus = new IdentityHashMap<>()

    public DirCache dirCache;

    public boolean resolveConflicstsAuto = true
    public Status status1
    public HashSet<String> modifiedBefore = new HashSet();
    public long maxFileSizeToMergeInKb = 1000;
//    public HashSet<String> addedNow = new HashSet();
//    public HashSet<String> touchedNow = new HashSet();
    public MergeAlgorithm mergerAlgorithm = new MergeAlgorithm();
    public Map<DiffEntry, MergeResult<RawText>> conflictMergedResult = new IdentityHashMap<>()
    public RawTextComparator mergeRawTextComparator = RawTextComparator.WS_IGNORE_ALL
    public Runnable onStatusKnown

    DiffApplier(List<DiffEntry> entries, GitRepoUtils gitRepoUtils) {
        this.entriesOriginal = entries
        this.gitRepoUtils = gitRepoUtils
    }


    DiffApplier(List<DiffEntry> entriesOriginal, CommitIdCanonicalTreeParser iterator1, CommitIdCanonicalTreeParser iterator2, GitRepoUtils gitRepoUtils) {
        this.entriesOriginal = entriesOriginal
        this.iterator1 = iterator1
        this.iterator2 = iterator2
        this.gitRepoUtils = gitRepoUtils
    }

    void setMergerAlgorithm(DiffAlgorithm.SupportedAlgorithm algorithm) {
        mergerAlgorithm = new MergeAlgorithm(DiffAlgorithm.getAlgorithm(algorithm));
    }

    public static enum DiffStatusEnum {
        modifiedNoConflict,
        modifiedConflictUnknown,
        modifiedConflictIssue,
        modifiedConflictFileTooBig,
        modifiedConflictResolvedAuto,
        added,
        copied,
        deleted,
        renamed,
        ignored,
    }

    List<DiffEntry> getByStatus(DiffStatusEnum statusEnum) {
        return diffStatus.findAll { it.value == statusEnum }.collect { it.key }
    }


    void prepare() {
        callStatus()
        handleStatus()
        readCache()
        entriesOriginal.each {
            DiffStatusEnum take = isOkToTake(it)
            diffStatus.put(it, take)
        }
    }

    Set<String> getManualPaths() {
        TreeSet<String> skippedS = new TreeSet<>()
        getByStatus(DiffStatusEnum.ignored).each {
            DiffEntryUtils.addBothEntryPaths(it, skippedS)
        }
        getByStatus(DiffStatusEnum.modifiedConflictIssue).each {
            DiffEntryUtils.addBothEntryPaths(it, skippedS)
        }
        getByStatus(DiffStatusEnum.modifiedConflictFileTooBig).each {
            DiffEntryUtils.addBothEntryPaths(it, skippedS)
        }
        getByStatus(DiffStatusEnum.modifiedConflictUnknown).each {
            DiffEntryUtils.addBothEntryPaths(it, skippedS)
        }
        return skippedS
    }

    void doStuff() {
        prepare()
        applyNewValues()
        if (isHasSomethingToAdd()) {
            addToIndex()
        }
    }

    boolean isHasSomethingToAdd() {
        return calcTouchedNow().size() > 0 || calcRemoved().size() > 0
    }

    HashSet<String> calcRemoved() {
        HashSet<String> res = new HashSet<>()
        getByStatus(DiffStatusEnum.deleted).each { res.add(it.getOldPath()) }
        getByStatus(DiffStatusEnum.renamed).each { res.add(it.getOldPath()) }
        return res
    }

    HashSet<String> calcTouchedNow() {
        HashSet<String> res = new HashSet<>()
        getByStatus(DiffStatusEnum.modifiedNoConflict).each { res.add(it.getNewPath()) }
        getByStatus(DiffStatusEnum.modifiedConflictResolvedAuto).each { res.add(it.getNewPath()) }
        getByStatus(DiffStatusEnum.added).each { res.add(it.getNewPath()) }
        getByStatus(DiffStatusEnum.copied).each { res.add(it.getNewPath()) }
        getByStatus(DiffStatusEnum.renamed).each { res.add(it.getNewPath()) }
        return res
    }

    HashSet<String> calcAddedNow() {
        HashSet<String> res = new HashSet<>()
        getByStatus(DiffStatusEnum.added).each { res.add(it.getNewPath()) }
        getByStatus(DiffStatusEnum.copied).each { res.add(it.getNewPath()) }
        getByStatus(DiffStatusEnum.renamed).each { res.add(it.getNewPath()) }
        return res
    }


    void applyNewValues() {
        getByStatus(DiffStatusEnum.modifiedNoConflict).each { applyNewValueEntry(it) }
        getByStatus(DiffStatusEnum.added).each {
            applyNewValueEntry(it)
//            addedNow.add(it.getNewPath())
        }
        getByStatus(DiffStatusEnum.copied).each {
            applyNewValueEntry(it)
//            addedNow.add(it.getNewPath())
        }
        getByStatus(DiffStatusEnum.deleted).each { deletePath(it.getOldPath()) }
        getByStatus(DiffStatusEnum.renamed).each {
            applyNewValueEntry(it)
            deletePath(it.getOldPath())
//            addedNow.add(it.getNewPath())
        }
        if (resolveConflicstsAuto) {
            getByStatus(DiffStatusEnum.modifiedConflictUnknown).each {
                File f = new File(gitRepoUtils.gitBaseDir, it.getNewPath());
                if (f.size() > maxFileSizeToMergeInKb * 1000) {
                    diffStatus.put(it, DiffStatusEnum.modifiedConflictFileTooBig)
                } else {
                    MergeResult<RawText> res = resolveConflictAndApply(it)
                    conflictMergedResult.put(it, res)
                    if (res.containsConflicts()) {
                        diffStatus.put(it, DiffStatusEnum.modifiedConflictIssue)
                    } else {
                        diffStatus.put(it, DiffStatusEnum.modifiedConflictResolvedAuto)
                    }
                }
            }

        }
    }


    void callStatus() {
        StatusCommand status = gitRepoUtils.git.status()
        HashSet<String> badPaths = new HashSet<>()
        entriesOriginal.each {
            DiffEntryUtils.addBothEntryPaths(it, badPaths)
        }
        badPaths.each { status.addPath(it) }
        status1 = status.call()
    }


    void handleStatus() {
        modifiedBefore.addAll(status1.getModified())
        modifiedBefore.addAll(status1.getUncommittedChanges())
        modifiedBefore.addAll(status1.getRemoved())
        modifiedBefore.addAll(status1.getMissing())
        if (onStatusKnown != null) {
            onStatusKnown.run()
        }
    }

    DiffStatusEnum resolveStatus(DiffEntry entry) {
        switch (entry.getChangeType()) {
            case DiffEntry.ChangeType.MODIFY:
                return DiffStatusEnum.modifiedNoConflict
            case DiffEntry.ChangeType.ADD:
                return DiffStatusEnum.added
            case DiffEntry.ChangeType.COPY:
                return DiffStatusEnum.copied
            case DiffEntry.ChangeType.DELETE:
                return DiffStatusEnum.deleted
            case DiffEntry.ChangeType.RENAME:
                return DiffStatusEnum.renamed
            default:
                throw new UnsupportedOperationException("${entry.getChangeType()} ${entry}")
        }
    }


    boolean isAcceptOverride(String path) {
        return false
    }

    boolean isIgnoreOverride(String path) {
        return false
    }

// TODO now white space diff will be applied, even if asked to ignore
    MergeResult<RawText> resolveConflictAndApply(DiffEntry entry) throws IOException {
        assert entry.getChangeType() == DiffEntry.ChangeType.MODIFY
        File f = new File(gitRepoUtils.gitBaseDir, entry.getNewPath());
        //DirCacheEntry entry1 = dirCache.getEntry(entry.getOldPath())
        ObjectDatabase db = gitRepoUtils.gitRepository.getObjectDatabase();
        try {
            RawText baseText = new RawText(db.open(entry.getOldId().toObjectId(), Constants.OBJ_BLOB).getCachedBytes());
            RawText ourText = new RawText(f.bytes);
            RawText theirsText = new RawText(db.open(entry.getNewId().toObjectId(), Constants.OBJ_BLOB).getCachedBytes());
            MergeResult<RawText> mergeResult = mergerAlgorithm.merge(mergeRawTextComparator, baseText, ourText, theirsText);
            if (!mergeResult.containsConflicts()) {
                resolveConflictAndApply5(entry, mergeResult)
            }
            return mergeResult
        } finally {
            db.close()
        }
    }


    //TODO verify opened and closed parenthers before writing to file
    void resolveConflictAndApply5(DiffEntry entry, MergeResult<RawText> merge) throws IOException {
        MergeFormatter format = new MergeFormatter();
        File f = new File(gitRepoUtils.gitBaseDir, entry.getNewPath());
        BufferedOutputStream outputStream = f.newOutputStream()
        OutputStream ooo = outputStream
        AutoCRLFOutputStream aa = new AutoCRLFOutputStream(outputStream)
        ooo = aa
        format.formatMerge(ooo, merge, ["BASE", "OURS", "THEIRS"], StandardCharsets.UTF_8);
        ooo.flush()
        ooo.close()
    }

    DiffStatusEnum isOkToTake(DiffEntry entry) {
        String oldPath = entry.getOldPath()
        String newPath = entry.getNewPath()
        if (oldPath != null) {
            if (isAcceptOverride(oldPath)) {
                // TODO dangerous
                return resolveStatus(entry)
            }
            if (isIgnoreOverride(oldPath)) {
                return DiffStatusEnum.ignored
            }
        }
        if (newPath != null) {
            if (isAcceptOverride(newPath)) {
                // TODO dangerous
                return resolveStatus(entry)
            }
            if (isIgnoreOverride(newPath)) {
                return DiffStatusEnum.ignored
            }
        }
        if (isPathMatched(entry, modifiedBefore)) {
            return DiffStatusEnum.ignored
        }
        switch (entry.getChangeType()) {
            case DiffEntry.ChangeType.MODIFY:
                DirCacheEntry entry1 = dirCache.getEntry(oldPath)
                if (entry1 == null) {
                    return DiffStatusEnum.ignored
                }
                if (entry1.getObjectId().equals(entry.getOldId().toObjectId())) {
                    return DiffStatusEnum.modifiedNoConflict
                }
                return DiffStatusEnum.modifiedConflictUnknown
            case DiffEntry.ChangeType.DELETE:
                DirCacheEntry entry1 = dirCache.getEntry(oldPath)
                if (entry1 == null) {
                    return DiffStatusEnum.ignored
                }
                if (entry1.getObjectId().equals(entry.getOldId().toObjectId())) {
                    return DiffStatusEnum.deleted
                }
                return DiffStatusEnum.ignored
            case DiffEntry.ChangeType.ADD:
                DirCacheEntry entry1 = dirCache.getEntry(newPath)
                if (entry1 == null) {
                    return DiffStatusEnum.added
                }
                return DiffStatusEnum.ignored
            case DiffEntry.ChangeType.RENAME:
                DirCacheEntry entry1 = dirCache.getEntry(oldPath)
                if (entry1 == null) {
                    return DiffStatusEnum.ignored
                }
                if (dirCache.getEntry(newPath) != null) {
                    return DiffStatusEnum.ignored
                }
                if (entry1.getObjectId().equals(entry.getOldId().toObjectId())) {
                    return DiffStatusEnum.renamed
                }
                return DiffStatusEnum.ignored
            case DiffEntry.ChangeType.COPY:
                DirCacheEntry entry1 = dirCache.getEntry(newPath)
                if (entry1 == null) {
                    return DiffStatusEnum.copied
                }
                return DiffStatusEnum.ignored
            default:
                throw new IllegalArgumentException("${entry}")
        }
    }


    void readCache() {
        Repository repo = gitRepoUtils.gitRepository
        dirCache = repo.readDirCache()
    }

    void applyNewValueEntry(DiffEntry entry) {
        File f = new File(gitRepoUtils.gitBaseDir, entry.getNewPath());
        File parentDir = f.getParentFile()
        parentDir.mkdirs()
        assert parentDir.exists()
        BufferedOutputStream outputStream = f.newOutputStream()
        ObjectLoader objectLoader = gitRepoUtils.gitRepository.open(entry.getNewId().toObjectId())
        objectLoader.copyTo(outputStream);
        outputStream.flush()
        outputStream.close();
//        touchedNow.add(entry.getNewPath())
    }

    void deletePath(String path) {
        File f = new File(gitRepoUtils.gitBaseDir, path);
        assert f.exists()
        f.delete()
        assert !f.exists()
    }


//    List<DiffEntry> findNoMatchedEntries(List<DiffEntry> entry, Collection<String> paths) {
//        return entry.findAll { !isPathMatched(it, paths) }
//    }

    boolean isPathMatched(DiffEntry entry, Collection<String> paths) {
        return DiffEntryUtils.isAnyPathMatched(entry, paths)
    }


    void addToIndex() {
        if (calcTouchedNow().size() > 0) {
            addToIndex3(calcTouchedNow(), true)
        }
        addToIndex2(calcRemoved())
        addToIndex2(calcAddedNow())
    }


    void addToIndex2(HashSet<String> paths) {
        if (paths.size() > 0) {
            addToIndex3(paths, true)
            addToIndex3(paths, false)
        }
    }

    void addToIndex3(HashSet<String> paths, boolean setUpdated1) {
        AddCommand addCommand = gitRepoUtils.git.add()
        addCommand.setUpdate(setUpdated1)
        gitRepoUtils.doCustomGitTuning(addCommand)
        paths.each { addCommand.addFilepattern(it) }
        dirCache = addCommand.call()
    }


}
