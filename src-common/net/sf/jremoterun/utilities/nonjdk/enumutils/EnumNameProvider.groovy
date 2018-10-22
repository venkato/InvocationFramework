package net.sf.jremoterun.utilities.nonjdk.enumutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface EnumNameProvider {

    String getCustomName();


}
