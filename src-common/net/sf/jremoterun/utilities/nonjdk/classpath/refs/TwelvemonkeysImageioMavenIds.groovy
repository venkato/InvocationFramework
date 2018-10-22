package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum TwelvemonkeysImageioMavenIds implements MavenIdContains, ToFileRef2, EnumNameProvider {

    batik,
    bmp,
    clippath,
    core,
    hdr,
    icns,
    iff,
    jpeg,
    metadata,
    pcx,
    pdf,
    pict,
    pnm,
    psd,
    reference,
    sgi,
    tga,
    thumbsdb,
    tiff,
;

    MavenId m;

    TwelvemonkeysImageioMavenIds() {
        String artifactId = 'imageio-'+name()
        m = new MavenId('com.twelvemonkeys.imageio', artifactId, '3.4.2');
    }

    public static List<TwelvemonkeysImageioMavenIds> all = values().toList()

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }

    @Override
    String getCustomName() {
        return m.artifactId;
    }


}
