package com.jcraft.jsch

import groovy.transform.CompileStatic;


@CompileStatic
enum SshCmds {

    SSH_MSG_SERVICE_REQUEST(                 5),
    SSH_MSG_SERVICE_ACCEPT(                6),
//    SSH_MSG_SERVICE_REQUEST(                 Session.SSH_MSG_SERVICE_REQUEST),
//    SSH_MSG_SERVICE_ACCEPT(                Session.SSH_MSG_SERVICE_ACCEPT),
    SSH_MSG_USERAUTH_REQUEST(               UserAuth.SSH_MSG_USERAUTH_REQUEST),
    SSH_MSG_USERAUTH_FAILURE(               UserAuth.SSH_MSG_USERAUTH_FAILURE),
    SSH_MSG_USERAUTH_SUCCESS(               UserAuth.SSH_MSG_USERAUTH_SUCCESS),
    SSH_MSG_USERAUTH_BANNER(                UserAuth.SSH_MSG_USERAUTH_BANNER),
    SSH_MSG_USERAUTH_INFO_REQUEST(          UserAuth.SSH_MSG_USERAUTH_INFO_REQUEST),
    SSH_MSG_USERAUTH_INFO_RESPONSE(         UserAuth.SSH_MSG_USERAUTH_INFO_RESPONSE),
    SSH_MSG_USERAUTH_PK_OK(                 UserAuth.SSH_MSG_USERAUTH_PK_OK),
    SSH_MSG_USERAUTH_PASSWD_CHANGEREQ( 60),
    ;

    public int cmdId;

    SshCmds(int cmdId) {
        this.cmdId = cmdId
    }
}