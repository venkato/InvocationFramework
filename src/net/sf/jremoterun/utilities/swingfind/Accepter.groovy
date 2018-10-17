package net.sf.jremoterun.utilities.swingfind;

import groovy.transform.CompileStatic;

import java.awt.Component;

@CompileStatic
public interface  Accepter {
	
	public boolean accept(Component component);
	

}
