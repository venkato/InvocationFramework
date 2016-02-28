package net.sf.jremoterun.utilities.nonjdk.swing

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel
import java.util.logging.Logger;


@CompileStatic
public class JPanel4FlowLayout extends JPanel {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	private double wantedHeight = -1;

	public JPanel4FlowLayout() {
		super(new FlowLayout(FlowLayout.LEADING));
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension viewPrefSize1 = super.getPreferredSize();
		final StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
		// log.info(stackTraceElements[2]);
//		if (stackTraceElements[1].getClassName().equals(BorderLayout.class.getName())) {
			// log.info(1);
			final Dimension viewPrefSize = new Dimension(viewPrefSize1);
			final Container scrollPane = getParent().getParent();
			viewPrefSize.@width = scrollPane.getWidth();
			if (viewPrefSize.width != 0) {
				wantedHeight = viewPrefSize1.getHeight() * viewPrefSize1.getWidth() / viewPrefSize.getWidth();
				viewPrefSize.@height = (int) (wantedHeight * 1.5);
				// log.info(viewPrefSize);
				return viewPrefSize;
			}
			wantedHeight = -1;

//		}
		// log.info(scrollPane.getWidth());
		return viewPrefSize1;
	}

	public double getWantedHeight() {
		return wantedHeight;
	}
}
