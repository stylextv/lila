package de.chess.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

public class FontUtil {
	
	public static void load() {
		try {
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			
			loadFont(env, "Montserrat-ExtraBold.ttf");
			loadFont(env, "Segoe UI Light.ttf");
			loadFont(env, "Segoe UI Bold.ttf");
			
		} catch (Exception ex) {
			ex.printStackTrace();
			
			System.exit(1);
		}
	}
	
	private static void loadFont(GraphicsEnvironment env, String name) throws FontFormatException, IOException {
		Font font = Font.createFont(Font.TRUETYPE_FONT, FontUtil.class.getClassLoader().getResourceAsStream("assets/fonts/"+name));
		
		env.registerFont(font);
	}
	
}
