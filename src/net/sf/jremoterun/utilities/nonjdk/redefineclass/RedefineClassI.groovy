package net.sf.jremoterun.utilities.nonjdk.redefineclass

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.management.ObjectName;
import java.util.logging.Logger;

@CompileStatic
interface RedefineClassI {

    public ObjectName objectName = new ObjectName('iff:type=redefineClass')
    public String thisClassCl = RedefineClassI.getName()+'.classloaderIff'

    void redefineClassOnly(String className,byte[] bytes,String classloaderId);

    void redefineClassOnly(String className,String classloaderId);

    void redefineClassAndAnonClasses( String className,String classloaderId);

}
