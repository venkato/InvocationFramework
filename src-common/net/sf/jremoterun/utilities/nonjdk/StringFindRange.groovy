package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

/**
 * By default symbol excluded, if not written 'Inclusive'
 */
@CompileStatic
class StringFindRange {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String s;
    public int begin;
    public int end;

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


    void skipFromBegining(int numSyn){
        int newEnd = end - numSyn
        assert newEnd >0
        begin = numSyn;
        end = newEnd

    }

    /**
     * by default return the same as in : new consctuctor(s)
     */
    String subStringInclusiveBoth() {
        assert end + 1 <= s.length()
        return s.substring(begin, end + 1)
    }

    @Override
    String toString() {
        return subStringInclusiveBoth()
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






}
