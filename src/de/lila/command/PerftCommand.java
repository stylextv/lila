package de.lila.command;

import java.util.HashMap;

import de.lila.game.Board;
import de.lila.game.Move;
import de.lila.game.MoveGenerator;
import de.lila.game.MoveList;
import de.lila.main.Main;

public class PerftCommand extends UCICommand {
	
	public PerftCommand() {
		super("perft", "depth");
	}
	
	@Override
	public void execute(HashMap<String, String> args) {
		Board b = Main.getController().getBoard();
		
		String s = args.get("depth");
		
		int n = s == null ? Integer.MAX_VALUE : Integer.parseInt(s);
		
		for(int i = 0; i < n; i++) {
			System.out.println(perft(b, i));
		}
	}
	
	private static int perft(Board b, int depth) {
		if(depth == 0) return 1;
		
		int nodes = 0;
		
		MoveList list = new MoveList();
		
		MoveGenerator.generateAllMoves(b, list);
		
		while(list.hasMovesLeft()) {
			Move m = list.next();
			
			b.makeMove(m);
			
			if(!b.isOpponentInCheck()) {
				nodes += perft(b, depth - 1);
			}
			
			b.undoMove(m);
		}
		return nodes;
	}
	
}
