package net.sf.jremoterun.utilities.nonjdk.maven.mavenupload

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.MavenMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.MavenResolverMavenIds
import org.apache.commons.io.FilenameUtils
import org.apache.maven.repository.internal.MavenSettingsJrr
import org.eclipse.aether.metadata.Metadata
import org.eclipse.aether.repository.Authentication
import org.eclipse.aether.util.repository.AuthenticationBuilder

import java.util.logging.Logger;

import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader
import org.apache.maven.repository.internal.DefaultVersionRangeResolver
import org.apache.maven.repository.internal.DefaultVersionResolver
import org.apache.maven.repository.internal.SnapshotMetadataGeneratorFactoryJrr
import org.apache.maven.repository.internal.VersionsMetadataGeneratorFactory
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.deployment.DeployRequest
import org.eclipse.aether.impl.ArtifactDescriptorReader
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.impl.MetadataGeneratorFactory
import org.eclipse.aether.impl.VersionRangeResolver
import org.eclipse.aether.impl.VersionResolver
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.LocalRepositoryManager
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory

// https://github.com/sonatype/nexus-book-examples/blob/master/ant-aether/simple-project-staging/build.xml
@CompileStatic
class MavenUploader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static final String SNAPSHOT = Metadata.Nature.SNAPSHOT.name();


    public DefaultServiceLocator dsl = new DefaultServiceLocator();
    public DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
    public RemoteRepository.Builder repoBuilder;
    public DeployRequest request = new DeployRequest();
    public List<Artifact> artifacts = []
    public Boolean snapshotBuild = false
    public LocalRepository localRepository;
    public SimpleLocalRepositoryManagerFactory simpleLocalRepositoryManagerFactory = new SimpleLocalRepositoryManagerFactory()
    public MavenSettingsJrr mavenSettingsJrr = new MavenSettingsJrr();

    MavenUploader(String url, File localRepo) {
        repoBuilder = new RemoteRepository.Builder('jrrrepo', "default", url);
        localRepository = new LocalRepository(localRepo);
    }

    void init() {
        dsl.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                log.severe("failed create ${type.getName()} with impl = ${impl.getName()} : ${exception}")
//                log.log(Level.SEVERE, 'maven uploader failed', exception)
                throw exception
            }
        })
        addService(ArtifactDescriptorReader, DefaultArtifactDescriptorReader);
        addService(VersionRangeResolver, DefaultVersionRangeResolver);
        addService(VersionResolver, DefaultVersionResolver);
        addService(RepositoryConnectorFactory, BasicRepositoryConnectorFactory);
        addService(TransporterFactory, MavenHttpTransporterFactoryJrr);


        addService(MetadataGeneratorFactory, VersionsMetadataGeneratorFactory);
        addService(MetadataGeneratorFactory, SnapshotMetadataGeneratorFactoryJrr);
    }

    File createPomFileAndAdd(MavenId mavenId) {
        String childPath = mavenId.groupId.replace('.', '/') + '/' + mavenId.artifactId + '/' + mavenId.version + '/'+mavenId.artifactId+'-'+mavenId.version+'.pom';
        File basedir = localRepository.getBasedir()
        assert basedir.exists()
        File pomPath = new File(basedir, childPath)
        File parentFile = pomPath.getParentFile()
        parentFile.mkdirs()
        assert parentFile.exists()
        pomPath.text = createPomStr(mavenId);
        addArtifact(pomPath, mavenId)
        return pomPath;
    }

    String createPomStr(MavenId mavenId) {
        String pom12 = """<?xml version="1.0"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>${mavenId.groupId}</groupId>
    <artifactId>${mavenId.artifactId}</artifactId>
    <version>${mavenId.version}</version>

</project>
"""
        return pom12
    }

    void addArtifact(File file, MavenId mavenId) {
        String extension = FilenameUtils.getExtension(file.getName())
        Artifact artifact1 = new DefaultArtifact(mavenId.groupId, mavenId.artifactId, '', extension,mavenId.version,null,file);
        artifacts.add(artifact1)
    }

    void setAuth(String username,String password){
        Authentication authentication = new AuthenticationBuilder().addUsername(username).addPassword(password).build()
        repoBuilder.setAuthentication(authentication)
    }

    void deploy() {
        session.setConfigProperty(MavenSettingsJrr.mavenSettingsJrrS,mavenSettingsJrr)
        LocalRepositoryManager localRepositoryManager = simpleLocalRepositoryManagerFactory.newInstance(session, localRepository);
        session.setLocalRepositoryManager(localRepositoryManager)
        request.setArtifacts(artifacts);

        RemoteRepository remoteRepository = repoBuilder.build()
        request.setRepository(remoteRepository);
//        dsl.getService(Deployer).deploy(session, request);
        dsl.getService(RepositorySystem).deploy(session, request);
    }


    void addService(Class class1, Class class2) {
        dsl.addService(class1, class2)
    }
}
