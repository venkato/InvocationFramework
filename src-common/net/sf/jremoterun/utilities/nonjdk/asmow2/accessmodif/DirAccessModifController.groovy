package net.sf.jremoterun.utilities.nonjdk.asmow2.accessmodif

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class DirAccessModifController extends AccessModifController {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    void handleDirSelf(File inJarFile) {
        handleDir(inJarFile,inJarFile)
    }

    void handleDir(File inFileDir, File outputFileDir) {
        handleDirImpl(inFileDir,outputFileDir)
        onFinish()
    }

    void handleDirImpl(File inFileDir, File outputFileDir) {
        assert inFileDir.exists()
        inFileDir.listFiles().toList().each {
            File child = outputFileDir.child(it.name)
            if (it.isDirectory()) {
                child.mkdir()
                assert child.exists()
                handleDirImpl(it, child)
            } else if (it.isFile()) {
                handleFile(it, child)
            }else{
                throw new Exception("Stange file : ${it}")
            }
        }
    }

    void handleFile(File inFile, File outputFile) {
        outputFile.bytes = removeFinalModifier(inFile.absolutePath, inFile.bytes)
    }

}
