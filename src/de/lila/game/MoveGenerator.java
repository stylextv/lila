package de.lila.game;

public class MoveGenerator {
	
	public static void generateAllMoves(Board b, MoveList list) {
		generateAllMoves(b, MoveFilter.ALL_MOVES, list);
	}
	
	public static void generateAllMoves(Board b, int filter, MoveList list) {
		generateAllMoves(b, b.getSide(), filter, list);
	}
	
	public static void generateAllMoves(Board b, int side, int filter, MoveList list) {
		int facing = 1;
		int opponentSide = Piece.flipSide(side);
		
		if(side == Piece.WHITE) facing = -1;
		
		long possibleSquares = BitOperations.inverse(b.getBitBoard(side).getValue());
		long occupiedSquares = b.getBitBoard(side).orReturn(b.getBitBoard(opponentSide));
		long emptySquares = BitOperations.inverse(occupiedSquares);
		
		addPawnMoves(b, list, side, filter, opponentSide, facing, emptySquares);
		addKnightMoves(b, list, side, filter, possibleSquares);
		addKingMoves(b, list, side, filter, possibleSquares, occupiedSquares);
		
		addSliderMoves(b, list, side, filter, possibleSquares, occupiedSquares, Piece.BISHOP);
		addSliderMoves(b, list, side, filter, possibleSquares, occupiedSquares, Piece.ROOK);
	}
	
	private static void addPawnMoves(Board b, MoveList list, int side, int filter, int opponentSide, int facing, long emptySquares) {
		long pawnSquares = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.PAWN));
		
		long pawnMoves = pawnSquares;
		
		pawnMoves = BitOperations.shift(pawnMoves, facing * BitOperations.SHIFT_DOWN);
		
		long possiblePawnMoves = pawnMoves & emptySquares;
		
		long possiblePawnMovesAdd = possiblePawnMoves;
		
		while(possiblePawnMovesAdd != 0) {
			int square = BitOperations.bitScanForward(possiblePawnMovesAdd);
			
			addPawnMove(list, side, filter, square - BitOperations.SHIFT_DOWN * facing, square, 0, MoveFlag.NONE);
			
			possiblePawnMovesAdd ^= BitBoards.SINGLE_SQUARE[square];
		}
		
		if(filter == MoveFilter.ALL_MOVES) {
			
			long possiblePawnDoubleMoves = possiblePawnMoves;
			if(side == Piece.WHITE) possiblePawnDoubleMoves &= BitBoards.RANK_3;
			else possiblePawnDoubleMoves &= BitBoards.RANK_6;
			
			possiblePawnDoubleMoves = BitOperations.shift(possiblePawnDoubleMoves, facing * BitOperations.SHIFT_DOWN);
			
			possiblePawnDoubleMoves = possiblePawnDoubleMoves & emptySquares;
			
			while(possiblePawnDoubleMoves != 0) {
				int square = BitOperations.bitScanForward(possiblePawnDoubleMoves);
				
				list.addMove(square - BitOperations.SHIFT_DOWN * 2 * facing, square, 0, 0, MoveFlag.DOUBLE_PAWN_ADVANCE);
				
				possiblePawnDoubleMoves ^= BitBoards.SINGLE_SQUARE[square];
			}
		}
		
		long opponentSquares = b.getBitBoard(opponentSide).getValue();
		
		long pawnAttacksLeft = BitOperations.shift(pawnMoves, BitOperations.SHIFT_LEFT);
		long pawnAttacksRight = BitOperations.shift(pawnMoves, BitOperations.SHIFT_RIGHT);
		
		pawnAttacksLeft &= BitOperations.inverse(BitBoards.FILE_H);
		pawnAttacksRight &= BitOperations.inverse(BitBoards.FILE_A);
		
		if(filter == MoveFilter.ALL_MOVES) {
			int enPassant = b.getEnPassant();
			
			if(enPassant != BoardSquare.NONE) {
				long moveTo = BitBoards.SINGLE_SQUARE[enPassant];
				
				if((pawnAttacksLeft & moveTo) != 0) {
					list.addMove(enPassant - BitOperations.SHIFT_DOWN * facing + 1, enPassant, 0, 0, MoveFlag.EN_PASSANT);
				}
				
				if((pawnAttacksRight & moveTo) != 0) {
					list.addMove(enPassant - BitOperations.SHIFT_DOWN * facing - 1, enPassant, 0, 0, MoveFlag.EN_PASSANT);
				}
			}
		}
		
		pawnAttacksLeft = pawnAttacksLeft & opponentSquares;
		pawnAttacksRight = pawnAttacksRight & opponentSquares;
		
		while(pawnAttacksLeft != 0) {
			int square = BitOperations.bitScanForward(pawnAttacksLeft);
			
			addPawnMove(list, side, filter, square - BitOperations.SHIFT_DOWN * facing + 1, square, b.getPieceType(square), MoveFlag.NONE);
			
			pawnAttacksLeft ^= BitBoards.SINGLE_SQUARE[square];
		}
		
		while(pawnAttacksRight != 0) {
			int square = BitOperations.bitScanForward(pawnAttacksRight);
			
			addPawnMove(list, side, filter, square - BitOperations.SHIFT_DOWN * facing - 1, square, b.getPieceType(square), MoveFlag.NONE);
			
			pawnAttacksRight ^= BitBoards.SINGLE_SQUARE[square];
		}
	}
	
	private static void addPawnMove(MoveList list, int side, int filter, int from, int to, int captured, int flag) {
		int toY = to / 8;
		
		boolean promoted = toY % 7 == 0;
		
		if(promoted) {
			
			list.addMove(from, to, captured, Piece.QUEEN, flag);
			list.addMove(from, to, captured, Piece.KNIGHT, flag);
			list.addMove(from, to, captured, Piece.ROOK, flag);
			list.addMove(from, to, captured, Piece.BISHOP, flag);
			
		} else if(filter == MoveFilter.ALL_MOVES || captured != 0) {
			
			list.addMove(from, to, captured, 0, flag);
		}
	}
	
	private static void addKnightMoves(Board b, MoveList list, int side, int filter, long possibleSquares) {
		long knightSquares = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.KNIGHT));
		
		while(knightSquares != 0) {
			int square = BitOperations.bitScanForward(knightSquares);
			
			long moves = LookupTable.KNIGHT_MOVES[square] & possibleSquares;
			
			while(moves != 0) {
				int to = BitOperations.bitScanForward(moves);
				
				int captured = b.getPieceType(to);
				
				if(filter == MoveFilter.ALL_MOVES || captured != 0) {
					list.addMove(square, to, captured, 0, MoveFlag.NONE);
				}
				
				moves ^= BitBoards.SINGLE_SQUARE[to];
			}
			
			knightSquares ^= BitBoards.SINGLE_SQUARE[square];
		}
	}
	
	private static void addKingMoves(Board b, MoveList list, int side, int filter, long possibleSquares, long occupiedSquares) {
		long kingSquares = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.KING));
		
		int square = BitOperations.bitScanForward(kingSquares);
		
		long moves = LookupTable.KING_MOVES[square] & possibleSquares;
		
		while(moves != 0) {
			int to = BitOperations.bitScanForward(moves);
			
			int captured = b.getPieceType(to);
			
			if(filter == MoveFilter.ALL_MOVES || captured != 0) {
				list.addMove(square, to, captured, 0, MoveFlag.NONE);
			}
			
			moves ^= BitBoards.SINGLE_SQUARE[to];
		}
		
		if(filter == MoveFilter.TACTICAL_MOVES) return;
		
		if(side == Piece.WHITE) {
			
			if((b.getCastlePerms() & Castling.WHITE_QUEEN_SIDE) == 0) {
				addCastlingMove(b, list, side, square, occupiedSquares, -1, MoveFlag.CASTLING_QUEEN_SIDE);
			}
			
			if((b.getCastlePerms() & Castling.WHITE_KING_SIDE) == 0) {
				addCastlingMove(b, list, side, square, occupiedSquares, 1, MoveFlag.CASTLING_KING_SIDE);
			}
			
		} else {
			
			if((b.getCastlePerms() & Castling.BLACK_QUEEN_SIDE) == 0) {
				addCastlingMove(b, list, side, square, occupiedSquares, -1, MoveFlag.CASTLING_QUEEN_SIDE);
			}
			
			if((b.getCastlePerms() & Castling.BLACK_KING_SIDE) == 0) {
				addCastlingMove(b, list, side, square, occupiedSquares, 1, MoveFlag.CASTLING_KING_SIDE);
			}
		}
	}
	
	private static void addCastlingMove(Board b, MoveList list, int side, int from, long occupiedSquares, int dir, int flag) {
		int to = from + dir * 2;
		
		long mask = BitBoards.getRank(from) & BitBoards.getFiles((from + dir) % 8, flag == MoveFlag.CASTLING_QUEEN_SIDE ? 1 : 6);
		
		if((occupiedSquares & mask) != 0) {
			return;
		}
		
		for(int i = 0; i < 3; i++) {
			int square = from + dir * i;
			
			if(b.isUnderAttack(square, side)) {
				return;
			}
		}
		
		list.addMove(from, to, 0, 0, flag);
	}
	
	private static void addSliderMoves(Board b, MoveList list, int side, int filter, long possibleSquares, long occupiedSquares, int type) {
		long squares = b.getBitBoard(side).andReturn(b.getBitBoard(type).orReturn(b.getBitBoard(Piece.QUEEN)));
		
		while(squares != 0) {
			int square = BitOperations.bitScanForward(squares);
			
			long moves = LookupTable.getSliderMoves(square, occupiedSquares, type);
			
			moves = moves & possibleSquares;
			
			while(moves != 0) {
				int to = BitOperations.bitScanForward(moves);
				
				int captured = b.getPieceType(to);
				
				if(filter == MoveFilter.ALL_MOVES || captured != 0) {
					list.addMove(square, to, captured, 0, MoveFlag.NONE);
				}
				
				moves ^= BitBoards.SINGLE_SQUARE[to];
			}
			
			squares ^= BitBoards.SINGLE_SQUARE[square];
		}
	}
	
}
