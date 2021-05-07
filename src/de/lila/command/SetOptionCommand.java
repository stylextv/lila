package de.lila.command;

import java.util.HashMap;

import de.lila.option.Options;
import de.lila.option.UCIOption;

public class SetOptionCommand extends UCICommand {
	
	public SetOptionCommand() {
		super("setoption", "name", "value");
	}
	
	@Override
	public void execute(HashMap<String, String> args) {
		String name = args.get("name");
		
		UCIOption option = Options.getOption(name);
		
		if(option == null) {
			System.out.println("No such option: " + name);
			
			return;
		}
		
		option.getField().setValue(args.get("value"));
	}
	
}
