package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.lang3.SystemUtils

import java.util.logging.Logger

@CompileStatic
class SshConSet2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public static SshConSet2 defaultSet = new SshConSet2();
    public boolean showMessage = true

    public static int defaultPort = 22;
    public static int maxRetry = 22;
    public static String defaultPassword;
    public static String defaultUser = SystemUtils.USER_NAME;
    public static File defaultSshKey = new File(SystemUtils.USER_HOME + "/.ssh/id_rsa");
    public static File defaultKnownHosts = new File(SystemUtils.USER_HOME + '/.ssh/known_hosts'); ;
    static {
        if (!defaultSshKey.exists()) {
            defaultSshKey = null
        }
        if (!defaultKnownHosts.exists()) {
            defaultKnownHosts = null
        }
    }

    public String host;
    public int port = defaultPort;
    public String user = defaultUser
    public String password = defaultPassword
    public File sshKey = defaultSshKey
    public File knownHosts = defaultKnownHosts

    @Override
    String toString() {
        return "${user}@${host} , sshkey = ${sshKey}, password set ? ${password != null}, ${knownHosts}"
    }
}
