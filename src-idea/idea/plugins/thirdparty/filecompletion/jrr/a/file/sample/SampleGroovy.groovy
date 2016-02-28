package idea.plugins.thirdparty.filecompletion.jrr.a.file.sample

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds

import java.nio.charset.Charset;

/**
 * Created by nick on 04.03.2017.
 */
@CompileStatic
public class SampleGroovy {

    public static File file1 = new File("c:/1/");

    private void testNoyUsed(){
        new File("c:/windows/System32/drivers/");
        Charset.forName("273");
        "aaa".toString()
        file1.toString();
        "C:\\progi\\idea\\2017.2ce\\lib\\annotations.jar" as File
        LatestMavenIds.sshd.toString()
        file1.child("gcc2/regeva/jremoterun-1.0.zip");
    }
}
