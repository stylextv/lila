package de.lila.ai;

import de.lila.game.BoardConstants;
import de.lila.game.Move;

public class KillerTable {
	
	public static final int SIZE = 3;
	
	private static final Move[][] TABLE = new Move[BoardConstants.MAX_GAME_MOVES][SIZE];
	
	public static void storeMove(Move move, int ply) {
		if(move.isTactical()) return;
		
		short hash = move.getHash();
		
		Move[] moves = TABLE[ply];
		
		for(Move m : moves) {
			if(m == null) break;
			
			if(m.getHash() == hash) return;
		}
		
		for(int i = moves.length - 2; i >= 0; i--) {
			moves[i + 1] = moves[i];
		}
		
		moves[0] = move;
	}
	
	public static Move getMove(int ply, int index) {
		return TABLE[ply][index];
	}
	
}
