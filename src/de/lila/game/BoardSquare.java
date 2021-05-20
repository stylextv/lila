package de.lila.game;

public class BoardSquare {
	
	public static final int NONE = -1;
	
	private static final char[] FILES = new char[] {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'
	};
	
	private static final char[] RANKS = new char[] {
			'8', '7', '6', '5', '4', '3', '2', '1'
	};
	
	public static String getNotation(int square) {
		int x = square % 8;
		int y = square / 8;
		
		return "" + FILES[x] + RANKS[y];
	}
	
	public static int getSquareFromNotation(String s) {
		char file = s.charAt(0);
		char rank = s.charAt(1);
		
		int x = file - FILES[0];
		int y = RANKS[0] - rank;
		
		return y * 8 + x;
	}
	
}
