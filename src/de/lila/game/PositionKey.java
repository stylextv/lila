package de.lila.game;

import java.util.Random;

public class PositionKey {
	
	private static final long[] RANDOM_NUMBERS = new long[13 * BoardConstants.BOARD_SIZE_SQ + 1 + 8 + 4];
	
	public static final int NOTHING_OFFSET = 12 * 64;
	public static final int SIDE_OFFSET = 13 * 64;
	public static final int EN_PASSANT_OFFSET = 13 * 64 + 1;
	public static final int CASTLING_OFFSET = 13 * 64 + 1 + 8;
	
	public static void init() {
		Random random = new Random(1070372);
		
		for(int i = 0; i < RANDOM_NUMBERS.length; i++) {
			RANDOM_NUMBERS[i] = random.nextLong();
		}
	}
	
	public static long getRandomNumber(int index) {
		return RANDOM_NUMBERS[index];
	}
	
	public static long generatePositionKeySlow(Board b) {
		long key = 0;
		
		for(int i=0; i<b.getPieces().length; i++) {
			int p = b.getPieces()[i];
			
			if(p == Piece.NO_PIECE) key ^= PositionKey.getRandomNumber(PositionKey.NOTHING_OFFSET + i);
			else key ^= PositionKey.getRandomNumber(p * 64 + i);
		}
		
		if(b.getSide() == Piece.BLACK) key ^= PositionKey.getRandomNumber(SIDE_OFFSET);
		if(b.getEnPassant() != BoardSquare.NONE) key ^= PositionKey.getRandomNumber(EN_PASSANT_OFFSET + b.getEnPassant() % 8);
		
		int castlePerms = b.getCastlePerms();
		
		if(castlePerms != 0) {
			if((castlePerms & Castling.WHITE_KING_SIDE) != 0) key ^= PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET);
			if((castlePerms & Castling.WHITE_QUEEN_SIDE) != 0) key ^= PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET + 1);
			if((castlePerms & Castling.BLACK_KING_SIDE) != 0) key ^= PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET + 2);
			if((castlePerms & Castling.BLACK_QUEEN_SIDE) != 0) key ^= PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET + 3);
		}
		
		return key;
	}
	
}
