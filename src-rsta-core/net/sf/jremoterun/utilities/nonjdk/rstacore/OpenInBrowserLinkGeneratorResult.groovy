package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult

import javax.swing.event.HyperlinkEvent;
import java.util.logging.Logger;

@CompileStatic
abstract class OpenInBrowserLinkGeneratorResult implements LinkGeneratorResult{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    int sourceOffset
    String url;

    OpenInBrowserLinkGeneratorResult(int sourceOffset, String url) {
        this.sourceOffset = sourceOffset
        this.url = url
    }

    @Override
    int getSourceOffset() {
        return sourceOffset
    }
}
