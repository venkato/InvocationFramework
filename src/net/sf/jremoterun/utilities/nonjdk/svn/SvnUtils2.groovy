package net.sf.jremoterun.utilities.nonjdk.svn

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec
import net.sf.jremoterun.utilities.nonjdk.git.UrlSymbolsReplacer
import org.apache.commons.io.FileUtils
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager
import org.tmatesoft.svn.core.io.SVNRepository
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc2.SvnCheckout
import org.tmatesoft.svn.core.wc2.SvnOperationFactory
import org.tmatesoft.svn.core.wc2.SvnTarget

import java.util.logging.Logger

@CompileStatic
class SvnUtils2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    void checkoutSvnRefImpl(String svnRef, File workingCopyDirectory) {
        SVNURL svnurl = SVNURL.parseURIDecoded(svnRef)
        SVNRepository svnRepo = SVNRepositoryFactory.create(svnurl);
        svnRepo.authenticationManager = getAuth()
        configureRepo(svnRepo)
        long remoteRevision = svnRepo.latestRevision
        log.info "${remoteRevision}"
        final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        svnOperationFactory.authenticationManager = getAuth()
        try {
            final SvnCheckout checkout = svnOperationFactory.createCheckout();
            checkout.setSource(SvnTarget.fromURL(svnurl));
            checkout.setSingleTarget(SvnTarget.fromFile(workingCopyDirectory));
            long run3 = checkout.run();
            log.info "${run3}"
            log.info "${checkout}"
            log.info "checkout done"
        } finally {
            svnOperationFactory.dispose();
        }
    }

    ISVNAuthenticationManager getAuth(){
        return null
    }


    void configureRepo(SVNRepository svnRepo){
        // set here:
        // svn user, password
        // proxy : BasicAuthenticationManager.setProxy
        // https://svn.svnkit.com/repos/svnkit/branches/ssh.ping/www/kb/config-settings.html
        // org.tmatesoft.svn.core.internal.wc.SVNConfigFile.createDefaultConfiguration
    }


}
