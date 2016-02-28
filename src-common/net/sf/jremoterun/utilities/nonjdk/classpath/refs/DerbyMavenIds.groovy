package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains

@CompileStatic
enum DerbyMavenIds implements MavenIdContains {
    derbyLocale_zh_TW,
    derbyLocale_zh_CN,
    derbyLocale_ru,
    derbyLocale_pt_BR,
    derbyLocale_pl,
    derbyLocale_ko_KR,
    derbyLocale_ja_JP,
    derbyLocale_it,
    derbyLocale_hu,
    derbyLocale_fr,
    derbyLocale_es,
    derbyLocale_de_DE,
    derbyLocale_cs,
    derbyoptionaltools,
    derbytools,
    derbyclient,
    derbynet,
    derby,
    ;
    // war


    MavenId m;

    DerbyMavenIds() {
        m = new MavenId('org.apache.derby', name(), '10.14.2.0');
    }

    public static List<DerbyMavenIds> all = values().toList()

}
