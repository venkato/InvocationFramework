import java.util.*;
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaRunner;
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaScriptHelper;
import groovy.transform.CompileStatic;

@CompileStatic
class RstaRunner2 extends RstaScriptHelper {
	
	void r(){
		RstaRunner runner2 = runner;
		println "123";
		runner2.status = "${runner2.stopFlag}";
		Thread.sleep(10_000);
		runner2.status = "${runner2.stopFlag}";
		Thread t;
	}
}