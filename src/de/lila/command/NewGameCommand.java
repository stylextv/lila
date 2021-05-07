package de.lila.command;

import java.util.HashMap;

public class NewGameCommand extends UCICommand {
	
	public NewGameCommand() {
		super("ucinewgame");
	}
	
	@Override
	public void execute(HashMap<String, String> args) {
		
	}
	
}
