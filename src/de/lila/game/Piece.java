package de.lila.game;

public class Piece {
	
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	public static final int PAWN = 2;
	public static final int KNIGHT = 3;
	public static final int BISHOP = 4;
	public static final int ROOK = 5;
	public static final int QUEEN = 6;
	public static final int KING = 7;
	
	public static final int LAST = KING;
	
	public static final int TYPE_AMOUNT = 6;
	
	public static final int BOTH_SIDES = 8;
	public static final int ALL_PIECES = 9;
	
	public static final int BISHOP_PAIR = -1;
	
	private static final char[] FEN_NOTATIONS = new char[] {
			'P', 'N', 'B', 'R', 'Q', 'K',
			'p', 'n', 'b', 'r', 'q', 'k'
	};
	
	public static int flipSide(int side) {
		return side ^ 1;
	}
	
	public static int getPiece(int side, int type) {
		return type - PAWN + side * TYPE_AMOUNT;
	}
	
	public static int getSideOfPiece(int p) {
		return p / TYPE_AMOUNT;
	}
	
	public static int getTypeOfPiece(int p) {
		return p % TYPE_AMOUNT + PAWN;
	}
	
	public static char getFenNotation(int p) {
		return FEN_NOTATIONS[p];
	}
	
	public static int getPieceFromFenNotation(char ch) {
		for(int i=0; i<FEN_NOTATIONS.length; i++) {
			if(FEN_NOTATIONS[i] == ch) return i;
		}
		
		return -1;
	}
	
}
