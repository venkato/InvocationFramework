package nik.eclipse.jrr.memberproposals;

import java.util.Comparator;

import org.eclipse.jdt.core.IMember;

public class ImemberComparator implements Comparator<IMember> {

	@Override
	public int compare(IMember o1, IMember o2) {
		return o1.getElementName().toLowerCase().compareTo(o2.getElementName().toLowerCase());
	}

}
