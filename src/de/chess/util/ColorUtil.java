package de.chess.util;

import java.awt.Color;

public class ColorUtil {
	
	public static Color TRANSPARENT = new Color(0, 0, 0, 0);
	
	public static Color mixColors(Color c1, Color c2, float f) {
		int r = mixChannels(c1.getRed(), c2.getRed(), f);
		int g = mixChannels(c1.getGreen(), c2.getGreen(), f);
		int b = mixChannels(c1.getBlue(), c2.getBlue(), f);
		
		return new Color(r, g, b);
	}
	
	private static int mixChannels(int v1, int v2, float f) {
		return (int) (v1 + (v2 - v1) * f);
	}
	
}
