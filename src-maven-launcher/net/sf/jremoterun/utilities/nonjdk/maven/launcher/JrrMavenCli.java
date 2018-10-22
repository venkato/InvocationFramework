package net.sf.jremoterun.utilities.nonjdk.maven.launcher;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.JrrGroovyScriptRunner;
import org.apache.maven.cli.CliRequest;
import org.apache.maven.cli.CliRequestPublic;
import org.apache.maven.shared.utils.logging.MessageUtils;
import org.codehaus.plexus.classworlds.ClassWorld;

import java.io.File;
import java.util.logging.Logger;

public class JrrMavenCli extends org.apache.maven.cli.MavenCli {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public JrrMavenCli() {
    }

    public JrrMavenCli(ClassWorld classWorld) {
        super(classWorld);
    }

    @Override
    public int doMain(CliRequest cliRequest) {
        return super.doMain(cliRequest);
    }


}
