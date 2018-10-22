package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.title.DockingWindowTitleProvider;
import org.apache.log4j.Logger;

@CompileStatic
public class MyDockingWindowTitleProvider implements DockingWindowTitleProvider {
	private static final Logger logger = Logger
			.getLogger(MyDockingWindowTitleProvider.class);

	private String title;
	
	
	
	public MyDockingWindowTitleProvider(String title) {
		this.title = title;
	}

	@Override
	public String getTitle(DockingWindow dockingwindow) {
		return title;
	}
}
