package de.lila.search;

import de.lila.game.Move;

public class TranspositionTable {
	
	private static TranspositionEntry[] map;
	
	private static long lookupMask;
	
	public static void changeSize(int size) {
		long l = (size * 1000000l) / TranspositionEntry.MEMORY_SIZE;
		
		map = new TranspositionEntry[Integer.highestOneBit((int) l)];
		
		lookupMask = map.length - 1;
	}
	
	public static void putEntry(long key, int depth, int plyFromRoot, Move m, int type, int score, int age) {
		int index = getMapIndex(key);
		
		TranspositionEntry old = map[index];
		
		if(old == null || shouldReplace(depth, type, age, old)) map[index] = new TranspositionEntry(key, depth, plyFromRoot, m, type, score, age);
	}
	
	public static TranspositionEntry getEntry(long key) {
		TranspositionEntry e = map[getMapIndex(key)];
		
		if(e == null || e.getPositionKey() != key) return null;
		return e;
	}
	
	private static int getMapIndex(long key) {
		return (int) (key & lookupMask);
	}
	
	private static boolean shouldReplace(int depth, int type, int age, TranspositionEntry old) {
		return type == TranspositionEntry.TYPE_EXACT || age > old.getAge() || depth > old.getDepth();
	}
	
}
