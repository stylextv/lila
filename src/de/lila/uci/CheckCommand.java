package de.lila.uci;

import de.lila.main.Constants;

public class CheckCommand extends UCICommand {
	
	public CheckCommand() {
		super("uci");
	}
	
	@Override
	public void execute(String[] args) {
		System.out.println("id name " + Constants.NAME);
		System.out.println("id author " + Constants.AUTHOR);
		System.out.println("");
		System.out.println("uciok");
	}
	
}
