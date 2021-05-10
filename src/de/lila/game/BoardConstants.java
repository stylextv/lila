package de.lila.game;

public class BoardConstants {
	
	public static final String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	
	public static final int BOARD_SIZE_SQ = 64;
	
	public static final int MAX_GAME_MOVES = 2048;
	
	public static final int MAX_POSSIBLE_MOVES = 256;
	
	public static final int[] KING_START_POSITION = new int[] {
			60, 4
	};
	
	public static final int[] LEFT_ROOK_START_POSITION = new int[] {
			56, 0
	};
	
	public static final int[] RIGHT_ROOK_START_POSITION = new int[] {
			63, 7
	};
	
}
