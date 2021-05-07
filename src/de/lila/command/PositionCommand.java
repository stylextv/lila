package de.lila.command;

import java.util.HashMap;

import de.lila.game.Board;
import de.lila.game.BoardConstants;
import de.lila.game.Move;
import de.lila.game.MoveGenerator;
import de.lila.game.MoveList;
import de.lila.main.Main;

public class PositionCommand extends UCICommand {
	
	public PositionCommand() {
		super("position", "startpos", "fen", "moves");
	}
	
	@Override
	public void execute(HashMap<String, String> args) {
		Board b = Main.getController().getBoard();
		
		String fen = args.get("startpos") == null ? null : BoardConstants.STARTING_POSITION;
		
		String inputFen = args.get("fen");
		
		if(inputFen != null) fen = inputFen;
		
		if(fen == null) return;
		
		b.parseFen(fen);
		
		String moves = args.get("moves");
		
		if(moves != null) {
			
			String[] arr = moves.split(" ");
			
			for(String s : arr) {
				MoveList list = new MoveList();
				
				MoveGenerator.generateAllMoves(b, list);
				
				Move move = null;
				
				while(list.hasMovesLeft()) {
					Move m = list.next();
					
					if(m.getAlgebraicNotation().equals(s)) {
						move = m;
						
						break;
					}
				}
				
				if(move == null) return;
				
				b.makeMove(move);
			}
		}
	}
	
}
