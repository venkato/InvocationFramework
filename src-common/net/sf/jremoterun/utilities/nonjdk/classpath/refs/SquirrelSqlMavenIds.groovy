package net.sf.jremoterun.utilities.nonjdk.classpath.refs;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
enum SquirrelSqlMavenIds  implements MavenIdContains, EnumNameProvider, ToFileRef2 {



    fw,
    squirrel_sql,
    squirrelsql_launcher,
    ;


    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    MavenId m;

    SquirrelSqlMavenIds() {
        String artifactId = name().replace('_', '-')
        m = new MavenId("net.sf.squirrel-sql:${artifactId}:3.5.0");
    }

    public static List<? extends MavenIdContains> all = (List) values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }
}
