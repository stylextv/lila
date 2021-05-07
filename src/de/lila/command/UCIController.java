package de.lila.command;

import java.util.HashMap;

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
		
		if(c == null) {
			System.out.println("Unknown command: " + name);
			
			return;
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		String currentToken = null;
		String currentValue = null;
		
		for(String arg : args) {
			if(c.isToken(arg)) {
				
				if(currentToken != null) {
					map.put(currentToken, currentValue);
				}
				
				currentToken = arg.toLowerCase();
				
				currentValue = "";
				
			} else if(currentToken != null) {
				
				currentValue = currentValue.isBlank() ? arg : currentValue + " " + arg;
			}
		}
		
		if(currentToken != null) {
			map.put(currentToken, currentValue);
		}
		
		c.execute(map);
	}
	
	public Board getBoard() {
		return board;
	}
	
}
