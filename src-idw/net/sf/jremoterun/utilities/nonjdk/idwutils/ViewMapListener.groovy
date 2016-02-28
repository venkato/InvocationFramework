package net.sf.jremoterun.utilities.nonjdk.idwutils

import net.infonode.docking.View
import net.infonode.docking.util.ViewMap;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class ViewMapListener extends ViewMap{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    protected void addView(Object id, View view) {
        super.addView(id, view)
        log.info "adding view : ${id} ${view}"
    }

    @Override
    void addView(int id, View view) {
        super.addView(id, view)
        log.info "adding view : ${id} ${view}"
    }
}
