package net.sf.jremoterun.utilities.nonjdk.idwutils;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.title.DockingWindowTitleProvider;
import org.apache.log4j.Logger;

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
