package de.lila.option;

import de.lila.ai.SearchExecutor;

public class Options {
	
	private static final UCIOption[] OPTIONS = new UCIOption[1];
	
	public static final UCIOption THREADS = new UCIOption("Threads", new SpinField(1, 1, 512, (n) -> SearchExecutor.changeThreadAmount(n)));
	
	private static int pointer;
	
	public static void init() {
		pointer = 0;
	}
	
	public static void registerOption(UCIOption option) {
		OPTIONS[pointer] = option;
		
		pointer++;
	}
	
	public static UCIOption getOption(String name) {
		for(UCIOption option : OPTIONS) {
			if(option.getName().equalsIgnoreCase(name)) return option;
		}
		
		return null;
	}
	
	public static UCIOption[] list() {
		return OPTIONS;
	}
	
}
