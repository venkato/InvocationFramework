package net.sf.jremoterun.utilities.nonjdk.cvsutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class CsvUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static char dotSymbol = '.'.charAt(0)

    static String normalizePrice(String price) {
        if (price == null) {
            return null
        }
        if (price.length() == 0) {
            return ''
        }
        int i = price.indexOf(dotSymbol as int)
        if (i == -1) {
            return price
        }
        if (i > 0) {
            if (price.charAt(price.length() - 1) == '0'.charAt(0)) {
                price = price.substring(0, price.length() - 1)
                return normalizePrice(price)
            }
        }
        if (i == 0) {
            price = '0' + price
        }
        char lastSymbol = price.charAt(price.length() - 1)
        if (lastSymbol == dotSymbol) {
            price = price.substring(0, price.length() - 1)
        }
        return price
    }

}
