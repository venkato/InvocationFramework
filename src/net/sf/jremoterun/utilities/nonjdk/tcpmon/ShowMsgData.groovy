package net.sf.jremoterun.utilities.nonjdk.tcpmon

import groovy.transform.CompileStatic;

@CompileStatic
public interface ShowMsgData {
	boolean isShowMsg(byte[] bs,int  begin, int len,String s) ;
}
