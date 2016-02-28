package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic


@CompileStatic
interface GitBinaryAndSourceRefRef extends GitRefRef{

    GitBinaryAndSourceRef getRef();

}