package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileWithSources

import java.util.logging.Logger

@CompileStatic
class AddFilesToEclipse extends AddFileWithSources{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<BinaryWithSource> binWithSources = []

    AddFilesToEclipse() {
        downloadSources = true
    }

    @Override
    void addLibraryWithSource(File binary, List<File> source) throws Exception {
        binWithSources.add new BinaryWithSource(binary,source[0]);
    }

    @Override
    void addSourceFImpl(File source) throws Exception {

    }

    @Override
    void addSourceS(String source) throws Exception {

    }






    void saveToDir(String libname,File dir){
        JrrUtilities.checkFileExist(dir)
        File file = new File(dir,"${libname}.userlibraries")
        assert file.parentFile.exists()
        file.text= saveToString(libname)
    }

    void saveToFile(String libname,File file){
        assert file.parentFile.exists()
        file.text= saveToString(libname)
    }

    boolean filter1(BinaryWithSource b){
        String fileName = b.binary.name
        switch (fileName){
            case {fileName.startsWith('groovy-')}:
                log.info "ignore groovy : ${b.binary}"
                return false
            case {fileName.startsWith('junit-')}:
                log.info "ignore jnit : ${b.binary}"
                return false
            default:
                return true
        }
    }

    void filter(){
        binWithSources=binWithSources.findAll {filter1(it)}
    }

    String saveToString(String libname){
        filter()
        List<String> collect = binWithSources.collect {
            assert it.binary != null
            String binPath = it.binary.canonicalFile.absolutePath.replace('\\', '/');
            if (it.source == null) {
                return "<archive path=\"${binPath}\" />" as String;
            } else {
                String srcPath = it.source.canonicalFile.absolutePath.replace('\\', '/');
                return "<archive path=\"${binPath}\" source=\"${srcPath}\" />" as String;
            }
        }
        collect=collect.collect{"        ${it}" as String}
        String content = collect.join("\n")
        String headerFooter="""<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<eclipse-userlibraries version="2">
    <library name="${libname}" systemlibrary="false">
${content}        
    </library>
</eclipse-userlibraries>

"""
        return headerFooter
    }





}
