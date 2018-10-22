package com.jcraft.jsch

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface UserAuthLoggingInterface {

     boolean canContinue(Session session);

     void logError(String msg);
     int getPassphraseCount() ;

     void onReply(int command) ;

     void sendingCommand(SshCmds cmd);



}
