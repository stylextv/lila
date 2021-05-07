package de.lila.command;

import java.util.HashMap;

public class ReadyCommand extends UCICommand {
	
	public ReadyCommand() {
		super("isready");
	}
	
	@Override
	public void execute(HashMap<String, String> args) {
		System.out.println("readyok");
	}
	
}
