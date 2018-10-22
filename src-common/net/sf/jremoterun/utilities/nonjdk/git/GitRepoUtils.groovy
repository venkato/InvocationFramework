package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.log.FileExtentionClass
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.*
import org.eclipse.jgit.errors.NoRemoteRepositoryException
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.transport.FetchResult
import org.eclipse.jgit.transport.RemoteConfig
import org.eclipse.jgit.transport.URIish

import java.text.SimpleDateFormat
import java.util.logging.Logger

/**
 Sync code with :
 @see net.sf.jremoterun.utilities.nonjdk.firstdownload.specclassloader.GitRepoUtils2
 */
@CompileStatic
class GitRepoUtils implements AutoCloseable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File gitBaseDir;
    public Git git;
    public Repository gitRepository
    public GitCommandConfigure gitCommandConfigure = CloneGitRepo4.gitCommandConfigure

    GitRepoUtils(GitSpecRef gitSpecRef) {
        this(gitSpecRef.getGitSpec().resolveToFile())
    }

    GitRepoUtils(File gitBaseDir) {
        this.gitBaseDir = gitBaseDir
        if(!gitBaseDir.exists()){
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
        FileUtils.copyFile(f, fileTo)
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

    void fetch(String remoteName) {
        FetchCommand fetch1 = git.fetch()
        if (remoteName != null) {
            fetch1.setRemote(remoteName)
        }
        doCustomGitTuning(fetch1)
        FetchResult fetchResult = fetch1.call()
        log.info("fetch done for ${gitBaseDir}")
    }

    void fetchAndCheckout(String branch) {
        fetch('origin')
        checkoutAndCommitDirty2(branch, 'origin')
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
        branchCreate.startPoint = "remotes/${remoteName}/${branch}"
        doCustomGitTuning(branchCreate)
        branchCreate.call()

        CheckoutCommand checkoutCommand = git.checkout()
        checkoutCommand.setName(branchName)
        doCustomGitTuning(checkoutCommand)
        checkoutCommand.call()
    }

    /**
     * steps :
     * 1. Copy dirty files to 'copyToDir'
     * 2. create branch originalBranchName+currentDate
     * 3. hard reset
     * 4. checkout
     * 5. Copy dirty files from 'copyToDir'
     */
    void checkoutSavedDirtyFiles(String originalBranchName, File copyToDir, String remoteName) {
        String branchName = buildBranchName(originalBranchName, new Date())
        checkoutSavedDirtyFiles(originalBranchName,copyToDir,remoteName,branchName)
    }

    void checkoutSavedDirtyFiles(String originalBranchName, File copyToDir, String remoteName,String branchName) {
        if(getRefForBranch(branchName)!=null){
            throw new Exception("Branch already exist : ${branchName}")
        }
        FileUtils.deleteQuietly(copyToDir)
        copyToDir.mkdir()
        assert copyToDir.exists()
        assert copyToDir.listFiles().length == 0
        copyDirtyFiles(copyToDir)

        CreateBranchCommand branchCreate = git.branchCreate()

        branchCreate.setName(branchName)
        branchCreate.setStartPoint("remotes/${remoteName}/${originalBranchName}")
        doCustomGitTuning(branchCreate)
        branchCreate.call()


        try {
            ResetCommand resetCommand = git.reset()
            resetCommand.mode = ResetCommand.ResetType.HARD
            doCustomGitTuning(resetCommand)
            resetCommand.call()

            CheckoutCommand checkoutCommand = git.checkout()
            checkoutCommand.setName(branchName)
            checkoutCommand.setForced(true)
            doCustomGitTuning(checkoutCommand)
            checkoutCommand.call()
        } catch (Exception e) {
            log.info "copiing files on exception ${e}"
            FileUtils.copyDirectory(copyToDir, gitBaseDir)
            throw e
        }

        FileUtils.copyDirectory(copyToDir, gitBaseDir)
    }

    /**
     * @return if ref null then branch not exist
     */
    Ref getRefForBranch(String branchName){
        Ref ref =  gitRepository.findRef(branchName)
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


    @Override
    void close() throws Exception {
        git.close()
    }
}
