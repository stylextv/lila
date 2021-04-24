package de.lila.uci;

public abstract class UCICommand {
	
	private static final UCICommand[] COMMANDS = new UCICommand[] {
			new CheckCommand(),
			new ReadyCommand(),
			new NewGameCommand(),
			new PositionCommand(),
			new GoCommand(),
			new StopCommand(),
			new DisplayCommand(),
			new QuitCommand()
	};
	
	private String name;
	
	public UCICommand(String name) {
		this.name = name;
	}
	
	public abstract void execute(String[] args);
	
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
