package idea.plugins.thirdparty.filecompletion.jrr.a.actions.reloadclass

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.ReloadClassActionImpl;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.jmx.MbeanConnectionCreatorCache
import net.sf.jremoterun.utilities.nonjdk.redefineclass.RedefineClassI
import net.sf.jremoterun.utilities.nonjdk.redefineclass.RedefineClassImpl
import net.sf.jremoterun.utilities.nonjdk.swing.JPanel4FlowLayout
import net.sf.jremoterun.utilities.nonjdk.swing.NameAndTextField

import javax.swing.AbstractButton
import javax.swing.ButtonGroup
import javax.swing.ButtonModel
import javax.swing.JPanel
import javax.swing.JRadioButton
import java.awt.event.ActionEvent
import java.awt.event.ActionListener;
import java.util.logging.Logger;

@CompileStatic
class ReloadClassConnectionPanel implements ReloadClassSettingsI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String ideaSelfButtonName = 'Idea self'
    public static String hostAndPortButtonName = 'Remote jmx'

    public JPanel panel = new JPanel4FlowLayout();
    public ButtonGroup btns = new ButtonGroup();
    public JRadioButton ideaButton = new JRadioButton(ideaSelfButtonName)
    public JRadioButton hostAndPortButton = new JRadioButton(hostAndPortButtonName)
    public NameAndTextField hostAndPortTextField = new NameAndTextField('Host&port', ' 127.0.0.1 : ', 70)
    public NameAndTextField classLoaderId = new NameAndTextField('Classloader id', ' ', 20)

    public Map<String, Object> map = [:];
    public ActionListener al = new ActionListener() {
        @Override
        void actionPerformed(ActionEvent e) {
            JRadioButton source = e.getSource() as JRadioButton;
            onActionListener(source)
        }
    }

    ReloadClassConnectionPanel() {
        init()
    }

    static  ReloadClassConnectionPanel createAndSet(){
        ReloadClassConnectionPanel panel3 = new ReloadClassConnectionPanel();
        panel3.createFooter()
        ReloadClassActionImpl.reloadClassSettings = panel3;
        return panel3
    }

    void onActionListener(JRadioButton source) {
        String text = source.getText();
        boolean enableT = text == hostAndPortButtonName
        hostAndPortTextField.setEnabled(enableT);
    }

    void init() {
        ideaButton = addToMap(ideaSelfButtonName, 'some')
        ideaButton.setSelected(true);
    }

    void createFooter(){
        hostAndPortButton = addToMap(hostAndPortButtonName, 'some')
        panel.add(hostAndPortButton)
        panel.add(hostAndPortTextField)
        panel.add(classLoaderId)
        hostAndPortTextField.setEnabled(false)
    }

    void addJmxLocalhostConnections(List<JmxLocalhostConnections> connections) {
        connections.each {
            addToMap(it.name(), it)
        }
    }

    JRadioButton addToMap(String name, Object handle) {
        JRadioButton b = new JRadioButton(name)
        Object put = map.put(name, handle);
        if (put != null) {
            throw new Exception("already contains ${name} before : ${put}, new : ${handle}")
        }
        b.addActionListener(al)
        btns.add(b);
        panel.add(b)
        return b;
    }

    @Override
    RedefineClassI receiveConnection() {
        JRadioButton selectedButton = findSelectedButton();
        String text = selectedButton.getText();
        RedefineClassI connectionDefault = receiveConnectionDefault(text)
        if (connectionDefault == null) {
            throw new Exception("failed map : ${text}")
        }
        return connectionDefault
    }

    @Override
    String getClassLoaderId() {
        String s = classLoaderId.getText()
        if (s == null) {
            return RedefineClassI.thisClassCl
        }
        s = s.trim();
        if (s.length() == 0) {
            return RedefineClassI.thisClassCl
        }
        return s;
    }


    RedefineClassI receiveConnectionDefault(String text) {
        if (text == ideaSelfButtonName) {
            return RedefineClassImpl.defaultInstance;
        }
        if (text == hostAndPortButtonName) {
            String text1 = hostAndPortTextField.getText();
            List<String> tokenize = text1.tokenize(':')
            String host = tokenize.get(0).trim()
            int port = Integer.parseInt(tokenize.get(1).trim())
            return MbeanConnectionCreatorCache.getClient(RedefineClassI, host, port, RedefineClassI.objectName)
        }
        Object handle = map.get(text)
        if (handle instanceof JmxLocalhostConnections) {
            JmxLocalhostConnections localhostConnection = (JmxLocalhostConnections) handle;
            return MbeanConnectionCreatorCache.getClient(RedefineClassI, '127.0.0.1', localhostConnection.port, RedefineClassI.objectName)
        }
        return null;
    }

    JRadioButton findSelectedButton() {
        List<AbstractButton> buttons = btns.getElements().toList()
        List<JRadioButton> buttons2 = buttons as List<JRadioButton>;
        JRadioButton selectedButton = buttons2.find { it.isSelected() }
        if (selectedButton == null) {
            throw new Exception("No button selected")
        }
        return selectedButton;
    }

}
