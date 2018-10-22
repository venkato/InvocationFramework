package net.sf.jremoterun.utilities.nonjdk.svn

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec
import net.sf.jremoterun.utilities.nonjdk.git.UrlSymbolsReplacer
import org.apache.commons.io.FileUtils
import org.tmatesoft.svn.core.SVNProperties
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager
import org.tmatesoft.svn.core.io.SVNRepository
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc2.SvnCat
import org.tmatesoft.svn.core.wc2.SvnCheckout
import org.tmatesoft.svn.core.wc2.SvnExport
import org.tmatesoft.svn.core.wc2.SvnOperationFactory
import org.tmatesoft.svn.core.wc2.SvnTarget

import java.util.logging.Logger

@CompileStatic
class SvnUtils2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    int initBufferSize = 1_000_000;

    long checkoutSvnRefImpl(String svnRef, File workingCopyDirectory) {
//        SVNRepository svnRepo = SVNRepositoryFactory.create(svnurl);
//        svnRepo.authenticationManager = getAuth()
//        configureRepo(svnRepo)
//        long remoteRevision = svnRepo.latestRevision
//        log.info "${remoteRevision}"
        final SvnOperationFactory svnOperationFactory = createSvnOperationFactory()
        try {
            final SvnCheckout checkout = svnOperationFactory.createCheckout();
            checkout.setSource(createSvnTarget(svnRef));
            checkout.setSingleTarget(SvnTarget.fromFile(workingCopyDirectory));
            prepare(checkout)
            Long revision = checkout.run();
//            log.info "${long revision}"
//            log.info "${checkout}"
//            log.info "checkout done"
            return revision
        } finally {
            disposeOperation(svnOperationFactory);
        }
    }

    long exportSvnRefImpl(String svnRef, File workingCopyDirectory) {
//        SVNRepository svnRepo = SVNRepositoryFactory.create(svnurl);
//        svnRepo.authenticationManager = getAuth()
//        configureRepo(svnRepo)
//        long remoteRevision = svnRepo.latestRevision
//        log.info "${remoteRevision}"
        final SvnOperationFactory svnOperationFactory = createSvnOperationFactory()
        try {
            final SvnExport checkout = svnOperationFactory.createExport();
            checkout.setSource(createSvnTarget(svnRef));
            checkout.setSingleTarget(SvnTarget.fromFile(workingCopyDirectory));
            prepare(checkout)
            Long revision = checkout.run();
//            log.info "${revision}"
//            log.info "${checkout}"
//            log.info "checkout done"
            return revision
        } finally {
            disposeOperation(svnOperationFactory);
        }
    }



    byte[] receiveContentSvnRefImpl(String svnRef) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(initBufferSize);
        receiveContentSvnRefImpl(svnRef, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }


    SVNProperties receiveContentSvnRefImpl(String svnRef, OutputStream out) {
        // see impl in
        new ClRef('org.tmatesoft.svn.core.internal.wc2.remote.SvnRemoteCat')
        final SvnOperationFactory svnOperationFactory = createSvnOperationFactory()
        try {
            final SvnCat checkout = svnOperationFactory.createCat();
            checkout.setSingleTarget(createSvnTarget(svnRef));
            checkout.setOutput(out)
            prepare(checkout)
            SVNProperties sVNProperties = checkout.run();
            return sVNProperties
        } finally {
            disposeOperation(svnOperationFactory);
        }
    }


    SvnOperationFactory createSvnOperationFactory() {
        final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        svnOperationFactory.authenticationManager = getAuth()
        return svnOperationFactory;
    }

    void disposeOperation(SvnOperationFactory svnOperationFactory){
        svnOperationFactory.dispose()
    }

    void prepare(org.tmatesoft.svn.core.wc2.SvnOperation svnOperation){
    }

    SvnTarget createSvnTarget(String svnRef) {
        SVNURL svnurl = SVNURL.parseURIEncoded(svnRef);
        SvnTarget target = SvnTarget.fromURL(svnurl)
        return target;
    }


    ISVNAuthenticationManager getAuth() {
        return null
    }


    void configureRepo(SVNRepository svnRepo) {
        // set here:
        // svn user, password
        // proxy : BasicAuthenticationManager.setProxy
        // https://svn.svnkit.com/repos/svnkit/branches/ssh.ping/www/kb/config-settings.html
        // org.tmatesoft.svn.core.internal.wc.SVNConfigFile.createDefaultConfiguration
    }


}
