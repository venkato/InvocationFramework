package com.jcraft.jsch;

import groovy.transform.CompileStatic;

@CompileStatic
public enum HostCheckResultEnum {

    OK(KnownHosts.OK),
    CHANGED(KnownHosts.CHANGED),
    NOT_INCLUDED(KnownHosts.NOT_INCLUDED),
    ;

    public int status;

    HostCheckResultEnum(int status1) {
        status = status1;
    }

    public static Map<Integer,HostCheckResultEnum> statusMap = values().collectEntries{[it.status,it]}

}
