package net.sf.jremoterun.utilities.nonjdk.maven.mavenupload

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.spi.connector.transport.Transporter
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transfer.NoTransporterException
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterJrr;

import java.util.logging.Logger;

@CompileStatic
class MavenHttpTransporterFactoryJrr implements TransporterFactory {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public HttpTransporterFactory origin = new HttpTransporterFactory();

    @Override
    Transporter newInstance(RepositorySystemSession session, RemoteRepository repository) throws NoTransporterException {
        Map<String, Object> configProperties = session.getConfigProperties()
        log.info "config keys = ${configProperties.keySet()}"
        Transporter transporter = new HttpTransporterJrr(repository,session)
        return transporter
    }

    @Override
    float getPriority() {
        return origin.getPriority()
    }
}
