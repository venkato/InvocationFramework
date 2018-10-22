package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.dircache.DirCacheEntry
import org.eclipse.jgit.util.QuotedString;

import java.util.logging.Logger

import static org.eclipse.jgit.diff.DiffEntry.ChangeType.ADD
import static org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE
import static org.eclipse.jgit.lib.Constants.encode
import static org.eclipse.jgit.lib.Constants.encode
import static org.eclipse.jgit.lib.Constants.encodeASCII;

@CompileStatic
class DiffFormatterJrr extends DiffFormatter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static byte[] newLineBytes = '\n'.getBytes()
    public static byte[] gitDiffBytes = "diff --git ".getBytes()


    DiffFormatterJrr(OutputStream out) {
        super(out)
    }

    String getPath12(DiffEntry.ChangeType type, String oldPath, String newPath) {
        switch (type) {
            case DiffEntry.ChangeType.MODIFY:
                return oldPath
            case DiffEntry.ChangeType.DELETE:
                return "${oldPath} ${type}"
            case DiffEntry.ChangeType.ADD:
                return "${newPath} ${type}"
            case DiffEntry.ChangeType.RENAME:
                return "${oldPath} ${type} ${newPath}"
            case DiffEntry.ChangeType.COPY:
                return "${newPath} ${type} from ${oldPath}"
            default:
                throw new IllegalArgumentException("${type} ${oldPath} ${newPath}")
        }
    }

    @Override
    protected void formatGitDiffFirstHeaderLine(ByteArrayOutputStream o, DiffEntry.ChangeType type, String oldPath, String newPath) throws IOException {
        String path = getPath12(type, oldPath, newPath);
        o.write(encodeASCII("diff --git ")); //$NON-NLS-1$
        o.write(encode(QuotedString.GIT_PATH.quote(path)));
        o.write(newLineBytes);
    }
}
