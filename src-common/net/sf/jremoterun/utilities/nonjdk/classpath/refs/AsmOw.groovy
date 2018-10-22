package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum AsmOw implements MavenIdContains, ToFileRef2, EnumNameProvider  {
    asm_util,
    asm_tree,
    asm_test,
    asm_commons,
    asm_analysis,
    asm,
//    asm_deprecated,
    ;


    MavenId m;

    AsmOw() {
        m = new MavenId('org.ow2.asm', name().replace('_','-'), '9.1');
    }

    public static List<AsmOw> all = values().toList()

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }

    @Override
    String getCustomName() {
        return m.artifactId;
    }

}
