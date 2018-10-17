package net.sf.jremoterun.utilities.nonjdk.jnautils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.jna.jvmtiutils2.JnaBean;

import java.util.logging.Logger;

@CompileStatic
class JrrJnaUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    Map<String,List<Socket>> findDuplicatedSockets(){
        Set<Socket> sockets = JnaBean.jnaBean.findSocket()
        sockets = sockets.findAll {it.isConnected()}
        Map<String,Socket> uniq = [:]
        Map<String,List<Socket>> dups = [:]
        sockets.each {
            String socketId = getSocketId(it)
            List<Socket> socketsDupss = dups.get(socketId)
            if(socketsDupss!=null){
                socketsDupss.add(it)
            }else{
                Socket socketAnother = uniq.put(socketId, it)
                if(socketAnother!=null){
                    socketsDupss = [it,socketAnother]
                    dups.put(socketId,socketsDupss)
                }
            }
        }

        return dups;
    }

    String getSocketId(Socket socket){
        InetAddress inetAddress = socket.getInetAddress()
        if(inetAddress==null){
            log.info "inet address is null for ${socket}"
        }
        String hostAddress = inetAddress.getHostAddress()
        return "${hostAddress}:${socket.getPort()}"
    }

}
