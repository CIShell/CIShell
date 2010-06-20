package org.cishell.utilities;

import java.util.ArrayList;
import java.util.List;

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