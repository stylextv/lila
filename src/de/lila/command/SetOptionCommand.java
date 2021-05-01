package de.lila.command;

import de.lila.option.Options;
import de.lila.option.UCIOption;
import de.lila.util.StringUtil;

public class SetOptionCommand extends UCICommand {
	
	public SetOptionCommand() {
		super("setoption");
	}
	
	@Override
	public void execute(String[] args) {
		String name = "";
		String valueString = null;
		
		if(args.length > 2 && args[1].equalsIgnoreCase("name")) {
			name = args[2];
		}
		
		if(args.length > 4 && args[3].equalsIgnoreCase("value")) {
			valueString = StringUtil.collapseArray(args, 4, args.length - 1);
		}
		
		UCIOption option = Options.getOption(name);
		
		if(option == null) {
			System.out.println("No such option: " + name);
			
			return;
		}
		
		option.getField().setValue(valueString);
	}
	
}
