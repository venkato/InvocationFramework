package net.sf.jremoterun.utilities.nonjdk.idwutils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.Serializable;

public class FrameLocationInfo implements Serializable{



private static final Logger log = LogManager.getLogger();

	public Point location;
    public Dimension dimension;
}