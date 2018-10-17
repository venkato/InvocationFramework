package net.sf.jremoterun.utilities.nonjdk.log;

import groovy.transform.CompileStatic;

import java.io.File;

// !! must be java file to compile in idea
@CompileStatic
public class FileExtentionClass {

    public static File child(File parent, String c) {
        if (parent == null) {
            throw new IllegalArgumentException("parent file is null");
        }
        return new File(parent, c);
    }

    public static String getAbsolutePathUnix(File file) {
        return file.getAbsolutePath().replace('\\','/');
    }

    public static boolean isChildFile(File parent, File child) {
        if (parent == null) {
            throw new IllegalArgumentException("parent file is null");
        }
        if (child == null) {
            throw new IllegalArgumentException("child file is null");
        }
        String parentPath = parent.getAbsolutePath().replace('\\', '/').toLowerCase();
        String childPath = child.getAbsolutePath().replace('\\', '/').toLowerCase();
        return childPath.startsWith(parentPath);

    }

    public static String getPathToParent(File parent, File child) {
        if(!isChildFile(parent,child)){
            throw new IllegalArgumentException("child = "+child+" has incorrect parent : "+parent);
        }
        String parentPath = parent.getAbsolutePath().replace('\\', '/');
        String childPath = child.getAbsolutePath().replace('\\', '/');
        assert childPath.length() >= parentPath.length();
        String res = childPath.substring(parentPath.length());
        if (res.startsWith("/")) {
            return res.substring(1);
        }
        return res;
    }




}
