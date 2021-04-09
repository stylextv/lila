package de.chess.ai;

import de.chess.game.Move;

public class TranspositionTable {
	
	private static final int SIZE = 27;
	
	private static final TranspositionEntry[] MAP = new TranspositionEntry[1 << SIZE];
	
	private static final long LOOKUP_MASK = MAP.length - 1;
	
	public static void putEntry(long key, int depth, int plyFromRoot, Move m, int type, int score, int age) {
		int index = getMapIndex(key);
		
		TranspositionEntry old = MAP[index];
		
		if(old == null || shouldReplace(depth, type, age, old)) MAP[index] = new TranspositionEntry(key, depth, plyFromRoot, m, type, score, age);
	}
	
	public static TranspositionEntry getEntry(long key) {
		TranspositionEntry e = MAP[getMapIndex(key)];
		
		if(e == null || e.getPositionKey() != key) return null;
		return e;
	}
	
	private static int getMapIndex(long key) {
		return (int) (key & LOOKUP_MASK);
	}
	
	private static boolean shouldReplace(int depth, int type, int age, TranspositionEntry old) {
		return type == TranspositionEntry.TYPE_EXACT || depth > old.getDepth() || age > old.getAge();
	}
	
}
