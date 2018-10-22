package net.sf.jremoterun.utilities.nonjdk.idwutils.alerttable

import groovy.transform.CompileStatic
import net.infonode.docking.SplitWindow
import net.infonode.docking.View
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.OsInegrationClientI
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.TextAreaAndView
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace
import net.sf.jremoterun.utilities.nonjdk.swing.JrrSwingUtils

import javax.swing.*
import javax.swing.table.DefaultTableModel
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class AlertTable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public TextAreaAndView detailsView = new TextAreaAndView('Details')
    public DefaultTableModel defaultTableModel = new DefaultTableModel()
    public JTable tableSummary = new JTable(defaultTableModel)

    public Vector columnNames = new Vector(AlertTableColumns.values().toList().collect { it.name() })
    JScrollPane tableMsgsScrollPane = new JScrollPane(tableSummary)
    View tableMsgsView = new View('Messages', null, tableMsgsScrollPane);

    public SplitWindow splitWindow = new SplitWindow(true, tableMsgsView, detailsView.view);

    public SimpleDateFormat sdf = new SimpleDateFormat('dd HH:mm')
    public List<String> exceptionList = []
    public int currentRowShowing = -1;
    public JButton showExceptionButton = new JButton('Show exception in IDE')
    public OsInegrationClientI inegrationClient;

    public static AlertTable defaultAlertTable = new AlertTable();
    public static Runnable afterAlertAdded;

    AlertTable() {
        defaultTableModel.setColumnIdentifiers(columnNames);
        tableSummary.addMouseListener(new MouseAdapter() {
            @Override
            void mouseClicked(MouseEvent e) {
                try {
                    showMsg(tableSummary.getSelectedRow())
                } catch (Throwable e2) {
                    log.info("failed show selected msg", e2)
                    JrrUtilities.showException("failed show selected msg", e2)
                }
            }
        });
        showExceptionButton.addActionListener {
            try {
                String ee = exceptionList.get(currentRowShowing)
                inegrationClient.showStackTrace(ee)
            } catch (Throwable e2) {
                log.info("failed show exception", e2)
                JrrUtilities.showException("failed show exception", e2)
            }
        }
        detailsView.panel.add(showExceptionButton, BorderLayout.NORTH)
        IdwUtils.setTitle(splitWindow, 'Alert table')
        detailsView.textArea.setLineWrap(true);
        detailsView.textArea.setWrapStyleWord(true);
        detailsView.textArea.setEditable(false);
    }


    protected void showMsg(int activeColumn) {
        currentRowShowing = activeColumn;
        Vector row = defaultTableModel.getDataVector().get(activeColumn)
        int i = columnNames.indexOf(AlertTableColumns.message.name())
        String msg = row.get(i);
        detailsView.textArea.setText(msg)
    }

    static void addAlertS(String msg) {
        addAlertS(msg, null)
    }

    static void addAlertS(String msg, Throwable e) {
        if (e == null) {
            e = new JustStackTrace()
        }
//        log.info(msg, e)
        SwingUtilities.invokeLater {
            try {
                defaultAlertTable.addAlert(msg, e);
                SwingUtilities.invokeLater {
                    try {
                        defaultAlertTable.splitWindow.makeVisible()
                        IdwUtils.setVisible(defaultAlertTable.splitWindow);
                        if (afterAlertAdded != null) {
                            afterAlertAdded.run()
                        }
                    } catch (Throwable e2) {
                        log.info("failed show selected msg", e2)
                        JrrUtilities.showException("failed show selected msg", e2)
                    }
                }
            } catch (Throwable e2) {
                log.info("failed show selected msg", e2)
                JrrUtilities.showException("failed show selected msg", e2)
            }
        }
    }

    void addAlert(String msg, Throwable e) {
        String excS = JrrUtils.exceptionToString(e)
        Vector row = new Vector()
        row.add(sdf.format(new Date()));
        msg += " \n ${excS}"
        row.add(msg);
        defaultTableModel.addRow(row)
        exceptionList.add(excS)
        detailsView.textArea.setText(msg)
        currentRowShowing=exceptionList.size()-1
    }


}
