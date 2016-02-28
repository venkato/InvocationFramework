package net.sf.jremoterun.utilities.swingfind;

import java.awt.Component;

import groovy.lang.Closure;
import groovy.transform.CompileStatic;

@CompileStatic
public class AccepterProxy implements Accepter{

	Closure<Boolean> accepter;

	public AccepterProxy(Closure<Boolean> accepter) {
		assert accepter.parameterTypes.length ==1
		assert accepter.parameterTypes[0] == Component
		this.accepter = accepter;
	}

	public boolean accept(Component component) {
		return accepter(component);
	}
}
