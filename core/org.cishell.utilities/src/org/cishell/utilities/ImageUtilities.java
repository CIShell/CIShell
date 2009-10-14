package org.cishell.utilities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageUtilities {
	// Constants.
	private static final int TEMPORARY_IMAGE_WIDTH = 640;
	private static final int TEMPORARY_IMAGE_HEIGHT = 480;
	
	public static BufferedImage createBufferedImageFilledWithColor
			(Color fillColor) {
		// Create the image (that we will fill and output).
		BufferedImage bufferedImage = new BufferedImage(TEMPORARY_IMAGE_WIDTH,
														TEMPORARY_IMAGE_HEIGHT,
														BufferedImage.TYPE_INT_RGB);
		
		// Get the image's graphics context so we can paint to/fill the image.
		Graphics2D bufferedImageGraphics2D = bufferedImage.createGraphics();
		
		// Fill the image with the color passed in.
		bufferedImageGraphics2D.setBackground(fillColor);
		
		return bufferedImage;
	}
}