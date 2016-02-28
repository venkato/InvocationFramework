package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.ChannelSftp
import com.sshtools.sftp.SftpClient
import com.sshtools.ssh2.Ssh2Client
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

/**
 * TODO during download or upload dir : Support to filter out unwanted content. Support to download only if newer.
 */
@CompileStatic
class SftpUtils implements Closeable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    SshConSet sshConSet

    ChannelSftp sftp;
    public Ssh2Client ssh3
    SftpClient sftp2

    @Override
    void close() throws IOException {
        if(sftp!=null) {
            sftp.disconnect()
        }
        if(sftp2!=null) {
            sftp2.exit()
        }
        if(ssh3!=null) {
            ssh3.disconnect()
        }
    }

    String findRemoteFile(String remoteFile) {
        def tokenize = remoteFile.tokenize('/')
        String path = remoteFile.startsWith('/') ? '/' : ''
        boolean first = true
        tokenize.each { String el ->
            if (el.contains("*")) {
                sftp.cd(path)
                Collection<ChannelSftp.LsEntry> ls = sftp.ls(el) as Collection<ChannelSftp.LsEntry>
                switch (ls.size()) {
                    case 0:
                        throw new Exception("Not found : ${path}/${el}")
                    case 1:
                        if (first) {
                            first = false
                        } else {
                            path += '/'
                        }
                        path += ls.first().filename
                        break
                    default:
                        throw new Exception("Many files for : ${path}/${el} : ${ls.collect { it.filename }.sort()})")
                }
            } else {
                if (first) {
                    first = false
                } else {
                    path += '/'
                }
                path += el
            }
        }
        if (remoteFile.endsWith('/')) {
            path += '/'
        }
        return path
    }


}
