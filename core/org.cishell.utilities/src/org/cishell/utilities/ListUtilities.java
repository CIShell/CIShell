package org.cishell.utilities;

import java.util.ArrayList;
import java.util.List;
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class ListUtilities {
	public static<T> List<T> createAndFillList(T... contents) {
		return fillList(new ArrayList<T>(), contents);
	}

	public static<T> List<T> fillList(List<T> list, T... contents) {
		for (T content : contents) {
			list.add(content);
		}

		return list;
	}
}