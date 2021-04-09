package de.chess.game;

public class BitBoard {
	
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
	
	public static final long FULL_BOARD = -1;
	
	private long value;
	
	public BitBoard() {
		this(0);
	}
	
	public BitBoard(long value) {
		this.value = value;
	}
	
	public void clear() {
		value = 0;
	}
	
	public void or(long l) {
		value |= l;
	}
	
	public long orReturn(BitBoard b) {
		return value | b.getValue();
	}
	
	public void and(long l) {
		value &= l;
	}
	
	public long andReturn(long l) {
		return value & l;
	}
	
	public long andReturn(BitBoard b) {
		return value & b.getValue();
	}
	
	public void xor(long l) {
		value ^= l;
	}
	
	public long getValue() {
		return value;
	}
	
	public static long getRank(int square) {
		int y = square / 8;
		
		return BitOperations.shift(RANK_8, BitOperations.SHIFT_DOWN * y);
	}
	
	public static long getFile(int square) {
		int x = square % 8;
		
		return BitOperations.shift(FILE_A, BitOperations.SHIFT_RIGHT * x);
	}
	
	public static long getAdjacentFiles(int square) {
		long l = 0;
		
		int x = square % 8;
		
		if(x > 0) l |= BitOperations.shift(FILE_A, BitOperations.SHIFT_RIGHT * (x - 1));
		if(x < 7) l |= BitOperations.shift(FILE_A, BitOperations.SHIFT_RIGHT * (x + 1));
		
		return l;
	}
	
	public static long getLowerRanks(int square, int dir) {
		long l = 0;
		
		int y = square / 8;
		
		int m = dir / 8;
		
		for(int cy = y + m; cy >= 0 && cy < 8; cy += m) {
			l |= BitOperations.shift(RANK_8, BitOperations.SHIFT_DOWN * cy);
		}
		
		return l;
	}
	
	public static long getDoublePawnAttacks(int side, long pawns) {
		int dir = side == PieceCode.WHITE ? BitOperations.SHIFT_UP : BitOperations.SHIFT_DOWN;
		
		return BitOperations.shift(pawns, dir + BitOperations.SHIFT_LEFT) & BitOperations.shift(pawns, dir + BitOperations.SHIFT_RIGHT);
	}
	
}
