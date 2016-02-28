package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class CurlrcBuilder {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static File userHome = System.getProperty('user.home') as File
    public static String userName = System.getProperty('user.name') as File

    static void writeCurlrcFile(String proxy, String password) {
        assert userHome.exists()
        File curlrc = new File(userHome, ".curlrc");
        curlrc.text = buildCurl(proxy, userName, password);
    }

    static String buildCurl(String proxy, String user, String password) {
        String res = """
proxy = ${proxy}
proxy-user = ${user}:${password}
proxy-ntlm = true
"""
        return StringNewLIneUtils.normalizeLine(res);
    }


}
