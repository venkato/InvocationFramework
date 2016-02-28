package nik.eclipse.jrr.memberproposals;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jdt.core.IMethod;

public class IMethodComparator2 implements Comparator<IMethod> {


	@Override
	public int compare(IMethod o1, IMethod o2) {
		int res = o1.getElementName().toLowerCase().compareTo(o2.getElementName().toLowerCase());
		if (res == 0) {
			res = Arrays.toString(o1.getParameterTypes()).compareTo(Arrays.toString(o1.getParameterTypes()));
		}
		return res;
	}


}
