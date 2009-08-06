package org.cishell.templates.wizards.utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// I'm awesome like that...
public class MultiHashMapWithCounts extends HashMap {
	private HashMap counts = new HashMap();
	
	public Object put(Object key, Object value) {
		if (containsKey(key)) {
			Set targetSet = (Set)get(key);
			targetSet.add(value);
			
			Integer oldCount = (Integer)this.counts.get(key);
			this.counts.put(key, new Integer(oldCount.intValue() + 1));
			
			return targetSet;
		} else {
			Set targetSet = new HashSet();
			targetSet.add(value);
			super.put(key, targetSet);
			
			this.counts.put(key, new Integer(0));
			
			return targetSet;
		}
	}
	
	public void removeValue(Object key, Object value) {
		if (containsKey(key)) {
			Set targetSet = (Set)get(key);
			targetSet.remove(value);
		}
	}
	
	public int getCount(Object key) {
		if (containsKey(key)) {
			Integer count = (Integer)this.counts.get(key);
			
			return count.intValue();
		} else {
			return 0;
		}
	}
}