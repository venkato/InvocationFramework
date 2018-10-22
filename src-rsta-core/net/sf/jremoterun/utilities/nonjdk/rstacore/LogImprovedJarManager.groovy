package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.fife.rsta.ac.java.JarManager;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;

import java.util.Set;
import java.util.logging.Logger;

@CompileStatic
public class LogImprovedJarManager extends JarManager {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    @Override
    public void addCompletions(CompletionProvider p, String text, Set<Completion> addTo) {
        if (text.length() > 0 && text.length() < 3) {
            log.fine("text too short : " + text);
        } else {
            super.addCompletions(p, text, addTo);
        }
    }

}
