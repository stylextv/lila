package de.lila.uci;

public class ReadyCommand extends UCICommand {
	
	public ReadyCommand() {
		super("isready");
	}
	
	@Override
	public void execute(String[] args) {
		System.out.println("readyok");
	}
	
}
