package net.sf.jremoterun.utilities.nonjdk.ivy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.mdep.ivy.JrrIvySettings
import org.apache.ivy.core.event.IvyListener

import java.util.logging.Logger


@CompileStatic
class ManyReposDownloaderImpl extends MavenManyReposDownloader {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static volatile boolean manyRepoLoaderWasSet = false
    public static MavenDependenciesResolver resolverBefore;



    public OnRepoCreatedListener repoCreatedListener
    MavenDependenciesResolver defaultRepo;
    Map<IBiblioRepository, MavenDependenciesResolver> repos = [:]

    ManyReposDownloaderImpl(MavenDependenciesResolver defaultRepo) {
        this.defaultRepo = defaultRepo
    }

    @Override
    MavenDependenciesResolver getDefaultRepo() {
        return defaultRepo
    }

    @Override
    MavenDependenciesResolver findRepo(IBiblioRepository repo) {
        if (repo == null) {
            return defaultRepo;
        }
        MavenDependenciesResolver resolver = repos.get(repo)
        if (resolver == null) {
            MavenDependenciesResolver resolver2 = createRepo(repo)
            resolver = resolver2
            if(repoCreatedListener!=null){
                repoCreatedListener.onRepoCreated(resolver2 as IvyDepResolver3)
            }
            repos.put(repo, resolver)
        }
        return resolver
    }

    void addIvyListener(IvyListener ideaIvyEvent){

        ManyReposDownloaderImpl resolver = this
        if(resolver.repoCreatedListener !=null){
            throw new Exception("repoCreatedListener is not null : ${repoCreatedListener}")
        }
        OnRepoCreatedListener repoCreatedListener = new OnRepoCreatedListener(){

            @Override
            void onRepoCreated(IvyDepResolver3 resolver2) {
                resolver2.ivy.eventManager.addIvyListener(ideaIvyEvent);
            }
        }
        resolver.repoCreatedListener = repoCreatedListener
        addListener(resolver.defaultRepo,ideaIvyEvent)
        resolver.repos.values().toList().each {addListener(it,ideaIvyEvent)}
    }

    void removeIvyListener(IvyListener ideaIvyEvent){
        ManyReposDownloaderImpl resolver = this
        resolver.repoCreatedListener = null
        resolver.repos.values().toList().each {removeListener(it,ideaIvyEvent)}
    }


    private void removeListener(MavenDependenciesResolver r, IvyListener ideaIvyEvent ){
        if (r instanceof IvyDepResolver2) {
            IvyDepResolver2  rr= (IvyDepResolver2) r;
            rr.ivy.eventManager.removeIvyListener(ideaIvyEvent)
        }
    }

    private void addListener(MavenDependenciesResolver r,IvyListener ideaIvyEvent ){
        if (r instanceof IvyDepResolver2) {
            IvyDepResolver2  rr= (IvyDepResolver2) r;
            rr.ivy.eventManager.addIvyListener(ideaIvyEvent)
        }
    }


    // force return MavenDependenciesResolver instead of IvyDepResolver3 due to bug when compiling in idea
    static MavenDependenciesResolver createRepo(IBiblioRepository rrr) {
        IvyDepResolver3 ivyDepResolver2 = new IvyDepResolver3(rrr)
        JrrIvySettings ivySettings = ivyDepResolver2.buildSettings();
        ivyDepResolver2.ivy = ivySettings.buildIvy()
        return ivyDepResolver2;
    }


    static void setManyRepoLoader() {
        if (manyRepoLoaderWasSet) {
            log.info "many repo loader already set"
        } else {
            resolverBefore = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
            MavenDependenciesResolver resolverBefore2 = resolverBefore;
            if (resolverBefore2 == null) {
                net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2.setDepResolverImpl()
            }
            resolverBefore2 = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
            assert resolverBefore2 != null
            MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver = new ManyReposDownloaderImpl(resolverBefore2)
            log.info "many resolver was set"
        }
    }

}
