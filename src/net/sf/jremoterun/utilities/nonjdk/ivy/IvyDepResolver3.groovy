package net.sf.jremoterun.utilities.nonjdk.ivy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import org.apache.ivy.plugins.resolver.IBiblioResolver;

import java.util.logging.Logger;

@CompileStatic
class IvyDepResolver3 extends IvyDepResolver2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    IBiblioRepository rrr;

    IvyDepResolver3(IBiblioRepository rrr) {
        this.rrr = rrr
    }

    @Override
    IBiblioResolver buildPublicIbiblio() {
        IBiblioResolver iBiblioResolver3 = buildPublicIbiblioCustom(rrr.name(), rrr.url)
        failBackDr = iBiblioResolver3
        return iBiblioResolver3
        return super.buildPublicIbiblio()
    }

    @Override
    String toString() {
        return rrr.toString()
    }
}
