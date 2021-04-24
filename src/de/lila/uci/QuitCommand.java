package de.lila.uci;

import de.lila.main.Main;

public class QuitCommand extends UCICommand {
	
	public QuitCommand() {
		super("quit");
	}
	
	@Override
	public void execute(String[] args) {
		Main.quit();
	}
	
}
