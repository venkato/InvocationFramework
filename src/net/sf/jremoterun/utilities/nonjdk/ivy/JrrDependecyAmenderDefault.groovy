package net.sf.jremoterun.utilities.nonjdk.ivy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.mdep.ivy.DependencyResolverDebugger
import net.sf.jremoterun.utilities.mdep.ivy.JrrDependecyAmender
import org.apache.ivy.core.module.descriptor.Artifact
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor
import org.apache.ivy.core.module.descriptor.DependencyDescriptor
import org.apache.ivy.core.module.descriptor.MDArtifact
import org.apache.ivy.core.module.id.ModuleId
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.resolve.ResolveData
import org.apache.ivy.core.resolve.ResolvedModuleRevision;

import java.util.logging.Logger;

@CompileStatic
class JrrDependecyAmenderDefault extends JrrDependecyAmender {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static MavenId mavenId1 = new MavenId('javax.ws.rs:javax.ws.rs-api:2.1.1')

    public static void setResolverAmender() {
        DependencyResolverDebugger.dependecyAmender = new JrrDependecyAmenderDefault();
    }

    @Override
    ResolvedModuleRevision amendIfNeeded(ResolvedModuleRevision resolvedModuleRevision, DependencyDescriptor dd, ResolveData data) {
        if(resolvedModuleRevision==null){
            return null
        }
        ModuleRevisionId moduleRevisionId1 = resolvedModuleRevision.getDescriptor().getModuleRevisionId()
        ModuleId moduleId1 = moduleRevisionId1.getModuleId()
        if (moduleId1.organisation == mavenId1.groupId && moduleId1.name == mavenId1.artifactId) {
            handleJavaWs(resolvedModuleRevision);
        }
        return resolvedModuleRevision;
    }


    static void handleJavaWs(ResolvedModuleRevision resolvedModuleRevision) {
        ModuleRevisionId moduleRevisionId1 = resolvedModuleRevision.getDescriptor().getModuleRevisionId()
        ModuleId moduleId1 = moduleRevisionId1.getModuleId()
        log.info "inside special handle for module id = ${moduleId1}"
        DefaultModuleDescriptor defaultModuleDescriptor1 = resolvedModuleRevision.getDescriptor() as DefaultModuleDescriptor;
        Collection<Artifact> artifacts = (Collection) JrrClassUtils.getFieldValue(defaultModuleDescriptor1, 'artifacts')
        artifacts.each {
            org.apache.ivy.core.module.descriptor.MDArtifact mdArtifact = it as MDArtifact;
//                mdArtifact.getId()
            if (mdArtifact.getExt() == '${packaging.type}') {
                log.info "set jar for ${resolvedModuleRevision.getDescriptor()} by ${resolvedModuleRevision} from ext = ${mdArtifact.getExt()}"
                JrrClassUtils.setFieldValue(mdArtifact, 'ext', 'jar');
            }
        }
    }
}
