package de.lila.game;

public class BoardSquare {
	
	public static final int NONE = -1;
	
	private static final char[] RANKS = new char[] {
			'8', '7', '6', '5', '4', '3', '2', '1'
	};
	
	private static final char[] FILES = new char[] {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'
	};
	
	public static String getNotation(int square) {
		int x = square % 8;
		int y = square / 8;
		
		return "" + FILES[x] + RANKS[y];
	}
	
	public static int getIndexFromNotation(String s) {
		char file = s.charAt(0);
		char rank = s.charAt(1);
		
		int x = 0;
		int y = 0;
		
		for(int i=0; i<FILES.length; i++) {
			if(file == FILES[i]) x = i;
		}
		
		for(int i=0; i<RANKS.length; i++) {
			if(rank == RANKS[i]) y = i;
		}
		
		return y * 8 + x;
	}
	
}
