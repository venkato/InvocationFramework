package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorWithAdder
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddToAdderSelf;

import java.util.logging.Logger;

@CompileStatic
class AddDirectoryWithFiles implements AddToAdderSelf {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public File baseDir;
    public List<String> excludeFiles = [];

    public ClassPathCalculatorWithAdder classPathCalculatorWithAdder = new ClassPathCalculatorWithAdder() {
        @Override
        protected void addElement2(Object obj) {
            boolean needAdd = false
            if (obj instanceof File) {
                File file = (File) obj;
                needAdd = needAcceptFile(file)
            } else {
                needAdd = true
            }
            if (needAdd) {
                super.addElement2(obj)
            }
        }
    }

    boolean needAcceptFile(File file) {
        if (excludeFiles.contains(file.getName())) {
            return false
        }
        return true;

    }

    AddDirectoryWithFiles(File baseDir, List<String> excludeFiles) {
        this.baseDir = baseDir
        this.excludeFiles = excludeFiles
    }

    @Override
    void addToAdder(AddFilesToClassLoaderCommon adder) {
        classPathCalculatorWithAdder.addFilesToClassLoaderGroovySave.addAllJarsInDirAndSubdirsDeep(baseDir)
        adder.addAll(classPathCalculatorWithAdder.filesAndMavenIds)
    }
}
