package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.rstacore.RstaLangSupportStatic
import net.sf.jremoterun.utilities.nonjdk.swing.ClipboardUtils

import javax.swing.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.logging.Logger

@CompileStatic
class StackTraceTextArea {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JTextArea textArea = new JTextArea()

    JScrollPane scrollPane

    StackTraceTextArea() {
        scrollPane = new JScrollPane(textArea)
        textArea.setEditable(false)
        textArea.setLineWrap(true)
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    shopPopupMenu(e)
                }
            }
        })
    }

    void shopPopupMenu(MouseEvent e) {
        JPopupMenu popupMenu = new JPopupMenu()
        addMenuItems(popupMenu)
        popupMenu.show(e.getComponent(), e.x, e.y);
    }

    void addMenuItems(JPopupMenu popupMenu) {
        if (RstaLangSupportStatic.langSupport.osInegrationClient != null) {
            JMenuItem menuItem = new JMenuItem("Show stack trace in IDE")
            popupMenu.add(menuItem)
            menuItem.addActionListener {
                RstaLangSupportStatic.langSupport.osInegrationClient.showStackTrace(textArea.text);
            }
        }

        JMenuItem copyMenuItem = new JMenuItem("Copy stack trace in IDE")
        popupMenu.add(copyMenuItem)
        copyMenuItem.addActionListener {
            ClipboardUtils.setClipboardContent(textArea.text)
        }
    }

}
