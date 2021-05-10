package de.lila.search;

import de.lila.game.BoardConstants;
import de.lila.game.Piece;

public class HistoryHeuristic {
	
	private static final int[][][] TABLE = new int[2][Piece.TYPE_AMOUNT][BoardConstants.BOARD_SIZE_SQ];
	
	public static void addToScore(int side, int pieceType, int to, int depth) {
		int k = TABLE[side][pieceType - Piece.PAWN][to];
		
		k += depth * depth;
		
		TABLE[side][pieceType - Piece.PAWN][to] = k;
		
		if(k > MoveEvaluator.HISTORY_HEURISTIC_SCORE) reduceTableValues();
	}
	
	private static void reduceTableValues() {
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < Piece.TYPE_AMOUNT; j++) {
				for(int k = 0; k < BoardConstants.BOARD_SIZE_SQ; k++) {
					
					TABLE[i][j][k] = TABLE[i][j][k] / 2;
				}
			}
		}
	}
	
	public static int getScore(int side, int pieceType, int to) {
		return TABLE[side][pieceType - Piece.PAWN][to];
	}
	
}
