package de.lila.option;

import de.lila.search.SearchExecutor;
import de.lila.search.TranspositionTable;

public class Options {
	
	private static final UCIOption[] OPTIONS = new UCIOption[2];
	
	public static final UCIOption THREADS = new UCIOption("Threads", new SpinField(1, 1, 1, (n) -> SearchExecutor.changeThreadAmount(n))); // max = 512
	public static final UCIOption HASH_SIZE = new UCIOption("Hash", new SpinField(64, 1, 1024000, (size) -> TranspositionTable.changeSize(size)));
	
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
