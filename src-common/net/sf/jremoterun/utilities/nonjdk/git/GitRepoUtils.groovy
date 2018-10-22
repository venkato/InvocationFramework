package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.log.FileExtentionClass
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.runtime.NullObject
import org.eclipse.jgit.api.*
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.dircache.DirCacheIterator
import org.eclipse.jgit.errors.NoRemoteRepositoryException
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.RefUpdate
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.FollowFilter
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.FetchResult
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.RemoteConfig
import org.eclipse.jgit.transport.TrackingRefUpdate
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.WorkingTreeIterator
import org.eclipse.jgit.treewalk.filter.AndTreeFilter
import org.eclipse.jgit.treewalk.filter.IndexDiffFilter
import org.eclipse.jgit.treewalk.filter.NotIgnoredFilter
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.eclipse.jgit.treewalk.filter.TreeFilter

import java.text.SimpleDateFormat
import java.util.logging.Logger

/**
 Sync code with :
 @see net.sf.jremoterun.utilities.nonjdk.firstdownload.specclassloader.GitRepoUtils2
  http://www.java2s.com/example/java-src/pkg/com/addthis/hydra/job/store/jobstoregit-17432.html
  https://github.com/centic9/jgit-cookbook
  https://www.baeldung.com/jgit
  https://github.com/dmusican/Elegit
  org.eclipse.jgit.pgm.Main
  //https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ShowFileDiff.java
  org.eclipse.jgit.transport.TransportHttp
 */
@CompileStatic
class GitRepoUtils implements AutoCloseable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File gitBaseDir;
    public Git git;
    public Repository gitRepository
    public GitCommandConfigure gitCommandConfigure = CloneGitRepo4.gitCommandConfigure
    public GitProgressMonitorJrr progressMonitor = new GitProgressMonitorJrr();
    private GitRepoUtils gitRepoUtils = this;
    public static List<RefUpdate.Result> badFetchResult = [RefUpdate.Result.IO_FAILURE, RefUpdate.Result.LOCK_FAILURE, RefUpdate.Result.REJECTED,
                                                           RefUpdate.Result.REJECTED_CURRENT_BRANCH, RefUpdate.Result.REJECTED_MISSING_OBJECT, RefUpdate.Result.REJECTED_OTHER_REASON]
    public static String defaultBranchName = 'master'

    GitRepoUtils(GitSpecRef gitSpecRef) {
        this(gitSpecRef.getGitSpec().resolveToFile())
    }

    GitRepoUtils(File gitBaseDir) {
        this.gitBaseDir = gitBaseDir
        if (!gitBaseDir.exists()) {
            throw new FileNotFoundException(gitBaseDir.getAbsolutePath());
        }
        assert gitBaseDir.isDirectory()
        git = Git.open(gitBaseDir)
        gitRepository = git.getRepository()
    }

    void doCustomGitTuning(GitCommand gitCommand) {
        if (gitCommandConfigure != null) {
            gitCommandConfigure.configure(gitCommand)
        }
    }


    CommitApplier calcRebaseCommitList(String branchCommit, String masterCommit) {
        return CommitApplier.calcRebaseCommitList(findCommit(branchCommit), findCommit(masterCommit), this)
    }



    void addAllFiles() {
        AddCommand addCommand = git.add()
        addCommand.addFilepattern('.')
        doCustomGitTuning(addCommand)
        addCommand.call()
//        GeneralUtils.runNativeProcess("${gitNative} add -A", dir, true)
        log.info("finished git add")
    }

    void gitCommit() {
        CommitCommand commitCommand = git.commit()
        commitCommand.setAll(true)
        SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd--HH-mm')
        String msg = "automsg-${sdf.format(new Date())}"
        commitCommand.setMessage(msg)
//        String commit2 = "${gitNative} commit -m '${new Date().format('yyyy-MM-dd--HH-mm')}'"
//        try {
        doCustomGitTuning(commitCommand)
        commitCommand.call()
//            GeneralUtils.runNativeProcess(commit2, dir, true)
//        } catch (Exception e) {
//            log.info "${e}"
//        }
    }

    RevCommit findCommit(String commitId) {
        ObjectId commitIdObj = gitRepository.resolve(commitId)
        if (NullObject.getNullObject().equals(commitIdObj)) {
            throw new IllegalArgumentException("Not found : ${commitId}")
        }
        return gitRepository.parseCommit(commitIdObj)

    }


    static void updateGitRepo2(GitSpecRef gitSpec1) {
        GitSpec gitSpec = gitSpec1.gitSpec;
        File f = gitSpec.specOnly.resolveToFile()

        String branch = gitSpec.branch
        if (branch == null) {
            branch = 'master'
        }
        GitRepoUtils gitRepoUtils = new GitRepoUtils(f)
        gitRepoUtils.fetchAndCheckout(branch);
    }


    void deleteBranch(String... branchNames) {
        String currentBranchShort = gitRepository.getBranch()
        String currentBranchFull = gitRepository.getFullBranch()
        List<String> listBranches = branchNames.toList()
        listBranches.each {
            if (it == currentBranchFull) {
                throw new Exception("tring delete current branch : ${it}")
            }
            if (it == currentBranchShort) {
                throw new Exception("tring delete current branch : ${it}")
            }
        }
        DeleteBranchCommand branchDelete = git.branchDelete()
        branchDelete.setBranchNames(branchNames)
        branchDelete.setForce(true)
        doCustomGitTuning(branchDelete)
        branchDelete.call()
    }


    void deleteOldBranches(String branchName, long dayBefore) {
        long oneDay = 1000 * 3600 * 24
        long dateBefore = System.currentTimeMillis() - oneDay * dayBefore
        deleteOldBranches(branchName, new Date(dateBefore))
    }

    void deleteOldBranches(String branchName, Date dateBefore) {
        String currentBranchShort = gitRepository.getBranch()
        String currentBranchFull = gitRepository.getFullBranch()
        ListBranchCommand branchList = git.branchList()
        doCustomGitTuning(branchList)
        List<Ref> refs = branchList.call()
        String prefix = "refs/heads/${branchName}-";
        refs = refs.findAll { it.getName().startsWith(prefix) }
        refs = refs.findAll { return ((it.getName() != currentBranchShort) && (it.getName() != currentBranchFull)) }
        String branchName1 = 'refs/heads/' + buildBranchName(branchName, dateBefore)
        refs = refs.findAll { it.getName().compareTo(branchName1) < 0 }
        List<String> refsHuman = refs.collect { it.getName().replace('refs/heads/', '') }.sort()
        if (refsHuman.size() == 0) {
            log.info "nothing to delete in ${gitBaseDir} for ${branchName} before date ${dateBefore}"
        } else {
            log.info "deleting : ${refsHuman}"
            refs.each {
                try {
                    deleteBranch(it.getName())
                } catch (Exception e) {
                    log.info "failed delete ${it} ${e}"
                    throw e
                }
            }
        }
    }

    void copyDirtyFiles(File copyToDir) {
        assert copyToDir.exists()
        StatusCommand statusCommand = git.status()
        doCustomGitTuning(statusCommand)
        Status status = statusCommand.call()
        List<String> filess = []
        filess.addAll status.getUntracked()
        filess.addAll status.getUncommittedChanges()
        filess = filess.unique().sort()
        List<File> files = filess.collect { gitBaseDir.child(it) }.findAll { it.isFile() }
        files = files.findAll { it.exists() }
        files.each { copyFile(gitBaseDir, copyToDir, it) }
    }

    static copyFile(File gitDir, File copyToDir, File f) {
        String pathToParent = FileExtentionClass.getPathToParent(gitDir, f)
        File fileTo = copyToDir.child(pathToParent)
        FileUtilsJrr.copyFile(f, fileTo)
    }

    String getRemote(String branchName) {
        if (branchName == null) {
            branchName = getCurrentBranch()
        }
        String remote1 = gitRepository.getConfig().getString('branch', branchName, 'remote')
        if (remote1 == null) {
            throw new Exception("failed resolve remote for branch : ${branchName}")
        }
        return remote1
    }

    void setRemote(String branchName, String remoteName) {
        if (branchName == null) {
            branchName = getCurrentBranch()
        }
        gitRepository.getConfig().setString('branch', branchName, 'remote', remoteName)
    }

    URIish getRemoteUri(String remoteName) {
        if (remoteName == null) {
            remoteName = org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
        }
        RemoteConfig remoteConfig = new RemoteConfig(gitRepository.getConfig(), remoteName)
        List<URIish> uRIs = remoteConfig.getURIs()
        int size = uRIs.size()
        switch (size) {
            case 0:
                throw new IllegalStateException("strange remote : ${remoteName}")
            case 1:
                return uRIs[0];
            default:
                throw new IllegalStateException("Found ${size} remotes for ${remoteName} : ${uRIs}")
        }

    }

    String getCurrentBranch() {
        return gitRepository.getBranch()
    }


    void fetchAllRemote() {
        Set<String> remoteNames = gitRepository.getRemoteNames()
        remoteNames.each {
            try {
                fetch(it)
            } catch (Exception e) {
                Throwable e2 = JrrUtils.getRootException(e);
                if (e2 instanceof NoRemoteRepositoryException) {
                    NoRemoteRepositoryException new_name = (NoRemoteRepositoryException) e2;
                    log.info("failed fetch ${it} : ${e}");
                } else {
                    throw e;
                }
            }
        }
    }


    FetchResult fetch2(String remoteName, String branchName) {
        assert remoteName != null
        assert branchName != null
        RefSpec refSpec = new RefSpec("refs/heads/${branchName}:refs/remotes/${remoteName}/${branchName}")
        return fetch2(remoteName,refSpec)
    }

    FetchResult fetch2(String remoteName, RefSpec refSpec) {
        FetchCommand fetch1 = git.fetch()
        fetch1.setProgressMonitor(progressMonitor)
        fetch1.setRemote(remoteName)
        List<RefSpec> specs = [refSpec]
        fetch1.setRefSpecs(specs)
        doCustomGitTuning(fetch1)
        FetchResult fetchResult = fetch1.call()
        Collection<TrackingRefUpdate> trackingRefUpdates = fetchResult.getTrackingRefUpdates()
        log.info "trackingRefUpdates size = ${trackingRefUpdates.size()}"
        trackingRefUpdates.each {
            handleFetchResult(it)
        }
        log.info("fetch done for ${gitBaseDir}")
        return fetchResult
    }

    FetchResult fetch(String remoteName) {
        FetchCommand fetch1 = git.fetch()
        if (remoteName != null) {
            fetch1.setRemote(remoteName)
        }
        fetch1.setProgressMonitor(progressMonitor)
        doCustomGitTuning(fetch1)
        FetchResult fetchResult = fetch1.call()
        Collection<TrackingRefUpdate> trackingRefUpdates = fetchResult.getTrackingRefUpdates()
        log.info "trackingRefUpdates size = ${trackingRefUpdates.size()}"
        trackingRefUpdates.each {
            handleFetchResult(it)
        }
        log.info("fetch done for ${gitBaseDir}")
        return fetchResult
    }



    void handleFetchResult(TrackingRefUpdate trackingRefUpdate) {
        String localName = trackingRefUpdate.getLocalName()
        RefUpdate.Result result = trackingRefUpdate.getResult();
        log.info "${localName} ${result}"
        if (badFetchResult.contains(result)) {
            throw new Exception("failed fetch ${localName} ${result} ${trackingRefUpdate}")
        }
    }

    void fetchAndCheckout(String branch) {
        fetch2(Constants.DEFAULT_REMOTE_NAME, branch)
        checkoutAndCommitDirty2(branch, Constants.DEFAULT_REMOTE_NAME)
    }


    String buildBranchName(String branchName, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
        return "${branchName}-${sdf.format(new Date())}"
    }

    void checkoutAndCommitDirty2(String branch, String remoteName) {
        StatusCommand statusCommand = git.status()
        doCustomGitTuning(statusCommand)
        Status statusResult = statusCommand.call()
        log.info "${gitBaseDir} is clean : ${statusResult.isClean()}"
        if (!statusResult.isClean()) {
            log.info "conflict : ${statusResult.conflicting}"
            log.info "modified : ${statusResult.modified}"
            log.info "untacked : ${statusResult.untrackedFolders}"
            log.info "missing : ${statusResult.missing}"
            addAllFiles()
            gitCommit()
        }


        CreateBranchCommand branchCreate = git.branchCreate()
        String branchName = buildBranchName(branch, new Date())
        branchCreate.setName(branchName)
        branchCreate.setStartPoint("remotes/${remoteName}/${branch}")
        doCustomGitTuning(branchCreate)
        branchCreate.call()

        CheckoutCommand checkoutCommand = git.checkout()
        checkoutCommand.setName(branchName)
        doCustomGitTuning(checkoutCommand)
        checkoutCommand.call()
    }

    void checkoutSavedDirtyFiles(String originalBranchName, File copyToDir, String remoteName) {
        String branchName = buildBranchName(originalBranchName, new Date())
        checkoutSavedDirtyFiles(originalBranchName, copyToDir, remoteName, branchName)
    }

    Ref checkoutSavedDirtyFiles(String originalBranchName, File copyToDir, String remoteName, String branchName) {
        String originalBranchPath = "remotes/${remoteName}/${originalBranchName}"
        return checkoutSavedDirtyFiles2(originalBranchPath, copyToDir, branchName)
    }


    /**
     * steps :
     * 1. Copy dirty files to 'copyToDir'
     * 2. create branch originalBranchName+currentDate
     * 3. hard reset
     * 4. checkout
     * 5. Copy dirty files from 'copyToDir'
     */
    Ref checkoutSavedDirtyFiles2(String startingPoint, File copyToDir, String newBranchName) {
        if (getRefForBranch(newBranchName) != null) {
            throw new Exception("Branch already exist : ${newBranchName}")
        }
        FileUtils.deleteQuietly(copyToDir)
        copyToDir.mkdir()
        assert copyToDir.exists()
        assert copyToDir.listFiles().length == 0
        copyDirtyFiles(copyToDir)

        CreateBranchCommand branchCreate = git.branchCreate()

        branchCreate.setName(newBranchName)
        branchCreate.setStartPoint(startingPoint)
        doCustomGitTuning(branchCreate)
        Ref newRef = branchCreate.call()
        log.info "branch created ${newRef}"

        try {
            ResetCommand resetCommand = git.reset()
            resetCommand.mode = ResetCommand.ResetType.HARD
            doCustomGitTuning(resetCommand)
            resetCommand.call()

            CheckoutCommand checkoutCommand = git.checkout()
            checkoutCommand.setName(newBranchName)
            checkoutCommand.setForced(true)
            doCustomGitTuning(checkoutCommand)
            newRef = checkoutCommand.call()
        } catch (Exception e) {
            log.info "copiing files on exception ${e}"
            FileUtilsJrr.copyDirectory(copyToDir, gitBaseDir)
            throw e
        }

        FileUtilsJrr.copyDirectory(copyToDir, gitBaseDir)
        return newRef;
    }

    /**
     *
     * @param startingPoint is name of local branch
     */
    Ref checkoutSavedDirtyFiles3(String startingPoint, File copyToDir) {
        FileUtils.deleteQuietly(copyToDir)
        copyToDir.mkdir()
        assert copyToDir.exists()
        assert copyToDir.listFiles().length == 0
        copyDirtyFiles(copyToDir)
        Ref newRef;
        try {
            ResetCommand resetCommand = git.reset()
            resetCommand.mode = ResetCommand.ResetType.HARD
            doCustomGitTuning(resetCommand)
            resetCommand.call()

            CheckoutCommand checkoutCommand = git.checkout()
            checkoutCommand.setName(startingPoint)
            checkoutCommand.setForced(true)
            doCustomGitTuning(checkoutCommand)
            newRef = checkoutCommand.call()
        } catch (Exception e) {
            log.info "copiing files on exception ${e}"
            FileUtilsJrr.copyDirectory(copyToDir, gitBaseDir)
            throw e
        }

        FileUtilsJrr.copyDirectory(copyToDir, gitBaseDir)
        return newRef;
    }

    /**
     * @return if ref null then branch not exist
     */
    Ref getRefForBranch(String branchName) {
        Ref ref = gitRepository.findRef(branchName)
        return ref
    }

    void pull() {
        StatusCommand statusCommand = git.status()
        doCustomGitTuning(statusCommand)
        Status statusResult = statusCommand.call()
        log.info "${gitBaseDir} is clean : ${statusResult.isClean()}"
        if (!statusResult.isClean()) {
            log.info "conflict : ${statusResult.conflicting}"
            log.info "modified : ${statusResult.modified}"
            log.info "untacked : ${statusResult.untrackedFolders}"
            log.info "missing : ${statusResult.missing}"

            ResetCommand resetCommand = git.reset()
            resetCommand.mode = ResetCommand.ResetType.HARD
            doCustomGitTuning(resetCommand)
            resetCommand.call()
        }
        PullCommand pullCommand = git.pull();
        doCustomGitTuning(pullCommand)
        pullCommand.call()

    }


    CommitIdCanonicalTreeParser getTreeIterator(String name) throws IOException {
        final ObjectId id = gitRepoUtils.gitRepository.resolve(name);

        if (NullObject.getNullObject().equals(id)) {
            throw new IllegalArgumentException(name);
        }
        final CommitIdCanonicalTreeParser p = new CommitIdCanonicalTreeParser(name,id);
        final ObjectReader or = gitRepoUtils.gitRepository.newObjectReader();
        try {
            p.reset(or, new RevWalk(gitRepoUtils.gitRepository).parseTree(id));
            return p;
        } finally {
            or.close();
        }
    }


    DiffApplier revertCommit(String commitId) {
        RevCommit rc = gitRepoUtils.findCommit(commitId)
        return diff3(rc.name(), rc.getParent(0).name())
    }

    DiffApplier diff3(String a1, String a2) {
        CommitIdCanonicalTreeParser iterator1 = getTreeIterator(a1)
        CommitIdCanonicalTreeParser iterator2 = getTreeIterator(a2)
        if(iterator1.id.name() == iterator2.id.name()){
            throw new IllegalArgumentException("point to same commit : ${a1} ${a2}")
        }
        List<DiffEntry> entries = diffStd(iterator1, iterator2)
        return new DiffApplier(entries, iterator1,iterator2, this);
    }

    DiffWriter createDiffWriter(String a1,String a2){
        DiffApplier diffApplier = diff3(a1, a2)
        final ObjectId ida1 = gitRepoUtils.gitRepository.resolve(a1);
        final ObjectId ida2 = gitRepoUtils.gitRepository.resolve(a2);
        if(ida1.name() == ida2.name()){
            throw new IllegalArgumentException("point to same commit : ${a1} ${a2}")
        }
        DiffWriter diffWriter = new DiffWriter(diffApplier.entriesOriginal, this)
        String aa1=a1
        String aa2=a2
        if(a1!=ida1.name()){
            aa1="${a1} ( ${ida1.name()} )"
        }
        if(a2!=ida2.name()){
            aa2="${a2} ( ${ida2.name()} )"
        }
        String datee = new SimpleDateFormat("YYYY-MM-dd").format(new Date())
        diffWriter.headers.add "${aa1} ${aa2} ${datee}".toString()
        return diffWriter;
    }



    List<DiffEntry> diffStd(AbstractTreeIterator a, AbstractTreeIterator b) {
        DiffCommand diffCommand = git.diff()
        diffCommand.setOldTree(a)
        diffCommand.setNewTree(b)
        return diffCommand.call()
    }

    List<DiffEntry> diffFast(AbstractTreeIterator a, AbstractTreeIterator b) {
        ObjectReader objectReader = gitRepoUtils.gitRepository.newObjectReader()
        try {
            List<DiffEntry> entries = diffImpl(a, b, TreeFilter.ALL, objectReader)
            return entries
        } finally {
            objectReader.close()
        }
    }

    List<DiffEntry> diffImpl(AbstractTreeIterator a, AbstractTreeIterator b, TreeFilter pathFilter, ObjectReader reader) {
        TreeWalk walk = new TreeWalk(gitRepoUtils.gitRepository, reader);
        int aIndex = walk.addTree(a);
        int bIndex = walk.addTree(b);
        if (a instanceof WorkingTreeIterator && b instanceof DirCacheIterator) {
            ((WorkingTreeIterator) a).setDirCacheIterator(walk, bIndex);
        } else if (b instanceof WorkingTreeIterator && a instanceof DirCacheIterator) {
            ((WorkingTreeIterator) b).setDirCacheIterator(walk, aIndex);
        }

        walk.setRecursive(true);

        TreeFilter filter = getDiffTreeFilterFor(a, b);
        if (pathFilter instanceof FollowFilter) {
            walk.setFilter(AndTreeFilter.create(
                    PathFilter.create(((FollowFilter) pathFilter).getPath()),
                    filter));
        } else {
            walk.setFilter(AndTreeFilter.create(pathFilter, filter));
        }

        //source = new ContentSource.Pair(source(a), source(b));

        List<DiffEntry> files = DiffEntry.scan(walk);
        return files;
    }


    TreeFilter getDiffTreeFilterFor(AbstractTreeIterator a, AbstractTreeIterator b) {
        if (a instanceof DirCacheIterator && b instanceof WorkingTreeIterator)
            return new IndexDiffFilter(0, 1);

        if (a instanceof WorkingTreeIterator && b instanceof DirCacheIterator)
            return new IndexDiffFilter(1, 0);

        TreeFilter filter = TreeFilter.ANY_DIFF;
        if (a instanceof WorkingTreeIterator)
            filter = AndTreeFilter.create(new NotIgnoredFilter(0), filter);
        if (b instanceof WorkingTreeIterator)
            filter = AndTreeFilter.create(new NotIgnoredFilter(1), filter);
        return filter;
    }


    @Override
    void close() throws Exception {
        git.close()
    }
}
