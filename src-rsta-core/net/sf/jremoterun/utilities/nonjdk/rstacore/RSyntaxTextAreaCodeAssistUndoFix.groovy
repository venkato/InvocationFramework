package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.fife.rsta.ac.LanguageSupportFactory
import org.fife.rsta.ac.java.JavaLanguageSupport
import org.fife.rsta.ac.java.custom.RSyntaxTextAreaCodeAssist
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane;

import java.util.logging.Logger;

@CompileStatic
abstract class RSyntaxTextAreaCodeAssistUndoFix extends RSyntaxTextAreaCodeAssistWithCustMenu {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    protected long editsTry = 0;
    private RSyntaxTextAreaCodeAssist textArea = this;

    RTextScrollPane scrollPane;

    @Override
    public void setText(String text) {
        super.setText(text);
        textArea.setHyperlinksEnabled(true);
        scrollPane.setLineNumbersEnabled(true);
        textArea.setHighlightCurrentLine(false);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.select(0, 0);
        if (!isEditable()) {
            editsTry++;
            if (editsTry > 5) {
                // discarding for avoiding memory leak
                textArea.discardAllEdits();
                editsTry = 0;
            }
        }
    }

    @Deprecated
    @Override
    String getText() {
        return getTextNormalized()
    }

    public String getTextNormalized() {
        String text = super.getText();
        text = nornalizeText(text);
        return text;
    }

    static String nornalizeText(String text){
        return text.replace("\r\n", "\n").replace("\r", "\n");
    }

    void addLangSupport() throws Exception {
        RstaLangSupportStatic langSupport = RstaLangSupportStatic.langSupport;
        langSupport.init();
//        RSyntaxTextArea textArea = this.getTextArea();
        JavaLanguageSupport groovyLanguageSupport;
        groovyLanguageSupport = (JavaLanguageSupport) LanguageSupportFactory.get()
                .getSupportFor(SyntaxConstants.SYNTAX_STYLE_JAVA);
        groovyLanguageSupport.setJarManager langSupport.addFileSourceToRsta.jarManager
        textArea.groovyLanguageSupport = groovyLanguageSupport
        groovyLanguageSupport.install(textArea);

//        JrrClassUtils.setFieldValue(langSupport.groovyLanguageSupport, "jarManager",
//                langSupport.addFileSourceToRsta.jarManager);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
        textArea.addSupport();
//        textArea.groovyLanguageSupport = langSupport.groovyLanguageSupport;
        if(langSupport.osInegrationClient!=null) {
            textArea.addExternalMemberClickedListener(
                    new RstaOpenMember(langSupport.osInegrationClient));
        }
    }


}
