package de.lila.game;

import java.util.ArrayList;

public class MoveGenerator {
	
	public static final int[] KING_START_POSITION = new int[] {
			60, 4
	};
	
	public static final int[] LEFT_ROOK_START_POSITION = new int[] {
			56, 0
	};
	
	public static final int[] RIGHT_ROOK_START_POSITION = new int[] {
			63, 7
	};
	
	public static void generateAllMoves(Board b, MoveList list) {
		generateAllMoves(b, b.getSide(), list);
	}
	
	public static void generateAllMoves(Board b, int side, MoveList list) {
		int facing = 1;
		int opponentSide = Piece.flipSide(side);
		
		if(side == Piece.WHITE) facing = -1;
		
		long possibleSquares = BitOperations.inverse(b.getBitBoard(side).getValue());
		long occupiedSquares = b.getBitBoard(side).orReturn(b.getBitBoard(opponentSide));
		long emptySquares = BitOperations.inverse(occupiedSquares);
		
		addPawnMoves(b, list, side, opponentSide, facing, emptySquares);
		addKnightMoves(b, list, side, possibleSquares);
		addKingMoves(b, list, side, possibleSquares, emptySquares);
		
		addSliderMoves(b, list, side, possibleSquares, occupiedSquares, Piece.BISHOP);
		addSliderMoves(b, list, side, possibleSquares, occupiedSquares, Piece.ROOK);
	}
	
	private static void addPawnMoves(Board b, MoveList list, int side, int opponentSide, int facing, long emptySquares) {
		long pawnSquares = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.PAWN));
		
		int forward;
		
		if(side == Piece.WHITE) forward = BitOperations.SHIFT_UP;
		else forward = BitOperations.SHIFT_DOWN;
		
		long pawnMoves = pawnSquares;
		
		pawnMoves = BitOperations.shift(pawnMoves, forward);
		
		long possiblePawnMoves = pawnMoves & emptySquares;
		
		long possiblePawnMovesAdd = possiblePawnMoves;
		
		while(possiblePawnMovesAdd != 0) {
			int index = BitOperations.bitScanForward(possiblePawnMovesAdd);
			
			addPawnMove(list, side, index - 8 * facing, index, 0, MoveFlag.NONE);
			
			possiblePawnMovesAdd ^= BitBoard.SINGLE_SQUARE[index];
		}
		
		long possiblePawnDoubleMoves = possiblePawnMoves;
		if(side == Piece.WHITE) possiblePawnDoubleMoves &= BitBoard.RANK_3;
		else possiblePawnDoubleMoves &= BitBoard.RANK_6;
		
		possiblePawnDoubleMoves = BitOperations.shift(possiblePawnDoubleMoves, forward);
		
		possiblePawnDoubleMoves = possiblePawnDoubleMoves & emptySquares;
		
		while(possiblePawnDoubleMoves != 0) {
			int index = BitOperations.bitScanForward(possiblePawnDoubleMoves);
			
			addPawnMove(list, side, index - 16 * facing, index, 0, MoveFlag.DOUBLE_PAWN_ADVANCE);
			
			possiblePawnDoubleMoves ^= BitBoard.SINGLE_SQUARE[index];
		}
		
		long opponentSquares = b.getBitBoard(opponentSide).getValue();
		
		long pawnAttacksLeft = BitOperations.shift(pawnMoves, BitOperations.SHIFT_LEFT);
		long pawnAttacksRight = BitOperations.shift(pawnMoves, BitOperations.SHIFT_RIGHT);
		
		pawnAttacksLeft &= BitOperations.inverse(BitBoard.FILE_H);
		pawnAttacksRight &= BitOperations.inverse(BitBoard.FILE_A);
		
		int enPassant = b.getEnPassant();
		
		if(enPassant != BoardSquare.NONE) {
			long moveTo = BitBoard.SINGLE_SQUARE[enPassant];
			
			if((pawnAttacksLeft & moveTo) != 0) {
				list.addMove(enPassant - 8 * facing + 1, enPassant, 0, 0, MoveFlag.EN_PASSANT);
			}
			if((pawnAttacksRight & moveTo) != 0) {
				list.addMove(enPassant - 8 * facing - 1, enPassant, 0, 0, MoveFlag.EN_PASSANT);
			}
		}
		
		pawnAttacksLeft = pawnAttacksLeft & opponentSquares;
		pawnAttacksRight = pawnAttacksRight & opponentSquares;
		
		while(pawnAttacksLeft != 0) {
			int index = BitOperations.bitScanForward(pawnAttacksLeft);
			
			addPawnMove(list, side, index - 8 * facing + 1, index, b.getPieceType(index), MoveFlag.NONE);
			
			pawnAttacksLeft ^= BitBoard.SINGLE_SQUARE[index];
		}
		while(pawnAttacksRight != 0) {
			int index = BitOperations.bitScanForward(pawnAttacksRight);
			
			addPawnMove(list, side, index - 8 * facing - 1, index, b.getPieceType(index), MoveFlag.NONE);
			
			pawnAttacksRight ^= BitBoard.SINGLE_SQUARE[index];
		}
	}
	
	private static void addPawnMove(MoveList list, int side, int from, int to, int captured, int flag) {
		int toY = to / 8;
		
		boolean promoted = toY % 7 == 0;
		
		if(promoted) {
			
			list.addMove(from, to, captured, Piece.QUEEN, flag);
			list.addMove(from, to, captured, Piece.KNIGHT, flag);
			list.addMove(from, to, captured, Piece.ROOK, flag);
			list.addMove(from, to, captured, Piece.BISHOP, flag);
			
		} else {
			
			list.addMove(from, to, captured, 0, flag);
		}
	}
	
	private static void addKnightMoves(Board b, MoveList list, int side, long possibleSquares) {
		long knightSquares = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.KNIGHT));
		
		while(knightSquares != 0) {
			int index = BitOperations.bitScanForward(knightSquares);
			
			long moves = LookupTable.KNIGHT_MOVES[index] & possibleSquares;
			
			while(moves != 0) {
				int to = BitOperations.bitScanForward(moves);
				
				list.addMove(index, to, b.getPieceType(to), 0, MoveFlag.NONE);
				
				moves ^= BitBoard.SINGLE_SQUARE[to];
			}
			
			knightSquares ^= BitBoard.SINGLE_SQUARE[index];
		}
	}
	
	private static void addKingMoves(Board b, MoveList list, int side, long possibleSquares, long emptySquares) {
		long kingSquares = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.KING));
		
		int index = BitOperations.bitScanForward(kingSquares);
		
		long moves = LookupTable.KING_MOVES[index] & possibleSquares;
		
		while(moves != 0) {
			int to = BitOperations.bitScanForward(moves);
			
			list.addMove(index, to, b.getPieceType(to), 0, MoveFlag.NONE);
			
			moves ^= BitBoard.SINGLE_SQUARE[to];
		}
		
		if(side == Piece.WHITE) {
			
			if((b.getCastlePerms() & Castling.WHITE_QUEEN_SIDE) == 0) {
				addCastlingMove(b, list, side, index, emptySquares, -1, MoveFlag.CASTLING_QUEEN_SIDE);
			}
			if((b.getCastlePerms() & Castling.WHITE_KING_SIDE) == 0) {
				addCastlingMove(b, list, side, index, emptySquares, 1, MoveFlag.CASTLING_KING_SIDE);
			}
			
		} else {
			
			if((b.getCastlePerms() & Castling.BLACK_QUEEN_SIDE) == 0) {
				addCastlingMove(b, list, side, index, emptySquares, -1, MoveFlag.CASTLING_QUEEN_SIDE);
			}
			if((b.getCastlePerms() & Castling.BLACK_KING_SIDE) == 0) {
				addCastlingMove(b, list, side, index, emptySquares, 1, MoveFlag.CASTLING_KING_SIDE);
			}
		}
	}
	
	private static void addCastlingMove(Board b, MoveList list, int side, int from, long emptySquares, int dir, int flag) {
		int to = from + dir * 2;
		
		int square = from;
		
		while(true) {
			square += dir;
			
			int x = square % 8;
			
			if(x == 0 || x == 7) break;
			
			if((emptySquares & BitBoard.SINGLE_SQUARE[square]) == 0) {
				return;
			}
		}
		
		for(int i = 0; i < 3; i++) {
			square = from + dir * i;
			
			if(b.isUnderAttack(square, side)) {
				return;
			}
		}
		
		list.addMove(from, to, 0, 0, flag);
	}
	
	private static void addSliderMoves(Board b, MoveList list, int side, long possibleSquares, long occupiedSquares, int type) {
		long squares = b.getBitBoard(side).andReturn(b.getBitBoard(type).orReturn(b.getBitBoard(Piece.QUEEN)));
		
		while(squares != 0) {
			int index = BitOperations.bitScanForward(squares);
			
			long moves = LookupTable.getSliderMoves(index, occupiedSquares, type);
			
			moves = moves & possibleSquares;
			
			while(moves != 0) {
				int to = BitOperations.bitScanForward(moves);
				
				list.addMove(index, to, b.getPieceType(to), 0, MoveFlag.NONE);
				
				moves ^= BitBoard.SINGLE_SQUARE[to];
			}
			
			squares ^= BitBoard.SINGLE_SQUARE[index];
		}
	}
	
	public static ArrayList<Move> getMovesForIndex(int index, Board b, MoveList list, boolean legalOnly) {
		ArrayList<Move> moves = new ArrayList<Move>();
		
		for(int i=0; i<list.getCount(); i++) {
			Move m = list.getMove(i);
			
			if(m.getFrom() == index) {
				if(!legalOnly || b.isLegalMove(m)) {
					moves.add(m);
				}
			}
		}
		
		return moves;
	}
	
}
