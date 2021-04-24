package de.lila.uci;

import de.lila.game.Board;
import de.lila.main.Constants;

public class UCIController {
	
	private Board board;
	
	public void init() {
		board = new Board();
		
		System.out.println(Constants.NAME + " by " + Constants.AUTHOR);
	}
	
	public void parseLine(String s) {
		String[] args = s.split(" ");
		
		String name = args[0];
		
		UCICommand c = UCICommand.getCommand(name);
		
		if(c != null) c.execute(args);
		else System.out.println("Unknown command: " + name);
	}
	
	public Board getBoard() {
		return board;
	}
	
}
