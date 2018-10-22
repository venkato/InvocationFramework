package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.Canonical;
import groovy.transform.CompileStatic;

@CompileStatic
@Canonical
public class ThreeResult<T, E, V>  extends TwoResult<T,E>{

	private static final long serialVersionUID = 8479268465726426364L;


	public final V third;

	public ThreeResult(final T t, final E e, final V v) {
		super(t,e)
		this.third = v;
	}

	V getThird() {
		return third
	}


	@Override
	String toString() {
		return "${first} , ${second} , ${third}"
	}
}
