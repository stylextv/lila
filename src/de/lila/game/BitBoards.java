package de.lila.game;

public class BitBoards {
	
	public static final long NO_SQUARES = 0;
	
	public static final long ALL_SQUARES = -1;
	
	public static final long LIGHT_SQUARES = -6172840429334713771l;
	public static final long DARK_SQUARES = 6172840429334713770l;
	
	public static final long FILE_A = 72340172838076673l;
	public static final long FILE_B = 144680345676153346l;
	public static final long FILE_C = 289360691352306692l;
	public static final long FILE_D = 578721382704613384l;
	public static final long FILE_E = 1157442765409226768l;
	public static final long FILE_F = 2314885530818453536l;
	public static final long FILE_G = 4629771061636907072l;
	public static final long FILE_H = -9187201950435737472l;
	
	public static final long RANK_8 = 255;
	public static final long RANK_7 = 65280;
	public static final long RANK_6 = 16711680;
	public static final long RANK_5 = 4278190080l;
	public static final long RANK_4 = 1095216660480l;
	public static final long RANK_3 = 280375465082880l;
	public static final long RANK_2 = 71776119061217280l;
	public static final long RANK_1 = -72057594037927936l;
	
	public static final long[] SINGLE_SQUARE = new long[64];
	
	private static final long[][] REPEATED_RANKS = new long[8][8];
	private static final long[][] REPEATED_FILES = new long[8][8];
	
	private static final long[] ADJACENT_FILES = new long[8];
	
	public static void init() {
		long l = 1;
		
		for(int i = 0; i < SINGLE_SQUARE.length; i++) {
			SINGLE_SQUARE[i] = l;
			
			l <<= 1;
		}
		
		initRepeatedSquares();
		initAdjacentFiles();
	}
	
	private static void initRepeatedSquares() {
		for(int from = 0; from < 8; from++) {
			for(int to = from; to < 8; to++) {
				
				long ranks = BitOperations.shift(RANK_8, BitOperations.SHIFT_DOWN * from);
				long files = BitOperations.shift(FILE_A, BitOperations.SHIFT_RIGHT * from);
				
				int n = to - from;
				
				for(int i = 0; i < n; i++) {
					ranks |= BitOperations.shift(ranks, BitOperations.SHIFT_DOWN);
					files |= BitOperations.shift(files, BitOperations.SHIFT_RIGHT);
				}
				
				REPEATED_RANKS[from][to] = ranks;
				REPEATED_FILES[from][to] = files;
			}
		}
	}
	
	private static void initAdjacentFiles() {
		for(int x = 0; x < 8; x++) {
			
			long l = NO_SQUARES;
			
			if(x > 0) l |= BitOperations.shift(FILE_A, BitOperations.SHIFT_RIGHT * (x - 1));
			if(x < 7) l |= BitOperations.shift(FILE_A, BitOperations.SHIFT_RIGHT * (x + 1));
			
			ADJACENT_FILES[x] = l;
		}
	}
	
	public static long getRank(int square) {
		int y = square / 8;
		
		return BitOperations.shift(RANK_8, BitOperations.SHIFT_DOWN * y);
	}
	
	public static long getFile(int square) {
		int x = square % 8;
		
		return BitOperations.shift(FILE_A, BitOperations.SHIFT_RIGHT * x);
	}
	
	public static long getRanks(int fromY, int toY) {
		if(fromY > toY) return getRanks(toY, fromY);
		
		return REPEATED_RANKS[fromY][toY];
	}
	
	public static long getFiles(int fromX, int toX) {
		if(fromX > toX) return getFiles(toX, fromX);
		
		return REPEATED_FILES[fromX][toX];
	}
	
	public static long getAdjacentFiles(int square) {
		return ADJACENT_FILES[square % 8];
	}
	
	public static long getLowerRanks(int square, int dir) {
		int y = square / 8;
		
		if(dir == BitOperations.SHIFT_UP) {
			return getRanks(0, y - 1);
		} else {
			return getRanks(y + 1, 7);
		}
	}
	
	public static long getDoublePawnAttacks(int side, long pawns) {
		int dir = side == Piece.WHITE ? BitOperations.SHIFT_UP : BitOperations.SHIFT_DOWN;
		
		pawns = BitOperations.shift(pawns, dir);
		
		long attacksLeft = BitOperations.shift(pawns, BitOperations.SHIFT_LEFT);
		long attacksRight = BitOperations.shift(pawns, BitOperations.SHIFT_RIGHT);
		
		attacksLeft &= BitOperations.inverse(FILE_H);
		attacksRight &= BitOperations.inverse(FILE_A);
		
		return attacksLeft & attacksRight;
	}
	
}
