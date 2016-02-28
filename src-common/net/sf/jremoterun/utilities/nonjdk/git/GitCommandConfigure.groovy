package net.sf.jremoterun.utilities.nonjdk.git;

import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.api.GitCommand;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
interface GitCommandConfigure {


    void configure(GitCommand gitCommand);
}
