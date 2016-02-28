package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@Canonical
@CompileStatic
class SvnRef extends SvnSpec{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String pathInRepo;

    SvnRef(String repo, String pathInRepo) {
        this.repo = repo
        this.pathInRepo = pathInRepo
    }

}
