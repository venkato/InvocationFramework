package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.util.logging.Logger

@CompileStatic
class PidDetector {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String cmdLineProp = 'sun.java.command'
    public static String javaAgentProp = '-javaagent:'
    public static String javaNativeAgentProp = '-agentpath:'

    static void printPid(){
        log.info "pid = ${detectPid()}"
    }

    static int detectPid(){
        RuntimeMXBean compilationMXBean = ManagementFactory.getRuntimeMXBean();
        String name = compilationMXBean.getName()
        int i = name.indexOf('@')
        if(i==-1){
            throw new Exception("Can't detect pid in : ${name}")
        }
        String substring = name.substring(0, i)
        return Integer.parseInt(substring)
    }

    static List<String> getCommandLine2(){
        return getCommandLine().tokenize(' ')
    }

    static String getCommandLine(){
        String cmdLine = System.getProperty(cmdLineProp)
        if(cmdLine==null){
            throw new IllegalStateException("prop not found : ${cmdLineProp}")
        }
        return cmdLine
    }

    static List<File> getAgents2(String agent){
        return getAgents(agent).collect {it as File}
    }

    static List<File> getNativeAgents3(){
        return getAgents2(javaNativeAgentProp)
    }

    static List<String> getAgents(String agent){
        RuntimeMXBean compilationMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> inputArguments = compilationMXBean.getInputArguments()
        List<String> agentProps = inputArguments.findAll {it.startsWith(agent)}
        List<String> result = agentProps.collect { it.substring(agent.length()) }
        return result
    }

}
