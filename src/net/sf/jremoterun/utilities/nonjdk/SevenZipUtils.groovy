package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.FileOutputStream2
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile

import java.util.logging.Logger

@Deprecated
@CompileStatic
class SevenZipUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void decompress(File inFile, File destination) throws IOException {
        SevenZFile sevenZFile = new SevenZFile(inFile);
        SevenZArchiveEntry entry;
        while ((entry = sevenZFile.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            File curfile = new File(destination, entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            FileOutputStream2 out = new FileOutputStream2(curfile);
            try {
                byte[] content = new byte[(int) entry.getSize()];
                sevenZFile.read(content, 0, content.length);
                out.write(content);
            } finally {
                out.close();
            }
        }
    }

}
