package org.cishell.reference.prefs.admin;

import java.util.Comparator;

import org.osgi.service.metatype.ObjectClassDefinition;

public class PrefPageComparator implements Comparator {
		public int compare (Object o1, Object o2) {
			if (! (o1 instanceof PrefPage) || ! (o2 instanceof PrefPage)) {
				throw new ClassCastException("Cannot compare two objects that are not both PrefPages.");
			}
			PrefPage pp1 = (PrefPage) o1;
			ObjectClassDefinition ocd1 = pp1.getPrefOCD(); 
			if (ocd1 == null) {
				return -1;
			}
			String ocd1Name = ocd1.getName();
			
			PrefPage pp2 = (PrefPage) o2;
			ObjectClassDefinition ocd2 = pp2.getPrefOCD(); 
			if (ocd2 == null) {
				return 1;
			}
			String ocd2Name = ocd2.getName();
			
			return ocd1Name.compareTo(ocd2Name);
		}
}
