package de.lila.test;

import de.lila.game.Board;
import de.lila.game.Move;
import de.lila.game.MoveGenerator;
import de.lila.game.MoveList;

public class Perft {
	
	private static Board board;
	
	public static void main(String[] args) {
		board = new Board();
		
		for(int i = 0; i < 10; i++) {
			System.out.println(perft(i));
		}
	}
	
	private static int perft(int depth) {
		if(depth == 0) return 1;
		
		int nodes = 0;
		
		MoveList list = new MoveList();
		
		MoveGenerator.generateAllMoves(board, list);
		
		while(list.hasMovesLeft()) {
			Move m = list.next();
			
			board.makeMove(m);
			
			if(!board.isOpponentInCheck()) {
				nodes += perft(depth - 1);
			}
			
			board.undoMove(m);
		}
		return nodes;
	}
	
}
