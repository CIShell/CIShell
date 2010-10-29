package org.cishell.utilities.color;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ColorRegistry provide algorithm to assign color to 
 * specific item that defined in <E>. It use generic 
 * so that the implementation can be use for any type
 * of item.
 * 
 * To use ColorRegistry, create your own ColorSchema
 * that hold the set of available colors and also
 * a default color if the color is out
 * @author kongch
 *
 */
public class ColorRegistry<K> {
	private int currentIndex;
	private ColorSchema colorSchema;
	private Map<K, Color> registedColors;
	
	public ColorRegistry(ColorSchema colorSchema) {
		this.currentIndex = 0;
		this.colorSchema = colorSchema;
		this.registedColors = new HashMap<K, Color>();
	}
	
	/** 
	 * Get all the keys that hold the specified color.
	 * @return Return the registered keys
	 */
	public Set<K> getKeySet() {
		return registedColors.keySet();
	}
	
	/**
	 * Request a color for the specific key.
	 * @param key - key must be type of <E>
	 * @return Return color that assigned to the specific 
	 * key. If all the colors are fully used, the default
	 * color denied by the ColorSchema will be returned
	 */
	public Color getColorOf(K key) {
		if (registedColors.containsKey(key)) {
			return registedColors.get(key);
		} else {
			return reserveColorFor(key);
		}
	}
	
	/**
	 * Clear all entry and reset to initial state.
	 */
	public void clear() {
		registedColors.clear();
	}
	
	/*
	 * Request a color from the color schema
	 */
	private Color reserveColorFor(K key) {
		
		Color color = colorSchema.get(getNextIndex());
		registedColors.put(key, color);
		
		return color;
	}
	
	/*
	 * Return next color index. This will reuse the color if it out of color
	 */
	private int getNextIndex() {
		int index = currentIndex;
		
		if (currentIndex < colorSchema.size() - 1) {
			currentIndex++;
		} else {
			currentIndex = 0;
		}
		return index;
	}
}
