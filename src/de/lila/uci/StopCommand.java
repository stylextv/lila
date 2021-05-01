package de.lila.uci;

import de.lila.ai.SearchExecutor;

public class StopCommand extends UCICommand {
	
	public StopCommand() {
		super("stop");
	}
	
	@Override
	public void execute(String[] args) {
		SearchExecutor.stopCurrentSearch();
	}
	
}
