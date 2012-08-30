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
 * @deprecated see http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction+for+CIShell+Utilities
 */
@Deprecated
public class ColorRegistry<K> {
	private int currentIndex;
	private ColorSchema colorSchema;
	private Map<K, Color> registedColors;
	private boolean recycleColor;

	public ColorRegistry(ColorSchema colorSchema) {
		this(colorSchema, true);
	}

	public ColorRegistry(ColorSchema colorSchema, boolean recycleColor) {
		this.currentIndex = 0;
		this.recycleColor = recycleColor;
		this.colorSchema = colorSchema;
		this.registedColors = new HashMap<K, Color>();
	}
	
	/** 
	 * Get all the keys that hold the specified color.
	 * @return Return the registered keys
	 */
	public Set<K> getKeySet() {
		return this.registedColors.keySet();
	}

	/**
	 * Request a color for the specific key.
	 * @param key - key must be type of <E>
	 * @return Return color that assigned to the specific 
	 * key. If all the colors are fully used, the default
	 * color defined by the ColorSchema will be returned
	 */
	public Color getColorOf(K key) {
		if (this.registedColors.containsKey(key)) {
			return this.registedColors.get(key);
		}
		return reserveColorFor(key);
	}

	/**
	 * Request the default color as defined by the ColorSchema.
	 * @return the default color.
	 */
	public Color getDefaultColor() {
		return this.colorSchema.getDefaultColor();
	}

	/**
	 * Clear all entry and reset to initial state.
	 */
	public void clear() {
		this.registedColors.clear();
	}

	/*
	 * Request a color from the color schema
	 */
	private Color reserveColorFor(K key) {

		Color color = this.colorSchema.get(getNextIndex());
		this.registedColors.put(key, color);

		return color;
	}

	/*
	 * Return next color index. This will recycle the color
	 * if the recycleColor is true
	 */
	private int getNextIndex() {
		int index = this.currentIndex;

		if (this.currentIndex < this.colorSchema.size() - 1
				|| !this.recycleColor) {
			this.currentIndex++;
		} else {
			this.currentIndex = 0;
		}
		return index;
	}
}
