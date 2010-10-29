package org.cishell.utilities.color;

import java.awt.Color;

/**
 * ColorSchema defined a set of colors to be used for
 * an application. This allows the interchange of color 
 * schema set if needed.
 * 
 * Schema contains a set of available color and a
 * default color value. The defaultColor will be
 * set to BLACK if it is not given
 * @author kongch
 *
 */
public class ColorSchema {
	private int totalColors;
	private Color[] colors;
	private Color defaultColor;
	
	public ColorSchema(Color[] colors, Color defaultColor) {
		setColors(colors);
		setDefaultColor(defaultColor);
	}
	
	/**
	 * The size of the color in schema which is not including
	 * defaultColor.
	 * @return Return the size of the color array
	 */
	public int size() {
		return this.totalColors;
	}

	/**
	 * Get color by index.
	 * @param index - index of the color in the schema
	 * @return Return a defaultColor if the given index is 
	 * out of bound. Else return the color of the given 
	 * index 
	 */
	public Color get(int index) {
		if (index >= this.totalColors) {
			return getDefaultColor();
		}
		
		return this.colors[index];
	}
	
	/**
	 * Get the set of available color.
	 * @return Always return the set of color under schema
	 */
	public Color[] getColors() {
		return this.colors;
	}

	/**
	 * Get the default color.
	 * @return Always return the default color
	 */
	public Color getDefaultColor() {
		return this.defaultColor;
	}

	private void setColors(Color[] colors) {
		if (colors == null) {
			this.colors = new Color[]{};
		} else {
			this.colors = colors;
		}
		this.totalColors = this.colors.length;
	}

	private void setDefaultColor(Color defaultColor) {
		/* Assigned to BLACK if it is not given */
		if (defaultColor == null) {
			this.defaultColor = Color.BLACK;
		} else {
			this.defaultColor = defaultColor;
		}
	}
}
