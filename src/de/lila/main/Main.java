package de.lila.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import de.lila.uci.UCIController;

public class Main {
	
	private static UCIController controller;
	
	private static BufferedReader reader;
	
	private static boolean running = true;
	
	public static void main(String[] args) {
		try {
			controller = new UCIController();
			
			controller.init();
			
			reader = new BufferedReader(new InputStreamReader(System.in));
			
			String line;
			
			while(running && (line = reader.readLine()) != null) {
				controller.parseLine(line);
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void quit() {
		running = false;
	}
	
	public static UCIController getController() {
		return controller;
	}
	
}
