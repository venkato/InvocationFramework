package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class UrlSymbolsReplacer {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static String replaceBadSymbols(String src) {
        String dirSuffix = src;
        dirSuffix = dirSuffix.replace("://", "/")
        dirSuffix = dirSuffix.replace('%25', '%')
        dirSuffix = dirSuffix.replace('%20', '-')
        dirSuffix = dirSuffix.replace('%', '-')
        dirSuffix = dirSuffix.replace(':', '-')
        dirSuffix = dirSuffix.replace('?', '/')
        dirSuffix = dirSuffix.replace('=', '/')
        dirSuffix = dirSuffix.replace('&', '/')

        dirSuffix = dirSuffix.replace('@', '/');
        dirSuffix = dirSuffix.replace(':', '/')
        dirSuffix = dirSuffix.replace('//', '/')
        dirSuffix = dirSuffix.replace('--', '-')
        if (dirSuffix.startsWith('/')) {
            dirSuffix = dirSuffix.substring(1)
        }
        return dirSuffix

    }


}
