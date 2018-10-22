package org.apache.maven.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.maven.execution.MavenExecutionRequest;
import org.codehaus.plexus.classworlds.ClassWorld;

import java.io.File;
import java.util.Properties;

public class CliRequestPublic extends org.apache.maven.cli.CliRequest{
    public CliRequestPublic(String[] args, ClassWorld classWorld) {
        super(args, classWorld);
    }


    public void setArgs(String[] args) {
        this.args = args;
    }

    public void setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public void setClassWorld(ClassWorld classWorld) {
        this.classWorld = classWorld;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public void setMultiModuleProjectDirectory(File multiModuleProjectDirectory) {
        this.multiModuleProjectDirectory = multiModuleProjectDirectory;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public void setShowErrors(boolean showErrors) {
        this.showErrors = showErrors;
    }

    @Override
    public void setUserProperties(Properties userProperties) {
        this.userProperties = userProperties;
    }

    public void setSystemProperties(Properties systemProperties) {
        this.systemProperties = systemProperties;
    }

    public void setRequest(MavenExecutionRequest request) {
        this.request = request;
    }
}
