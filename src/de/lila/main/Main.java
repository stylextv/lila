package de.lila.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import de.lila.ai.SearchExecutor;
import de.lila.ai.TranspositionTable;
import de.lila.command.UCIController;
import de.lila.game.LookupTable;
import de.lila.game.PositionKey;
import de.lila.option.Options;

public class Main {
	
	private static UCIController controller;
	
	private static BufferedReader reader;
	
	private static boolean running = true;
	
	public static void main(String[] args) {
		try {
			init();
			
			reader = new BufferedReader(new InputStreamReader(System.in));
			
			String line;
			
			while(running && (line = reader.readLine()) != null) {
				controller.parseLine(line);
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void init() {
		controller = new UCIController();
		
		controller.init();
		
		LookupTable.init();
		
		PositionKey.init();
		
		TranspositionTable.init();
		
		Options.init();
	}
	
	public static void quit() {
		running = false;
		
		SearchExecutor.shutdown();
	}
	
	public static UCIController getController() {
		return controller;
	}
	
}
