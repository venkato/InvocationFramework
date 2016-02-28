package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.openapi.util.TextRange
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionBean
import idea.plugins.thirdparty.filecompletion.share.Ideasettings.MoveFileDialogSettings
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.nonjdk.swing.NameAndTextField

import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.FlowLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.util.logging.Logger

@CompileStatic
class MoveFileDialog {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static  MoveFileDialog latestMoveFileDialog


    File file
    FileCompletionBean place;
    JDialog dialog
//    JPanelBorderLayout panel = new JPanelBorderLayout()
    JPanel panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT))
//    JPanel panelSub = new JPanel(new MigLayout("","[grow]","[center]"))
    NameAndTextField from
    NameAndTextField toSubDir
    NameAndTextField packageName;
    NameAndTextField className2;
    NameAndTextField fieldName2;
    File fileDocument

    JButton moveButton = new JButton("move")

    MoveFileDialog(File file, FileCompletionBean place) {
        this.file = file
        this.place = place
        init()
    }


    void init() {
        JrrLibMoveFileBean.bean.file = file
        JrrLibMoveFileBean.bean.wholeFileDeclaration = place.wholeFileDeclaration
        JrrLibMoveFileBean.bean.fileDocument = fileDocument
        from = new NameAndTextField("From",file.absolutePath)
        from.textField.editable = false

        packageName = new NameAndTextField("packageName", MoveFileDialogSettings.defaultPackageName,300)
        className2 = new NameAndTextField("className", MoveFileDialogSettings.defaultSimpleClassName,300)
        fieldName2 = new NameAndTextField("fieldName", MoveFileDialogSettings.defaultParentFileVar,300)
        toSubDir = new NameAndTextField("To",file.name,300)

        panelSub.add(from,"wrap")
        panelSub.add(packageName,"wrap")
        panelSub.add(className2,"wrap")
        panelSub.add(fieldName2,"wrap")
        panelSub.add(toSubDir,"wrap")
        moveButton.setMargin(new Insets(5,5,10,10))
        panelSub.add(moveButton,"wrap")
        moveButton.addActionListener(this.&moveFileDo)

        dialog = new JDialog((JFrame) null, "Move file", false)
        dialog.setContentPane(panelSub)
//        panel.add(panelSub,BorderLayout.CENTER)
        latestMoveFileDialog = this
    }

    void moveFileDo(ActionEvent e){
        try {
            log.info "move clicked"
            moveFileDoImpl()
        }catch (Throwable e2){
            JrrUtilities.showException("Failed move file",e2)
        }
    }

    MavenCommonUtils mcu = new MavenCommonUtils()

    void moveFileDoImpl(){
        String className3 = className2.getText().trim()
        String fieldName = fieldName2.getText().trim()
        File parentFile
        if(className3.length()!=0) {
            String className = "${packageName.getText()}.${className3}"
            Class<?> clazz = JrrClassUtils.currentClassLoader.loadClass(className)
            parentFile= JrrClassUtils.getFieldValue(clazz, fieldName) as File
        }
        String text = toSubDir.getText().replace('\\','/');
        File destFile
        if(parentFile==null) {
            destFile = text as File
        }else{
            assert parentFile.exists()
            destFile = new File(parentFile, text)
        }
        File parentFile1 = destFile.parentFile
        parentFile1.mkdirs()
        assert parentFile1.exists()
        String newText
        String import2
        if(className3.length()==0){
            newText = """ new File ( "${text}" ) """
        }else{
            import2 = "${packageName.getText()}.${className3}"
            newText = """ new File ( ${className3}.${fieldName} , "${text}" ) """
        }
        if(mcu.isParent(destFile,file)){
            throw new Exception("dest and source file are nested ${destFile} ${file}")
        }
        if(mcu.isParent(file,destFile)){
            throw new Exception("dest and source file are nested ${destFile} ${file}")
        }
        String documentText = fileDocument.text.replace('\r\n','\n')

        String text1 = place.wholeFileDeclaration.text
        TextRange textRange = place.wholeFileDeclaration.textRange
        int textLength =textRange.endOffset -textRange.startOffset
        int offset7= documentText.indexOf(text1)
        if(offset7==-1){
            throw new Exception("failed find text : ${text1}")
        }
        String begin = documentText.substring(0,offset7)
        String end = documentText.substring(offset7+textLength)



        log.info "begin 3 : ${begin.substring(begin.length()-10)}"
        log.info "end : ${end.substring(0,5)}"
        log.info "new text : ${newText}"
        String newText4 = begin+newText+end

        int importBegin = newText4.indexOf("import ")
        if(importBegin==-1){
            throw new Exception("failed find import : add if missed")
        }
        if(import2!=null){
            String beforeImport = newText4.substring(0,importBegin)
            String afterImport = newText4.substring(importBegin)
            newText4 = "${beforeImport} import ${import2};\n ${afterImport}"
        }
        if(true) {
            boolean renamedFileFine = file.renameTo(destFile)
            log.info "rename to ${destFile} from ${file} fine : ${renamedFileFine}"
            if (!renamedFileFine) {
                throw new Exception("failed rename to ${destFile} from ${file}")
            }
            log.info "${newText4}"
            fileDocument.text = newText4
            dialog.dispose()
        }
    }


}
