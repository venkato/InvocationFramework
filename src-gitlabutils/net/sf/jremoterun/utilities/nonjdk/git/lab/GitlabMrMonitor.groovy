package net.sf.jremoterun.utilities.nonjdk.git.lab

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.timer.CronTimer
import org.gitlab4j.api.Constants
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.MergeRequestApi
import org.gitlab4j.api.models.Discussion
import org.gitlab4j.api.models.MergeRequest
import org.gitlab4j.api.models.MergeRequestFilter
import org.gitlab4j.api.models.Note
import org.gitlab4j.api.models.User

import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

@CompileStatic
abstract class GitlabMrMonitor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GitLabApi gitLabApi;
//    public Map<Integer,List<MergeRequest>> maps = [:]
//    public Map<Integer, List<Integer>> project2MrMap = new ConcurrentHashMap<>()
    // lock evry time when query : to run 1 query per time
    public final Object lock = new Object()
//    public int runBigQueryEvery = 10;
    public int bigQueryEveryCountDown = 0;
    //public boolean doRun = true;
    //public long sleepTimeInSec = 60;
    public long queryTimeToExecute = -1
    public Map<List<Integer>, MergeRequest> mapObject = new HashMap<>()
    public CronTimer cronTimer = new CronTimer({ runnn2() })
    public Date lastTimeToCheck
    public volatile User meUser;


    GitlabMrMonitor(GitLabApi gitLabApi) {
        this.gitLabApi = gitLabApi
    }

    void startEveryMinute() {
        start('1 * * ? * *')
    }

    void start(String cronExpression) {
        cronTimer.setCronExpressions(cronExpression)
        cronTimer.start()
    }

    void runnn2() {
        try {
            runnn2Impl()
        } catch (Throwable e) {
            log.info "got exception ${e}"
            onException(e)
        }
    }

    void runnn2Impl() {
//        if (bigQueryEveryCountDown == 0) {
        doBigQuery()
//            bigQueryEveryCountDown = runBigQueryEvery;
//        } else {
//            bigQueryEveryCountDown--;
////            runSmallQuery()
//        }
    }

    void doBigQuery() {
        synchronized (lock) {
            if (meUser == null) {
                meUser = gitLabApi.getUserApi().getCurrentUser()
            }
            MergeRequestApi mergeRequestApi = gitLabApi.getMergeRequestApi()
            MergeRequestFilter filter = new MergeRequestFilter();
            Date lastTimeToCheck2 = new Date()
            filter.withScope(Constants.MergeRequestScope.CREATED_BY_ME)
            boolean fullQuery = lastTimeToCheck == null
            if (fullQuery) {
                filter.withState(Constants.MergeRequestState.OPENED)
            } else {
                filter.updatedAfter = lastTimeToCheck
            }
            lastTimeToCheck = new Date()
//            filter.setSimpleView(true)
            List<MergeRequest> mergeRequests = mergeRequestApi.getMergeRequests(filter)
            List<Integer> aaa = []
            mergeRequests.each {
                gotMergeRequest(it)
                aaa.add(it.getIid())
            };
            log.info "got mr = ${aaa}"
            mergeRequests.each {
                List<Integer> key = [it.projectId, it.iid]
                mapObject.put(key, it)
            }
            lastTimeToCheck = lastTimeToCheck2
            queryTimeToExecute = System.currentTimeMillis() - lastTimeToCheck2.getTime();
        }
    }

    void gotMergeRequest(MergeRequest mr) {
        synchronized (lock) {

            List<Integer> key = [mr.projectId, mr.iid]
            MergeRequest mrBefore = mapObject.get(key)
            if (mrBefore == null) {
                onMrAdded(mr)
            } else {
                if('merged'.equalsIgnoreCase(mr.getState())){
                    onMerged(mr);
                }else {
                    onMrUpdated(mrBefore, mr)
                }
            }
        }

    }


    abstract void onException(Throwable e);

    void onMrUpdated(MergeRequest mrOld, MergeRequest mrNew) {
        if (mrOld.getUserNotesCount() == mrNew.getUserNotesCount()) {
            log.info "updated dummy ${mrNew.getUserNotesCount()}"
            onOtherUpdates(mrOld, mrNew)
        } else {
            onMrUpdatedUserNotes(mrOld, mrNew)
        }
    }

    void onMrUpdatedUserNotes(MergeRequest mrOld, MergeRequest mrNew) {
        List<Discussion> discussions = gitLabApi.getDiscussionsApi().getMergeRequestDiscussions(mrNew.getProjectId(), mrNew.iid)
        int diff = mrNew.getUserNotesCount() - mrOld.getUserNotesCount()
        if (diff > 0) {
            int size = discussions.size()
            List<Discussion> new12 = discussions.subList(size - diff, size)
            new12 = new12.findAll { isShowDiscussion(it) }
            if (new12.size() > 0) {
                onNewComments(mrNew, new12)
            } else {
                log.info "skip my comments"
            }
        } else {
            log.info "comment count diff ${diff}"
        }
    }

    boolean isShowDiscussion(Discussion discussion) {
        List<Note> notes = discussion.getNotes()
        Note note1 = notes.find { isShowNote(it) }
        return note1 != null
    }

    boolean isShowNote(Note note) {
        return note.getAuthor() != null && note.getAuthor().getId() != meUser.getId()
    }


    abstract void onOtherUpdates(MergeRequest mrOld, MergeRequest mrNew)

    abstract void onNewComments(MergeRequest mr, List<Discussion> comment)

    abstract void onMrAdded(MergeRequest mr);

    //abstract void onMrRemoved(MergeRequest mr);

    abstract void onMerged(MergeRequest mr);

}
