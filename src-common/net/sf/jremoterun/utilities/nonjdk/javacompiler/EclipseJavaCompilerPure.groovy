package net.sf.jremoterun.utilities.nonjdk.javacompiler

import groovy.io.FileType
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse.EclipseCompiler3
import org.apache.commons.io.FileUtils

import java.util.logging.Logger

@CompileStatic
public class EclipseJavaCompilerPure {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public File outputDir
    public AddFileToClassloaderDummy adder = new AddFileToClassloaderDummy()
    List<File> files = []
    EclipseCompiler3 compiler3;
    String javaVersion
    public boolean rememberOutput = false;
    List<String> additionalFlags = ['-g','-nowarn',]

    void addInDir(ToFileRef2 f ){
        addInDir f.resolveToFile()
    }

    void addInDir(File f ){
        assert f.exists()
        if(f.file) {
            files.add(f)
        }else{
            assert f.directory
            f.eachFileRecurse(FileType.FILES, {
                addFileImpl(it)
            })
        }
    }

    void addFileImpl(File f2){
        String name = f2.name
        if (name.endsWith('.java')) {
            files.add(f2)
        }
    }

    void checkOutDir(){

        assert outputDir!=null
        FileUtils.deleteQuietly(outputDir)
        outputDir.mkdir()
        assert outputDir.exists()
        outputDir.deleteDir()
        outputDir.mkdir()
        if (!outputDir.exists()) {
            throw new FileNotFoundException("failed create ${outputDir}")
        }

        assert outputDir.listFiles().length == 0
    }

    StringWriter createCompiler( ){
        StringWriter javacOutput = new StringWriter();
        PrintWriter writer = new PrintWriter(javacOutput);
        compiler3 = new EclipseCompiler3(writer, writer, false, null, null,rememberOutput)
        return javacOutput
    }

    void compile() {
        assert javaVersion!=null
        checkOutDir()
        files = files.unique();
        String[] javacParameters = makeParameters();
        StringWriter javacOutput  = createCompiler()
        boolean result = compile2(javacParameters)
//        log.info "result : ${result}"
        if (result) {
            String trim2 = javacOutput.toString().trim()
            if (trim2.length() > 0) {
                log.info "${trim2}";
            }
        } else {
            String header = "Compile error \n${javacOutput}"
            throw new Exception(header)
        }
    }

    boolean compile2(String[] javacParameters ){

//        javacParameters = ['-help']
//        log.info "${Arrays.toString javacParameters}"
        boolean result = compiler3.compile(javacParameters)
        return result
    }


    private String[] makeParameters() {
        if(files.size()==0){
            throw new Exception("No file to compile")
        }
        LinkedList<String> params = new LinkedList<String>();
        params.addAll(additionalFlags)
        params.add("-d");
        assert outputDir!=null
        params.add(outputDir.getAbsolutePath());
        // add flags
        params.add('-source')
        params.add(javaVersion)
        params.add('-target')
        params.add(javaVersion)
        if (adder.addedFiles2.size()>0) {
            params.add("-classpath");
            params.add(adder.addedFiles2.join(File.pathSeparator));
        }else{
        }

        params.addAll(files.collect {it.absolutePath});

        return params.toArray(new String[params.size()]);
    }


}
