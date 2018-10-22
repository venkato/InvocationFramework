package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenFileType2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.NameMapper
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class JrrJavassistUtilsCompiler  extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public ClRef defaultRepositoryCacheManagerClRef = new ClRef('org.apache.ivy.core.cache.DefaultRepositoryCacheManager')


    public static List mavenIds =  [
            LatestMavenIds.log4jOld
    ]

    void prepare() {
        params.javaVersion = '1.7'
        client.adder.addAll DropshipClasspath.allLibsWithGroovy
        client.adder.addAll mavenIds
        client.adder.add mcu.getToolsJarFile()
//        client.adder.addMavenPath DropshipClasspath.sisiFileBin
    }

    void updateDefaultRepositoryCacheManager(File srcDir ){

        String suffix = defaultRepositoryCacheManagerClRef.className.replace('.','/')+'.java'
        File childF = srcDir.child(suffix)
        childF.delete()
        assert !childF.exists()
        childF.parentFile.mkdirs()
        assert childF.parentFile.exists()
        MavenCommonUtils mcu = new MavenCommonUtils();
        mcu.fileType = MavenFileType2.source.fileSuffix
        File f =mcu.findMavenOrGradle(DropshipClasspath.ivyMavenId.m)
        log.info "makeing class ${defaultRepositoryCacheManagerClRef} public from ${f}"
        assert f!=null : DropshipClasspath.ivyMavenId.toString()
        NameMapper nameMapper = new NameMapper() {
            @Override
            String map(String name) {
                if(name==suffix){
                    return name
                }
                return null
            }
        }
        ZipUtil.unpack(f,srcDir,nameMapper)
        assert childF.exists()
        childF.text = childF.text.replace('private','public')
        addInDir(srcDir)
    }

    void zip(File destJar ){
        File tmpJar = params.outputDir.parentFile.child('jrrassist.jar')
        ZipUtil.pack(params.outputDir, tmpJar)
        FileUtilsJrr.copyFile(tmpJar,destJar)
        log.info "copied to ${destJar}"
    }

}
