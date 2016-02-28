package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import net.sf.jremoterun.utilities.classpath.ToFileRef2


@Deprecated
interface ToFileRef extends ToFileRef2{

    File resolveToFile();

}