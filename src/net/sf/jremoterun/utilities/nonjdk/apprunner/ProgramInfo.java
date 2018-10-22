package net.sf.jremoterun.utilities.nonjdk.apprunner;

import groovy.transform.Canonical;

import java.io.File;

import groovy.transform.CompileStatic;


@Canonical
@CompileStatic
public interface ProgramInfo {


    File getRunFile();

//    ProgramInfo(String containsCmd, File runFile) {
//        this.containsCmd = containsCmd
//        this.runFile = runFile
//    }

    boolean matches(String cmd);

    String name();


    void runProcess();

    boolean allowManyProcessesMatched();

}
