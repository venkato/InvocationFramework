package net.sf.jremoterun.utilities.nonjdk.net.okhttp

import groovy.transform.CompileStatic;


@CompileStatic
enum NtlmState {

        UNINITIATED,
        CHALLENGE_RECEIVED,
        MSG_TYPE1_GENERATED,
        MSG_TYPE2_RECEVIED,
        MSG_TYPE3_GENERATED,
        FAILED,


}