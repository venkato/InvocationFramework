package net.sf.jremoterun.utilities.nonjdk.sfdownloader

import net.sf.jremoterun.utilities.classpath.ToFileRef2


interface UrlProvided  extends ToFileRef2{

    URL convertToUrl();
}