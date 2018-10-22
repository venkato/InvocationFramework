package net.sf.jremoterun.utilities.nonjdk.ivy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface OnRepoCreatedListener {

    void onRepoCreated(IvyDepResolver3 resolver2);


}
