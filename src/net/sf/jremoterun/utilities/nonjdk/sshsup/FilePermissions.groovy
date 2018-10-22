package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
enum FilePermissions {

    read(4),
    write(2),
    execute(1),
    ;


    public byte offset;

    FilePermissions(int offset) {
        this.offset = (byte) offset
    }

    List<List<FilePermissions>> decodeAll(int permission){
        int base1 = permission & 7;
        int base2= Integer.parseInt('70',8);
        int base3= Integer.parseInt('700',8);
        int permission1 = permission&base1
        int permission2 = permission&base2
        int permission3 = permission&base3
        return null;
    }

    static byte getPermission(int permission, byte offset){

    }

    static List<FilePermissions> decode(byte permission){
        return values().toList().findAll{it.offset && permission >0}
    }

    byte incode(Collection<FilePermissions> permissions){
        Collection<FilePermissions> permissions1 = permissions.unique()
        byte result = 0;
        permissions1.each {
            result+=it.offset
        }
        return result;
    }
}
