package de.lila.command;

import java.util.HashMap;

import de.lila.main.Main;

public class QuitCommand extends UCICommand {
	
	public QuitCommand() {
		super("quit");
	}
	
	@Override
	public void execute(HashMap<String, String> args) {
		Main.quit();
	}
	
}
