package de.lila.ai;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import de.lila.game.Board;

public class SearchExecutor {
	
	private static ThreadPoolExecutor threadPool;
	
	public static void changeThreadAmount(int n) {
		if(threadPool != null) threadPool.shutdown();
		
		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(n);
	}
	
	public static void startSearch(Board b, int depth, int time) {
		long startTime = System.currentTimeMillis();
		
		threadPool.submit(() -> Search.findBestMove(b, depth, time, startTime));
	}
	
	public static void stopCurrentSearch() {
		Search.abort();
	}
	
	public static void shutdown() {
		threadPool.shutdown();
		
		stopCurrentSearch();
	}
	
}
