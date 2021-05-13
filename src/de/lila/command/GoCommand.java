package de.lila.command;

import java.util.HashMap;

import de.lila.search.SearchExecutor;
import de.lila.game.Board;
import de.lila.game.Piece;
import de.lila.main.Main;

public class GoCommand extends UCICommand {
	
	private static final int TIME_BUFFER = 10;
	
	public GoCommand() {
		super("go", "depth", "movetime", "wtime", "btime", "movestogo", "winc", "binc");
	}
	
	@Override
	public void execute(HashMap<String, String> args) {
		Board b = Main.getController().getBoard();
		
		String s1 = args.get("depth");
		String s2 = args.get("movetime");
		
		int depth = s1 == null ? 0 : Integer.parseInt(s1);
		int time = s2 == null ? 0 : Integer.parseInt(s2);
		
		if(time == 0) {
			String s3 = args.get(b.getSide() == Piece.WHITE ? "wtime" : "btime");
			String s4 = args.get("movestogo");
			String s5 = args.get(b.getSide() == Piece.WHITE ? "winc" : "binc");
			
			int timeRemaining = s3 == null ? 0 : Integer.parseInt(s3);
			int movesRemaining = s4 == null ? 32 : Integer.parseInt(s4);
			int timeIncrement = s5 == null ? 0 : Integer.parseInt(s5);
			
			int i = movesRemaining - 1;
			
			timeRemaining += i * timeIncrement;
			
			if(s4 != null && movesRemaining > 1) timeRemaining -= TIME_BUFFER * i;
			
			time = timeRemaining / movesRemaining;
		}
		
		SearchExecutor.startSearch(b, depth, time);
	}
	
}
