package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic;


@CompileStatic
enum KeyType {

    RSA,
    DSA,
    // works  ??
    ECDSA,
    ;
}