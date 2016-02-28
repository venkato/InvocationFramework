package net.sf.jremoterun.utilities.nonjdk.timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyInterruptedException1 extends InterruptedException {

	private static final Logger log = LogManager.getLogger();

	public MyInterruptedException1(final InterruptedException interruptedException) {
		super(interruptedException.getMessage());
		setStackTrace(interruptedException.getStackTrace());
	}
}
