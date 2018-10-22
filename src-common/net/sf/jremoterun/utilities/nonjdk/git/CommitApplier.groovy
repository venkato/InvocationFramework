package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.api.CommitCommand
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevSort
import org.eclipse.jgit.revwalk.RevWalk

import java.util.logging.Logger;

@CompileStatic
class CommitApplier {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<RevCommit> revCommits;
    public GitRepoUtils gitRepoUtils;
    public List<DiffApplier> diffAppliers = []
    public List<RevCommit> newRevCommits = [];
    public HashSet<String> modifiedAtStart;
    public boolean includeSkippedCommits = true;
    public static int maxSearch = 1000


    CommitApplier(List<RevCommit> revCommits, GitRepoUtils gitRepoUtils) {
        this.revCommits = revCommits
        this.gitRepoUtils = gitRepoUtils
    }


    static CommitApplier calcRebaseCommitList(RevCommit branchCommit, RevCommit masterCommit, GitRepoUtils gitRepoUtils) {
        List<RevCommit> cherryPickList = new ArrayList<>();
        RevWalk r = new RevWalk(gitRepoUtils.gitRepository)
        try {
            r.sort(RevSort.TOPO_KEEP_BRANCH_TOGETHER, true);
            r.sort(RevSort.COMMIT_TIME_DESC, true);
            r.markUninteresting(r.lookupCommit(masterCommit));
            r.markStart(r.lookupCommit(branchCommit));
            Iterator<RevCommit> commitsToUse = r.iterator();
            while (commitsToUse.hasNext()) {
                RevCommit commit = commitsToUse.next();
                if (commit.getParentCount() == 1) {
                    cherryPickList.add(commit);
                }
            }
        } finally {
            r.close()
        }
        Collections.reverse(cherryPickList);
        return new CommitApplier(cherryPickList, gitRepoUtils);
    }

    static CommitApplier aaa(GitRepoUtils gitRepoUtils, String commitId, String parentCommit) {
        List<RevCommit> lisss = []
        gitRepoUtils.findCommit(parentCommit)
        RevCommit rc = gitRepoUtils.findCommit(commitId)
        lisss.add(0, rc)
        for (i in 1..<maxSearch) {
            RevCommit parent = rc.getParent(0)
            rc = gitRepoUtils.gitRepository.parseCommit(parent)
            lisss.add(0, rc)
            if (parent.name() == parentCommit) {
                return new CommitApplier(lisss, gitRepoUtils)
            }
        }
        throw new Exception("parent commit not found : ${parentCommit}, child : ${commitId}")
    }

    /**
     *
     * @param gitRepoUtils
     * @param commitId
     * @param upCount : 0 means appli only this commit
     * @return
     */
    static CommitApplier aaa(GitRepoUtils gitRepoUtils, String commitId, int upCount) {
        List<RevCommit> lisss = []
        RevCommit rc = gitRepoUtils.findCommit(commitId)
        lisss.add(0, rc)
        for (i in 0..<upCount) {
            RevCommit parent = rc.getParent(0)
            rc = gitRepoUtils.gitRepository.parseCommit(parent)
            lisss.add(0, rc)
        }
        return new CommitApplier(lisss, gitRepoUtils)
    }

    void doStuff() {
        revCommits.each {
            reApply(it)
        }
        Collection<String> manualPaths1 = getManualPaths()
        Collection<String> mergedPath = getMergedPath()
        log.info "manualPaths = ${manualPaths1.size()} ${manualPaths1}"
        log.info "mergedPaths = ${mergedPath.size()}"
        Collection<String> needBecareful = mergedPath.findAll { manualPaths1.contains(it) }
        log.info "need be careful = ${needBecareful.size()} ${needBecareful}"
    }


    DiffApplier createDiffApplier(RevCommit parent, RevCommit childCommit) {
        CommitIdCanonicalTreeParser iteratorParent = gitRepoUtils.getTreeIterator(parent.name())
        CommitIdCanonicalTreeParser iteratorChild = gitRepoUtils.getTreeIterator(childCommit.name())
        List<DiffEntry> entries = gitRepoUtils.diffStd(iteratorParent, iteratorChild)
        DiffApplier diffApplier = createDiffApplier(entries, iteratorParent, iteratorChild)
        return diffApplier;
    }

    DiffApplier createDiffApplier(List<DiffEntry> entries, CommitIdCanonicalTreeParser iteratorParent, CommitIdCanonicalTreeParser iteratorChild) {
        DiffApplier applier = DiffApplierFactory.factory1.create(entries, iteratorParent, iteratorChild, gitRepoUtils) ;
        applier.onStatusKnown = {
            checkModified(applier.modifiedBefore)
        }
        return applier
    }

    void checkModified(HashSet<String> modifiedBefore) {
        if (modifiedAtStart == null) {
            modifiedAtStart = modifiedBefore
        } else {
            if (modifiedAtStart != modifiedBefore) {
                log.info("Internal error : modifiedAtStart = ${modifiedAtStart}, modifiedAfterStartAndBeforeThisCommid = ${modifiedBefore}")
                throw new IllegalStateException("Internal error : modifiedAtStart = ${modifiedAtStart}, modifiedAfterStartAndBeforeThisCommid = ${modifiedBefore}")
            }
        }
    }


    void reApply(RevCommit revCommit) {
        RevCommit parent = revCommit.getParent(0)
        DiffApplier diff3 = createDiffApplier(parent, revCommit);
        if (diff3.entriesOriginal.size() > 0) {
            diffAppliers.add(diff3)
            diff3.doStuff()
            if (diff3.isHasSomethingToAdd()) {
                CommitCommand commitCommand = gitRepoUtils.git.commit()
                setCommitMsg(revCommit, commitCommand, diff3)
                gitRepoUtils.doCustomGitTuning(commitCommand)
                RevCommit revCommit1 = commitCommand.call()
                newRevCommits.add(revCommit1)
            } else {
                onSkippedCommit(revCommit, diff3)
            }
        } else {
            onSkippedCommit(revCommit, diff3)
        }
    }

    void onSkippedCommit(RevCommit revCommit, DiffApplier diff3) {
        CommitCommand commitCommand = gitRepoUtils.git.commit()
        commitCommand.setAllowEmpty(true)
        gitRepoUtils.doCustomGitTuning(commitCommand)
        setCommitMsgOnEmpty(revCommit,commitCommand,diff3)
        if(includeSkippedCommits) {
            RevCommit revCommit1 = commitCommand.call()
            newRevCommits.add(revCommit1)
        }
    }

    void setCommitMsgOnEmpty(RevCommit revCommit, CommitCommand commitCommand, DiffApplier diff3) {
        String msg
        if (diff3.entriesOriginal.size() > 0) {
            Set<String> manualPaths33 = diff3.getManualPaths()
            msg = "skip as found 0 files available to change ${revCommit.name()} ${revCommit.getFullMessage()}. Skipped entries : ${manualPaths33.size()}  ${manualPaths33}"
            log.info msg
        } else {
            msg = "skip as 0 files in commit ${revCommit.name()} ${revCommit.getFullMessage()}"
        }
        log.info msg
        commitCommand.setMessage(msg)
    }

    void setCommitMsg(RevCommit revCommit, CommitCommand commitCommand, DiffApplier diff3) {
        Set<String> manualPaths33 = diff3.getManualPaths()
        List<DiffEntry> autoMerged = diff3.getByStatus(DiffApplier.DiffStatusEnum.modifiedConflictResolvedAuto);
        Collection<String> autoM = new HashSet<>()
        autoMerged.each {
            DiffEntryUtils.addBothEntryPaths(it, autoM)
        }
        autoM = autoM.sort()
        String msg = "${revCommit.getShortMessage()} . Original commit : ${revCommit.name()}. AutoMerged ${autoM.size()} ${autoM} Skipped entries ${manualPaths33.size()}  ${manualPaths33}"
        commitCommand.setMessage(msg)
    }

    List<DiffEntry> getAllDiffs() {
        List<DiffEntry> result = []
        diffAppliers.each {
            result.addAll(it.entriesOriginal)
        }
        return result
    }

    Collection<String> getAutoMergedPaths() {
        List<DiffEntry> autoMergedObj = []
        TreeSet<String> autoMergedS = new TreeSet<>()
        diffAppliers.each {
            autoMergedObj.addAll(it.getByStatus(DiffApplier.DiffStatusEnum.modifiedConflictResolvedAuto))
            autoMergedS.addAll()
            autoMergedS.addAll(it.calcRemoved())
        }
        autoMergedObj.each {
            autoMergedS.add(it.getNewPath())
        }
        return autoMergedS
    }

    Collection<String> getMergedPath() {
        TreeSet<String> skipped2 = new TreeSet<>()
        diffAppliers.each {
            skipped2.addAll(it.calcTouchedNow())
            skipped2.addAll(it.calcRemoved())
        }
        return skipped2
    }

    TreeSet<String> getManualPaths() {
        TreeSet<String> skipped2 = new TreeSet<>()
        diffAppliers.each {
            skipped2.addAll(it.getManualPaths())
        }
        return skipped2
    }

}
