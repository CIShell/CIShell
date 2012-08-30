package org.cishell.utilities;

import com.google.common.base.Function;
/**
 * @deprecated see
 *             http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities
 */
@Deprecated
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