package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic;


@CompileStatic
enum ConnectionState {

    notInited,
    inProgressNoConnected,
    disconnected,
    inProgressConnected,
    AuthPassed,
    AuthFailed,
    ConnectionFailed,

}