package net.sf.jremoterun.utilities.nonjdk.classpath.refs;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
enum SquirrelSqlMavenIds  implements MavenIdContains {



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


}
