package de.lila.ai;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import de.lila.game.Board;

public class SearchExecutor {
	
	private static final int CONNECTION_TIME_COMPENSATION = 10;
	
	private static ThreadPoolExecutor threadPool;
	
	public static void init() {
		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	}
	
	public static void startSearch(Board b, int time, int depth) {
		long startTime = System.currentTimeMillis() - CONNECTION_TIME_COMPENSATION;
		
		threadPool.submit(() -> Search.findBestMove(b, time, depth, startTime));
	}
	
	public static void stopCurrentSearch() {
		Search.abort();
	}
	
	public static void shutdown() {
		threadPool.shutdownNow();
		
		stopCurrentSearch();
	}
	
}
