package de.chess.main;

import java.awt.Color;
import java.awt.Font;

public class Constants {
	
	public static final String NAME = "Bookmark";
	public static final int ELO = 2550;
	
	public static final int WINDOW_DEFAULT_WIDTH = 1050;
	public static final int WINDOW_DEFAULT_HEIGHT = 772;
	
	public static final int TILE_SIZE = 80;
	public static final int BOARD_SIZE = TILE_SIZE * 8;
	
	public static final Color COLOR_WHITE = new Color(0xFFFFFF);
	public static final Color COLOR_BLACK = new Color(0x000000);
	
	public static final Color COLOR_BACKGROUND = new Color(0x2B2B2B);
	public static final Color COLOR_MENU_BACKGROUND = COLOR_WHITE;
	
	public static final Color COLOR_TEXT_BLACK_TRANSPARENT = new Color(0, 0, 0, 166);
	
	public static final Color COLOR_BUTTON = new Color(0xF2F2F2);
	public static final Color COLOR_BUTTON_OUTLINE = new Color(0xC4C4C4);
	public static final Color COLOR_BUTTON_SHADOW = new Color(128, 128, 128, 35); // alpha = 26
	
	public static final Color COLOR_BOARD_HIGHLIGHT = new Color(155, 199, 0, 105);
	public static final Color COLOR_BOARD_MARKER = new Color(0, 0, 0, 26);
	
	public static final Color COLOR_PLAYER_WHITE = new Color(0xF2F2F2);
	public static final Color COLOR_PLAYER_BLACK = new Color(0x313335);
	
	public static final Color COLOR_HISTORY_BACKGROUND = COLOR_BUTTON;
	public static final Color COLOR_HISTORY_WHITE = new Color(38, 33, 27, 38);
	public static final Color COLOR_HISTORY_BLACK = new Color(0x312e2b);
	public static final Color COLOR_HISTORY_MARKER = COLOR_HISTORY_WHITE;
	public static final Color COLOR_HISTORY_HIGHLIGHT = new Color(0xfff35f);
	
	public static final Font FONT_LIGHT = new Font("Segoe UI Light", 0, 18);
	public static final Font FONT_BOLD = new Font("Segoe UI Bold", 0, 12);
	public static final Font FONT_EXTRA_BOLD = new Font("Montserrat ExtraBold", 0, 18);
	public static final Font FONT_EXTRA_BOLD_LARGE = new Font("Montserrat ExtraBold", 0, 24);
	
	public static final boolean PRINT_FPS = false;
	
}
