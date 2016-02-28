package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.*
import java.awt.*
import java.lang.reflect.Field
import java.util.List
import java.util.logging.Logger

@CompileStatic
public class SettingsChecker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile boolean showAsSwing = false;

    public static void check(Class class1) throws Exception {
        String location = "${class1.name}(${class1.simpleName}.groovy:5)"
        Field[] declaredFields = class1.getDeclaredFields();
        List<String> msgs  =[]
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object object = field.get(null);
            if (object instanceof File) {
                File new_name = (File) object;
                if (!new_name.exists()) {
    String msg ="${location} file not found ${field.name} ${new_name.absoluteFile}"
                    log.warning(msg)
                    msgs.add(msg)
                }
            }
        }
        if(msgs.size() > 0) {
            if (showAsSwing) {
                SwingUtilities.invokeLater {
                    showMsgInfo("File not found", msgs.join("\n"));
                }
            } else {
                new Exception("${msgs.join('\n')}").printStackTrace()
            }
        }
    }

    public static Dimension minimumSize = new Dimension(50, 50);


    static JFrame showMsgInfo(String title, String msg){
        JTextArea area = new JTextArea(msg)
        JScrollPane scrollPane = new JScrollPane(area)
        JFrame dialog = new JFrame(title)
        dialog.getContentPane().setLayout(new BorderLayout())
        dialog.getContentPane().add(scrollPane,BorderLayout.CENTER)
        dialog.getContentPane().setMinimumSize(new Dimension(minimumSize));
        dialog.pack()
        dialog.setVisible(true)
        return dialog;
    }
}
