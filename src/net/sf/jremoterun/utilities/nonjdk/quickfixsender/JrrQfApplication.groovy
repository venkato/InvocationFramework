package net.sf.jremoterun.utilities.nonjdk.quickfixsender

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import quickfix.Application
import quickfix.DoNotSend
import quickfix.FieldNotFound
import quickfix.IncorrectDataFormat
import quickfix.IncorrectTagValue
import quickfix.Message
import quickfix.RejectLogon
import quickfix.SessionID
import quickfix.UnsupportedMessageType

import java.awt.Color;
import java.util.logging.Logger;

@CompileStatic
class JrrQfApplication implements Application {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    QfRstaRunnerWithStackTrace runner;

    JrrQfApplication(QfRstaRunnerWithStackTrace runner) {
        this.runner = runner
    }

    @Override
    void onCreate(SessionID sessionId) {

    }

    @Override
    void onLogon(SessionID sessionId) {
        runner.statusField.setText("Logon")
        runner.statusField.setForeground(Color.GREEN)
    }

    @Override
    void onLogout(SessionID sessionId) {
        runner.statusField.setText("Logout")
        runner.statusField.setForeground(Color.RED)
    }

    @Override
    void toAdmin(Message message, SessionID sessionId) {
        runner.addMessageToView(message, true)
    }

    @Override
    void toApp(Message message, SessionID sessionId) throws DoNotSend {
        runner.addMessageToView(message, true)
    }



    @Override
    void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        runner.addMessageToView(message, false)
    }

    @Override
    void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        runner.addMessageToView(message, false)
    }
}
