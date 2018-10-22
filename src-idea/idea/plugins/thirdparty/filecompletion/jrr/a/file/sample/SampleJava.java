package idea.plugins.thirdparty.filecompletion.jrr.a.file.sample;

import java.io.File;
import java.nio.charset.Charset;


public class SampleJava {

   public static File f = new File("c:/a/");

    private void testNoyUsed(){
        new File("c:/windows/System32/drivers/");
        Charset.forName("273");
        f.toString();
        new File(SampleGroovy.file1, ".gitignore");
        new File(f, ".gitignore");
        new File(SampleEnum.a1.f, ".gitignore");
    }
}
