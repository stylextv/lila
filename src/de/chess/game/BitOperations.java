package de.chess.game;

public class BitOperations {
	
	public static final int SHIFT_LEFT = -1;
	public static final int SHIFT_RIGHT = 1;
	public static final int SHIFT_UP = -8;
	public static final int SHIFT_DOWN = 8;
	
	private static final long DE_BRUIJN_CONSTANT = 0x03f79d71b4cb0a89l;
	
	private static final int[] MAGIC_TABLE = {
			0,  1,  48,  2, 57, 49, 28,  3,
			61, 58, 50, 42, 38, 29, 17,  4,
			62, 55, 59, 36, 53, 51, 43, 22,
			45, 39, 33, 30, 24, 18, 12,  5,
			63, 47, 56, 27, 60, 41, 37, 16,
			54, 35, 52, 21, 44, 32, 23, 11,
			46, 26, 40, 15, 34, 20, 31, 10,
			25, 14, 19,  9, 13,  8,  7,  6
	};
	
	public static long shift(long l, int dir) {
		if(dir == 0) return l;
		
		if(dir > 0) return l << dir;
		return l >>> -dir;
	}
	
	public static long inverse(long l) {
		return ~l;
	}
	
	public static int bitScanForward(long l) {
		int i = (int) (((l & -l) * DE_BRUIJN_CONSTANT) >>> 58);
		
		return MAGIC_TABLE[i];
	}
	
	public static int bitScanBackward(long l) {
		return 63 - bitScanForward(Long.reverse(l));
	}
	
	public static int countBits(long l) {
		return Long.bitCount(l);
	}
	
	public static long moveEast(long l, int n) {
		long newBoard = l;
		
		for(int i = 0; i < n; i++) {
			newBoard = ((newBoard << 1) & (~0x101010101010101l));
		}
		return newBoard;
	}
	
	public static long moveWest(long l, int n) {
		long newBoard = l;
		
		for(int i = 0; i < n; i++) {
			newBoard = ((newBoard >>> 1) & (~0x8080808080808080l));
		}
		return newBoard;
	}
	
	public static void print(long l) {
		System.out.println();
		
		String s = Long.toBinaryString(l);
		
		while(s.length() < 64) s = "0" + s;
		
		char[] map = s.toCharArray();
		
		for(int y=0; y<8; y++) {
			String s2 = "";
			
			for(int x=0; x<8; x++) {
				s2 = s2 + map[63 - (y * 8 + x)];
				
				if(!(x == 7 && y == 7)) s2 = s2 + ", ";
			}
			
			System.out.println(s2);
		}
		
		System.out.println(s);
	}
	
}
