package org.cishell.utilities;

import com.google.common.base.Function;

public enum ToCaseFunction implements Function<String, String> {
	LOWER {
		public String apply(String from) {
			return from.toLowerCase();
		}
	},
	UPPER {
		public String apply(String from) {
			return from.toUpperCase();
		}
	};
}