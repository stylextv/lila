package de.lila.command;

import de.lila.main.Constants;
import de.lila.option.Options;
import de.lila.option.UCIOption;

public class CheckCommand extends UCICommand {
	
	public CheckCommand() {
		super("uci");
	}
	
	@Override
	public void execute(String[] args) {
		System.out.println("id name " + Constants.NAME);
		System.out.println("id author " + Constants.AUTHOR);
		System.out.println("");
		
		printOptions();
		
		System.out.println("uciok");
	}
	
	private static void printOptions() {
		for(UCIOption option : Options.list()) {
			
			System.out.println(option.toString());
		}
	}
	
}
