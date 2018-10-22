package net.sf.jremoterun.utilities.nonjdk.packun

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.SshConSet3
import net.sf.jremoterun.utilities.nonjdk.store.ListStore
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil;

import java.util.logging.Logger;

@CompileStatic
class Unpack {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File baseDir
    List packInfos
    File backupDir

    Unpack(File baseDir,File backupDir) {
        this(baseDir.child(Pack.infoFile),baseDir,backupDir)
    }

    Unpack(File configFile, File baseDir,File backupDir) {
        ListStore config =new ListStore(configFile)
        List<PackInfo> packInfos = config.loadsettings()
        this.packInfos = packInfos
        this.baseDir = baseDir
    }

    static Unpack downloadFromRemoteHost(SshConSet3 sshConSet3, String remoteDir,File localDir,File backupDir){
        sshConSet3.createSftpUtils().sftp2.copyRemoteDirectory(remoteDir,localDir.absolutePath,true,false,true,null)
        return new Unpack(localDir,backupDir)
    }

    void unpackAll(){
        packInfos.each {
            if (it instanceof EmptyDir) {
                it.dir.mkdirs()
            }else {
                handlePack((PackInfo)it)
            }

        }
    }

    void handlePack(PackInfo packInfo){
        File zipFile = baseDir.child(packInfo.zipLocation)
        assert zipFile.exists()
        if(packInfo.unzipLocation.exists()){
            assert packInfo.unzipLocation.directory
            if(packInfo.unzipLocation.listFiles().length>0){
                rename(packInfo)
            }
//            FileUtils.cleanDirectory(packInfo.unzipLocation)
        }
        packInfo.unzipLocation.mkdirs()
        assert packInfo.unzipLocation.exists()
        handlePackImpl(zipFile,packInfo)
    }


    void rename(PackInfo packInfo){
        File backupdir2=backupDir.child(packInfo.zipLocation)
        assert !backupdir2.exists()
        File unzipLoc =packInfo.unzipLocation
        assert unzipLoc.renameTo(backupdir2)
    }

    void handlePackImpl(File zipFile,PackInfo packInfo){
        ZipUtil.unpack(zipFile,packInfo.unzipLocation)
    }


}
