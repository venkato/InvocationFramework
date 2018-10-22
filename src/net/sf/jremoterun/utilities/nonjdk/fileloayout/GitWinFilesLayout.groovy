package net.sf.jremoterun.utilities.nonjdk.fileloayout

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
enum GitWinFilesLayout {


    bashExe('bin/bash.exe'),
    lessExe('usr/bin/less.exe'),
    findExe('usr/bin/find.exe'),

    binDir('bin/'),
    mingwBinDir('mingw64/bin/'),
    usrBinDir('usr/bin/'),
    ;


    public String subPath;

    GitWinFilesLayout(String subPath) {
        this.subPath = subPath
    }

    boolean isDir() {
        return subPath.endsWith('/');
    }

    boolean isExecFile(){
        return name().endsWith('exe')
    }

    File buildFile(File baseDir) {
        return new File(baseDir, subPath);
    }


}
