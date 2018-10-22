package net.sf.jremoterun.utilities.nonjdk.shell

import me.bazhenov.groovysh.GroovyShellService;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;



// ssh  -o "StrictHostKeyChecking=no" -p port host
@CompileStatic
class GroovyShellSshService extends GroovyShellService{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<String,Object> binding2 = [:]

    GroovyShellSshService(int port) {
        super(port)
        setBindings(binding2)
    }

    void varPut(String varName, Object varValue){
        binding2.put(varName,varValue)
    }

    void varPutAll(Map<String,Object>  vars){
        binding2.putAll(vars)
    }

    void setAllowConnectFromLocalHost(){
        setHost("127.0.0.1")
    }
}
