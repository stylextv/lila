package de.lila.command;

import de.lila.game.Board;
import de.lila.game.BoardConstants;
import de.lila.game.Move;
import de.lila.game.MoveGenerator;
import de.lila.game.MoveList;
import de.lila.main.Main;
import de.lila.util.StringUtil;

public class PositionCommand extends UCICommand {
	
	public PositionCommand() {
		super("position");
	}
	
	@Override
	public void execute(String[] args) {
		Board b = Main.getController().getBoard();
		
		int i = 1;
		
		if(args.length <= i) return;
		
		String fen;
		
		if(args[i].equalsIgnoreCase("startpos")) {
			fen = BoardConstants.STARTING_POSITION;
			
			i++;
		} else if(args[i].equalsIgnoreCase("fen")) {
			fen = StringUtil.collapseArray(args, i + 1, i + 6);
			
			i += 7;
		} else return;
		
		b.parseFen(fen);
		
		if(args.length > i && args[i].equalsIgnoreCase("moves")) {
			
			for(int j = i + 1; j < args.length; j++) {
				String s = args[j];
				
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
