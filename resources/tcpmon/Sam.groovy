import groovy.transform.CompileStatic;
import net.infonode.docking.TabWindow;
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaScriptHelper;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.AdminPage2;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.Listener;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.SlowLinkSimulator;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

@CompileStatic
class Sam extends RstaScriptHelper {

    void r() {
        AdminPage2 adminPage2 = (AdminPage2) runner;
        TabWindow tw = (TabWindow) adminPage2.defaultView.getWindowParent();
        String tHost = "b";
        int lPort = 22;
        int tPort = 22;
        boolean isProxy = false;
        SlowLinkSimulator slowLink;
        //slowLink = new SlowLinkSimulator(bytes, time);
        Listener l = new Listener(tw, null, lPort, tHost,
                tPort, isProxy, slowLink,
                false, null,
                SyntaxConstants.SYNTAX_STYLE_NONE,
                SyntaxConstants.SYNTAX_STYLE_NONE, true);
        //configProxy(l);

    }

    void configProxy(Listener l) {
        l.HTTPProxyHost = "127.0.0.1";
        l.HTTPProxyPort = 8080;
    }


}
