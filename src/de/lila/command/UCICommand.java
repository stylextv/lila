package de.lila.command;

import java.util.HashMap;

public abstract class UCICommand {
	
	private static final UCICommand[] COMMANDS = new UCICommand[] {
			new InfoCommand(),
			new ReadyCommand(),
			new SetOptionCommand(),
			new NewGameCommand(),
			new PositionCommand(),
			new GoCommand(),
			new StopCommand(),
			new DisplayCommand(),
			new PerftCommand(),
			new QuitCommand()
	};
	
	private String name;
	
	private String[] tokens;
	
	public UCICommand(String name, String... tokens) {
		this.name = name;
		this.tokens = tokens;
	}
	
	public abstract void execute(HashMap<String, String> args);
	
	public boolean isToken(String s) {
		for(String token : tokens) {
			if(token.equalsIgnoreCase(s)) return true;
		}
		
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public static UCICommand getCommand(String name) {
		for(UCICommand c : COMMANDS) {
			if(c.getName().equalsIgnoreCase(name)) return c;
		}
		
		return null;
	}
	
}
