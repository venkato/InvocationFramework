package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class StringFindRange {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String s;
    int begin;
    int end;

    StringFindRange(String s, int begin, int end) {
        this.s = s
        this.begin = begin
        this.end = end
    }

    StringFindRange(String s) {
        this.s = s
        begin = 0
        end = s.length() - 1
    }

    String subExclusive() {
        return s.substring(begin, end + 1)
    }

    String subStringExlusiveBoth() {
        assert begin+1<=end
        return s.substring(begin+1, end )
    }

    String subStringInclusiveStart() {
        return s.substring(begin, end )
    }


    String subStringInclusiveEnd() {
        return s.substring(begin+1, end+1 )
    }

    String subStringInclusiveBoth() {
        assert end + 1 <= s.length()
        return s.substring(begin, end + 1)
    }





}
