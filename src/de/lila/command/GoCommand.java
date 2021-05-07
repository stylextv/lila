package de.lila.command;

import java.util.HashMap;

import de.lila.ai.SearchExecutor;
import de.lila.game.Board;
import de.lila.main.Main;

public class GoCommand extends UCICommand {
	
	public GoCommand() {
		super("go", "depth", "movetime");
	}
	
	@Override
	public void execute(HashMap<String, String> args) {
		Board b = Main.getController().getBoard();
		
		String s1 = args.get("depth");
		String s2 = args.get("movetime");
		
		int depth = s1 == null ? 0 : Integer.parseInt(s1);
		int time = s2 == null ? 0 : Integer.parseInt(s2);
		
		SearchExecutor.startSearch(b, depth, time);
	}
	
}
