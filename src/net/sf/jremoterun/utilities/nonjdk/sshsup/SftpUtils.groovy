package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.SftpATTRS
import com.sshtools.sftp.SftpClient
import com.sshtools.ssh2.Ssh2Client
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.channels.JrrChannelSftp

import java.util.logging.Logger

/**
 * TODO during download or upload dir : Support to filter out unwanted content. Support to download only if newer.
 */
@CompileStatic
class SftpUtils implements Closeable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    SshConSet sshConSet

    JrrChannelSftp sftp;
    public Ssh2Client ssh3
    SftpClient sftp2

    @Override
    void close() throws IOException {
        if (sftp != null) {
            sftp.disconnect()
        }
        if (sftp2 != null) {
            sftp2.exit()
        }
        if (ssh3 != null) {
            ssh3.disconnect()
        }
    }

    String findRemoteFile(String remoteFile) {
        try {
            return findRemoteFileImpl(remoteFile)
        } catch (Exception e) {
            log.info "failed find remote file : ${remoteFile}"
            throw e
        }
    }

    void cd(String remotePath) {
        try {
            sftp.cd(remotePath)
        } catch (Exception e) {
            log.info "failed cd to : ${remotePath}"
            throw e
        }
    }

    InputStream get(String remotePath) {
        try {
            return sftp.get(remotePath)
        } catch (Exception e) {
            log.info "failed get : ${remotePath}"
            throw e
        }
    }

    void getFile(String remotePath, File dest) {
        get(remotePath, dest)
    }

    void get(String remotePath, File dest) {
        try {
            sftp.get(remotePath, dest.getAbsolutePath())
        } catch (Exception e) {
            log.info "failed get : ${remotePath}"
            throw e
        }
    }

    void putFile(File src, String remotePath) {
        put(src, remotePath)
    }

    /**
     * sample permission : 755 - write user only, other can read only
     */
    void setPermissions(String remotePath, int permissions){
        String permS = permissions;
        sftp.chmod(Integer.parseInt(permS,8),remotePath)
    }

    void setPermissions2(String remotePath, int permissions){
        String permS = permissions;
        sftp2.chmod(Integer.parseInt(permS,8),remotePath)
    }

    void put(File src, String remotePath) {
        try {
            sftp.put(src.getAbsolutePath(), remotePath)
        } catch (Exception e) {
            log.info "failed put ${src} to ${remotePath}"
            throw e
        }
    }


    String findRemoteFile2(String remoteFile) {
        return findRemoteFile2(remoteFile, 1)
    }

    String findRemoteFile2(String remoteFile, int maxFoundCount) {
        List<String> res = findRemoteFiles2(remoteFile, maxFoundCount)
        int size = res.size()
        if (size == 0) {
            throw new Exception("not found : ${remoteFile}")
        }
        if (size > 1) {
            throw new Exception("found many : ${res.sort()}")
        }
        return res[0]
    }

    List<String> findRemoteFiles2(String remoteFile, int maxFoundCount) {
        remoteFile = remoteFile.replace('//', '/')
        remoteFile = remoteFile.replace('//', '/')
        List<String> subPaths = remoteFile.tokenize('/')
        List<String> sub2 = new ArrayList<>(subPaths)
        String cur = sub2.remove(0)
        return handleSubPath('', cur, sub2, maxFoundCount, true, false)
    }


    boolean changeDir(String pathParent, boolean throwExc) {
        try {
            if (pathParent.length() == 0) {
                sftp.cd('/')
            } else {
                sftp.cd(pathParent)
            }
            return true
        } catch (Exception e) {
            log.info "failed cd to : ${pathParent} ${e}"
            if (throwExc) {
                throw e
            }
            return false
        }
    }

    List<String> handleSubPath(final String pathParent, String el, List<String> subPaths, int maxFoundCount, boolean throwExc, boolean forceCd) {
        if (!el.contains("*")) {
            String s = pathParent + '/' + el;
            if (forceCd) {
                if (!changeDir(s, throwExc)) {
                    return []
                }
            }
            if (subPaths.size() == 0) {
                return [s]
            }
            List<String> sub2 = new ArrayList<>(subPaths)
            String cur = sub2.remove(0)
            return handleSubPath(s, cur, sub2, maxFoundCount, throwExc, forceCd)
        }
        if (!changeDir(pathParent, throwExc)) {
            return []
        }
        Collection<ChannelSftp.LsEntry> result = handleElementAstrix(el, pathParent)
        int size = result.size()
        if (size == 0) {
            if (throwExc) {
                throw new Exception("no matches in ${pathParent} for ${el}")
            }
            return []
        }
        List<String> fileNames = result.collect { it.getFilename() }.sort()
        log.info "result many : ${}"

        if (size == 1) {
            String s = pathParent + '/' + fileNames[0]
            if (subPaths.size() == 0) {
                return [s]
            }
            List<String> sub2 = new ArrayList<>(subPaths)
            String cur = sub2.remove(0)
            return handleSubPath(s, cur, sub2, maxFoundCount, throwExc, forceCd)
        }
        if (size > maxFoundCount) {
            throw new FoundManyException("Found many matches in ${pathParent} candidates : ${fileNames} , max allowed : ${maxFoundCount}")
        }
        if (subPaths.size() == 0) {
            return result.collect { pathParent + '/' + it }
        }
        List<String> sub2 = new ArrayList<>(subPaths)
        String cur = sub2.remove(0)
        int newMaxFoundCount = maxFoundCount - size
        List<String> result2 = []
        result.each {
            SftpATTRS attrs = it.getAttrs()
            if (attrs.isDir()) {
                String s = pathParent + '/' + it.getFilename();

                log.info "going to ${s}"
                result2.addAll(handleSubPath(s, cur, sub2, newMaxFoundCount, false, true))
            } else {
                log.info "not a dir : ${it.getFilename()} from ${pathParent}"
            }
        }

        if (result2.size() == 0 && throwExc) {
            throw new Exception("no matches in ${pathParent} for ${el}")
        }
        return result2;
    }

    Collection<ChannelSftp.LsEntry> handleElementAstrix(String el, final String path) {
        try {
            Collection<ChannelSftp.LsEntry> ls = sftp.ls(el) as Collection<ChannelSftp.LsEntry>
            switch (ls.size()) {
                case 0:
                    return []
//                case 1:
//                    return [ls.first().getFilename()]
//                    break
                default:
                    return ls;
            //throw new Exception("Many files for : ${path}/${el} : ${ls.collect { it.getFilename() }.sort()})")
            }
        } catch (Exception e) {
            log.info "failed list in : ${path} for ${el}"
            throw e
        }
//        }
//        return [path + el]

    }

    String findRemoteFileImpl(String remoteFile) {
        List<String> tokenize = remoteFile.tokenize('/')
        String path = remoteFile.startsWith('/') ? '/' : ''
        boolean first = true
        tokenize.each { String el ->
            if (el.contains("*")) {
                try {
                    sftp.cd(path)
                } catch (Exception e) {
                    log.info "failed cd to : ${path}"
                    throw e
                }
                try {
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
                } catch (Exception e) {
                    log.info "failed list in : ${path} for ${el}"
                    throw e
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
