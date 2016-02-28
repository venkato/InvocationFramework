package net.sf.jremoterun.utilities.nonjdk;

import groovy.transform.Canonical;
import groovy.transform.CompileStatic;

@CompileStatic
@Canonical
class TwoResult<T, E> implements Serializable{

	private static final long serialVersionUID = -6962559531081773986L;

	public final T first;

	public final E second;

	public TwoResult(final T t, final E e) {
		this.first = t;
		this.second = e;
	}

	T getFirst() {
		return first
	}

	E getSecond() {
		return second
	}
}
