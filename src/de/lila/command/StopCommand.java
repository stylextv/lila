package de.lila.command;

import java.util.HashMap;

import de.lila.ai.SearchExecutor;

public class StopCommand extends UCICommand {
	
	public StopCommand() {
		super("stop");
	}
	
	@Override
	public void execute(HashMap<String, String> args) {
		SearchExecutor.stopCurrentSearch();
	}
	
}
