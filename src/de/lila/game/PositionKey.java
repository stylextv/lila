package de.lila.game;

import de.lila.util.RandomUtil;

public class PositionKey {
	
	private static final long[] RANDOM_NUMBERS = new long[12 * BoardConstants.BOARD_SIZE_SQ + 1 + 8 + 16];
	
	public static final int SIDE_OFFSET = 12 * 64;
	public static final int EN_PASSANT_OFFSET = 12 * 64 + 1;
	public static final int CASTLING_OFFSET = 12 * 64 + 1 + 8;
	
	public static void init() {
		RandomUtil.setSeed(1070372);
		
		for(int i = 0; i < RANDOM_NUMBERS.length; i++) {
			RANDOM_NUMBERS[i] = RandomUtil.next();
		}
	}
	
	public static long getRandomNumber(int index) {
		return RANDOM_NUMBERS[index];
	}
	
	public static long generatePositionKeySlow(Board b) {
		long key = 0;
		
		for(int i = 0; i < b.getPieces().length; i++) {
			int p = b.getPieces()[i];
			
			if(p != Piece.NO_PIECE) key ^= PositionKey.getRandomNumber(p * 64 + i);
		}
		
		if(b.getSide() == Piece.BLACK) key ^= PositionKey.getRandomNumber(SIDE_OFFSET);
		if(b.getEnPassant() != BoardSquare.NONE) key ^= PositionKey.getRandomNumber(EN_PASSANT_OFFSET + b.getEnPassant() % 8);
		
		int castlePerms = b.getCastlePerms();
		
		key ^= PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET + castlePerms);
		
		return key;
	}
	
}
