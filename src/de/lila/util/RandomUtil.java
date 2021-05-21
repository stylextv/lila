package de.lila.util;

public class RandomUtil {
	
	private static long seed;
	
	public static void setSeed(long l) {
		seed = l;
	}
	
	public static long next() {
		seed ^= seed >> 12;
		seed ^= seed << 25;
		seed ^= seed >> 27;
		
	    return seed * 2685821657736338717l;
	}
	
}
