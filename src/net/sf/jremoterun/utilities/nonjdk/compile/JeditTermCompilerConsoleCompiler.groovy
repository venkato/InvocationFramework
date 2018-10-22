package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustomRefsUrls
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JeditermBinRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class JeditTermCompilerConsoleCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List mavenIds = [
            LatestMavenIds.jcraft,
            LatestMavenIds.jcraftZlib,
            LatestMavenIds.guavaMavenIdNew,
            LatestMavenIds.log4jOld,
            LatestMavenIds.jnaPlatform,
            LatestMavenIds.jna,
            LatestMavenIds.jetbrainsAnnotations,
    ]


    ClRef clRef1 = new ClRef('com.jediterm.ssh.jsch.JSchTtyConnector');
    ClRef clRef2 = new ClRef('com.jediterm.terminal.ui.TerminalPanel');


    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    JeditTermCompilerConsoleCompiler() {
    }

//    File baseDir
    File buildDir


    void prepare() {
        detectBuildDir()
        buildDir.mkdir()
        assert buildDir.exists()
        compilerPure.outputDir = buildDir.child('jediterm_classes')
        compilerPure.outputDir.mkdirs()

        compilerPure.adder.addAll mavenIds
        compilerPure.adder.add CustomRefsUrls.pureJavacommnyJetBrainsUrl

        compilerPure.addInDir GitReferences.pty4jJetbrainsSrc


        JeditermBinRefs2.all.each { compilerPure.addInDir(it) }
        compilerPure.javaVersion = '1.8'
        hackSpeicicClass()
        hackSpeicicClass2()
        log.info("out dir : ${compilerPure.outputDir}")
    }


    public static String createJSchMethodName = 'createJSch';

    void hackSpeicicClass() {
        String specificClassSuffix1 = clRef1.className.replace('.', '/') + '.java';
        File specificFile = JeditermBinRefs2.ssh.childL(specificClassSuffix1).resolveToFile()
        assert specificFile.exists()
        File fileToRemove = compilerPure.files.find { it.getName() == specificFile.getName() }
        assert compilerPure.files.remove(fileToRemove)
        String text = specificFile.text
        String fromText1='JSch jsch = new JSch();'
        String fromText2='private Session connectSession(Questioner questioner) throws JSchException {'
        assert text.contains(fromText1)
        assert text.contains(fromText2)
        if (text.contains(fromText1)) {
            text = text.replace(fromText1, "JSch jsch = ${createJSchMethodName}();")
            text = text.replace(fromText2, """

protected JSch ${createJSchMethodName}(){return new JSch();}

protected Session connectSession(Questioner questioner) throws JSchException {

""")
        }
        File srcOverride = buildDir.child('src_override')
        srcOverride.mkdir()
        assert srcOverride.exists()
        File fileOverride = srcOverride.child(specificFile.getName())
        fileOverride.text = text
        compilerPure.files.add(fileOverride)

    }


    void hackSpeicicClass2() {
        String specificClassSuffix1 = clRef2.className.replace('.', '/') + '.java';
        File specificFile = JeditermBinRefs2.terminal.childL(specificClassSuffix1).resolveToFile();
        assert specificFile.exists()
        File fileToRemove = compilerPure.files.find { it.getName() == specificFile.getName() }
        assert compilerPure.files.remove(fileToRemove)
        String text = specificFile.text
        String fromText1='private void pasteFromClipboard(boolean useSystemSelectionClipboardIfAvailable) {'
        String fromText2='private HyperlinkStyle findHyperlink(Point p) {'
        assert text.contains(fromText1)
        assert text.contains(fromText2)
        if (text.contains(fromText1)) {
            text = text.replace(fromText1, "protected void pasteFromClipboard(boolean useSystemSelectionClipboardIfAvailable) {")
            text = text.replace(fromText2, "protected HyperlinkStyle findHyperlink(Point p) {")
        }
        File srcOverride = buildDir.child('src_override')
        srcOverride.mkdir()
        assert srcOverride.exists()
        File fileOverride = srcOverride.child(specificFile.getName())
        fileOverride.text = text
        compilerPure.files.add(fileOverride)

    }


    void detectBuildDir() {
        if (buildDir == null) {
            File baseDir = JeditermBinRefs2.terminal.ref.specOnly.resolveToFile()
            buildDir = baseDir.child('build')
        }
    }

    static File compileIfNeededS() {
        return new JeditTermCompilerConsoleCompiler().compileIfNeeded()
    }

    File compileIfNeeded() {
        detectBuildDir()
        File zipFile = buildDir.child('jediterm.jar')
        if (!zipFile.exists()) {
            doCompile()
        }
        assert zipFile.exists()
        return zipFile
    }

    void doCompile() {
        log.info "compiling JeditTerm"
        prepare()
        compilerPure.compile()
        zipp()
        log.info "JeditTerm compiled"
    }


    void checkSpecificFile() {
        String specificClassSuffix1 = clRef1.className.replace('.', '/') + '.class';
        File f = compilerPure.outputDir.child(specificClassSuffix1)
        assert f.exists()
        boolean methodExists = f.text.contains(createJSchMethodName)
        assert methodExists
        log.info "${createJSchMethodName} method found"
    }


    File zipp() {
        assert buildDir.exists()
        checkSpecificFile()
        File zipFile = buildDir.child('jediterm.jar')
        File zipFileTmp = buildDir.child('jeditermTmp.jar')
        zipFileTmp.delete()
        assert !zipFileTmp.exists()
        ZipUtil.pack(compilerPure.outputDir, zipFileTmp)
        FileUtilsJrr.copyFile(zipFileTmp, zipFile)
        return zipFile;
    }


}
