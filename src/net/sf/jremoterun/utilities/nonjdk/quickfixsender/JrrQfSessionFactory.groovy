package net.sf.jremoterun.utilities.nonjdk.quickfixsender

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import quickfix.Application
import quickfix.ConfigError
import quickfix.DefaultSessionFactory
import quickfix.LogFactory
import quickfix.MessageFactory
import quickfix.MessageStoreFactory
import quickfix.Session
import quickfix.SessionID
import quickfix.SessionSettings;

import java.util.logging.Logger;

@CompileStatic
class JrrQfSessionFactory extends DefaultSessionFactory{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrQfHelper jrrQfHelper

    JrrQfSessionFactory(Application application, MessageStoreFactory messageStoreFactory, LogFactory logFactory, MessageFactory messageFactory, JrrQfHelper jrrQfHelper ) {
        super(application, messageStoreFactory, logFactory, messageFactory)
        this.jrrQfHelper = jrrQfHelper;
    }

    @Override
    Session create(SessionID sessionID, SessionSettings settings) throws ConfigError {
        Session session=jrrQfHelper.createSession(sessionID, settings)
        return session
    }

    Session createSessionSuper(SessionID sessionID, SessionSettings settings) throws ConfigError {
        return super.create(sessionID, settings)
    }
}
