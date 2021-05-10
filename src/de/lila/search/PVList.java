package de.lila.search;

import de.lila.game.BoardConstants;
import de.lila.game.Move;

public class PVList {
	
	private Move[] moves;
	
	public PVList() {
		setMoves(new Move[BoardConstants.MAX_GAME_MOVES]);
	}
	
	public Move getMove(int plyFromRoot) {
		return moves[plyFromRoot];
	}
	
	public void setMove(Move move, int plyFromRoot) {
		moves[plyFromRoot] = move;
	}
	
	public Move[] getMoves() {
		return moves;
	}
	
	public void setMoves(Move[] moves) {
		this.moves = moves;
	}
	
}
