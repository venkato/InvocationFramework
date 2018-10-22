package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.junit.Test

import java.util.logging.Logger;

@CompileStatic
class VersionComparator implements Comparator<String> {


    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Map DEFAULT_SPECIAL_MEANINGS_default;

    public Map DEFAULT_SPECIAL_MEANINGS = [:];
    List<String> badWords = []

    static {
        DEFAULT_SPECIAL_MEANINGS_default = new HashMap();
        DEFAULT_SPECIAL_MEANINGS_default.put("dev", new Integer(-1));
        DEFAULT_SPECIAL_MEANINGS_default.put("rc", new Integer(1));
        DEFAULT_SPECIAL_MEANINGS_default.put("final", new Integer(2));
    }

    VersionComparator() {
        DEFAULT_SPECIAL_MEANINGS.putAll(DEFAULT_SPECIAL_MEANINGS_default)
    }

    @Test
    void test1(){
        assert compare('1.2.1','1.3.1') < 0
        assert compare('1.2.1','1.31.1') < 0
        assert compare('1.21.1','1.3.1') > 0
        assert compare('1.21','1.3.1') > 0
        assert compare('1.2','1.31.1') < 0
        assert compare('1.2','1.2') ==0
        assert compare('1.2dev','1.31.1') < 0

    }

/**
 *
 * return 1 if versionSaved > versionCandidate
 */
    @Override
    int compare(String versionSaved, String versionCandidate) {
        versionSaved = versionSaved.toLowerCase()
        versionCandidate = versionCandidate.toLowerCase()

        boolean savedHasNonStableVersion = badWords.find {
            versionSaved.contains(it)
        } != null
        boolean candidateHasNonStableVersion = badWords.find {
            versionCandidate.contains(it)
        } != null
        if (savedHasNonStableVersion && !candidateHasNonStableVersion) {
            return 1
        }
        if (!savedHasNonStableVersion && candidateHasNonStableVersion) {
//            log.info "vs = ${versionCandidate} ${versionSaved}"
            return -1
        }
        int res = isOverrideMavenId(versionSaved, versionCandidate)
        return res
    }


    int isOverrideMavenId(String rev1, String rev2) {

        rev1 = rev1.replaceAll('([a-zA-Z])(\\d)', '$1.$2');
        rev1 = rev1.replaceAll('(\\d)([a-zA-Z])', '$1.$2');
        rev2 = rev2.replaceAll('([a-zA-Z])(\\d)', '$1.$2');
        rev2 = rev2.replaceAll('(\\d)([a-zA-Z])', '$1.$2');

        String[] parts1 = rev1.split('[\\._\\-\\+]');
        String[] parts2 = rev2.split('[\\._\\-\\+]');

        int i = 0;
        for (; i < parts1.length && i < parts2.length; i++) {
            if (parts1[i].equals(parts2[i])) {
                continue;
            }
            boolean is1Number = isNumber(parts1[i]);
            boolean is2Number = isNumber(parts2[i]);
            if (is1Number && !is2Number) {
                return 1;
            }
            if (is2Number && !is1Number) {
                return -1;
            }
            if (is1Number && is2Number) {
                return Long.valueOf(parts1[i]).compareTo(Long.valueOf(parts2[i]));
            }
            // both are strings, we compare them taking into account special meaning
            Integer sm1 = (Integer) DEFAULT_SPECIAL_MEANINGS.get(parts1[i].toLowerCase(Locale.US));
            Integer sm2 = (Integer) DEFAULT_SPECIAL_MEANINGS.get(parts2[i].toLowerCase(Locale.US));
            if (sm1 != null) {
                sm2 = sm2 == null ? new Integer(0) : sm2;
                return sm1.compareTo(sm2);
            }
            if (sm2 != null) {
                return new Integer(0).compareTo(sm2);
            }
            return parts1[i].compareTo(parts2[i]);
        }
        if (i < parts1.length) {
            return isNumber(parts1[i]) ? 1 : -1;
        }
        if (i < parts2.length) {
            return isNumber(parts2[i]) ? -1 : 1;
        }
        return 0;
    }

    boolean isNumber(String str) {
        return str.matches('\\d+');

    }

}
