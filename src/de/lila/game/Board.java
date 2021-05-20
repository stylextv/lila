package de.lila.game;

public class Board {
	
	private BitBoard[] bitBoards = new BitBoard[Piece.LAST + 1];
	
	private int[] pieces = new int[BoardConstants.BOARD_SIZE_SQ];
	
	private int[][] pieceSquares = new int[12][11];
	
	private int[] pieceCounters = new int[12];
	
	private boolean countedPieces;
	
	private long[] attackedSquares = new long[12];
	
	private boolean calculatedAttacks;
	
	private int side;
	
	private int historyPly;
	
	private int fiftyMoveCounter;
	
	private int castlePerms;
	
	private int enPassant;
	
	private long positionKey;
	
	private UndoStructure[] history = new UndoStructure[BoardConstants.MAX_GAME_MOVES];
	
	public Board() {
		for(int i = 0; i < bitBoards.length; i++) {
			bitBoards[i] = new BitBoard();
		}
		
		for(int i = 0; i < history.length; i++) {
			history[i] = new UndoStructure();
		}
		
		reset();
	}
	
	public void reset() {
		parseFen(BoardConstants.STARTING_POSITION);
		
//		System.out.println("final eval: " + Evaluator.eval(this, Piece.WHITE));
		
//		System.out.println("---");
//		System.out.println(Evaluator.evalPawnStructure(this, Piece.WHITE, true));
//		System.out.println("---");
//		System.out.println(Evaluator.evalPawnStructure(this, Piece.BLACK, true));
	}
	
	public void parseFen(String fen) {
		String[] split = fen.split(" ");
		
		for(int i=0; i<bitBoards.length; i++) {
			bitBoards[i].clear();
		}
		
		int square = 0;
		
		char[] piecePosition = split[0].replace("/", "").toCharArray();
		
		for(char ch : piecePosition) {
			int p = Piece.getPieceFromFenNotation(ch);
			
			if(p == Piece.NO_PIECE) {
				int a = Integer.parseInt("" + ch);
				
				for(int i=0; i<a; i++) {
					pieces[square] = Piece.NO_PIECE;
					
					square++;
				}
			} else {
				int side = Piece.getSideOfPiece(p);
				int type = Piece.getTypeOfPiece(p);
				
				long key = BitBoard.SINGLE_SQUARE[square];
				
				bitBoards[side].xor(key);
				bitBoards[type].xor(key);
				
				pieces[square] = p;
				
				square++;
			}
		}
		
		countedPieces = false;
		
		calculatedAttacks = false;
		
		if(split[1].equals("w")) side = Piece.WHITE;
		else side = Piece.BLACK;
		
		String castling = split[2];
		
		if(castling.equals("-")) {
			
			castlePerms = Castling.BOTH;
			
		} else {
			
			castlePerms = 0;
			
			if(!castling.contains("K")) castlePerms ^= Castling.WHITE_KING_SIDE;
			if(!castling.contains("Q")) castlePerms ^= Castling.WHITE_QUEEN_SIDE;
			if(!castling.contains("k")) castlePerms ^= Castling.BLACK_KING_SIDE;
			if(!castling.contains("q")) castlePerms ^= Castling.BLACK_QUEEN_SIDE;
		}
		
		String enPassantSquare = split[3];
		
		if(enPassantSquare.equals("-")) {
			enPassant = BoardSquare.NONE;
		} else {
			enPassant = BoardSquare.getSquareFromNotation(enPassantSquare);
		}
		
		fiftyMoveCounter = Integer.parseInt(split[4]);
		
		int ply = Integer.parseInt(split[5]);
		
		ply = (ply - 1) * 2;
		
		if(side == Piece.BLACK) ply++;
		
		historyPly = ply;
		
		positionKey = PositionKey.generatePositionKeySlow(this);
	}
	
	public String getFen() {
		String fen = "";
		
		for(int y = 0; y < 8; y++) {
			int emptySquares = 0;
			
			for(int x = 0; x < 8; x++) {
				int p = pieces[y * 8 +x];
				
				if(p == Piece.NO_PIECE) {
					emptySquares++;
				} else {
					if(emptySquares != 0) {
						fen += emptySquares;
						
						emptySquares = 0;
					}
					
					fen += Piece.getFenNotation(p);
				}
			}
			
			if(emptySquares != 0) {
				fen += emptySquares;
			}
			
			if(y != 7) fen = fen + "/";
		}
		
		if(side == Piece.WHITE) fen += " w";
		else fen += " b";
		
		String castling = " ";
		
		if((castlePerms & Castling.WHITE_KING_SIDE) == 0) castling += "K";
		if((castlePerms & Castling.WHITE_QUEEN_SIDE) == 0) castling += "Q";
		if((castlePerms & Castling.BLACK_KING_SIDE) == 0) castling += "k";
		if((castlePerms & Castling.BLACK_QUEEN_SIDE) == 0) castling += "q";
		
		if(castling.length() == 1) castling += "-";
		
		fen += castling;
		
		if(enPassant == BoardSquare.NONE) {
			fen += " -";
		} else {
			fen += " " + BoardSquare.getNotation(enPassant);
		}
		
		fen += " " + fiftyMoveCounter;
		fen += " " + (1 + historyPly / 2);
		
		return fen;
	}
	
	public void print() {
		System.out.println("\n +---+---+---+---+---+---+---+---+");
		
		for(int y = 0; y < 8; y++) {
			String line = " | ";
			
			for(int x = 0; x < 8; x++) {
				char ch = ' ';
				
				int p = pieces[y * 8 + x];
				
				if(p != Piece.NO_PIECE) ch = Piece.getFenNotation(p);
				
				line = line + ch + " | ";
			}
			
			System.out.println(line + (8 - y));
			
			System.out.println(" +---+---+---+---+---+---+---+---+");
		}
		
		System.out.println("   a   b   c   d   e   f   g   h\n");
		
		System.out.println("Fen: " + getFen());
		System.out.println("Key: " + positionKey);
	}
	
	public int getPiece(int square) {
		return pieces[square];
	}
	
	public int getPieceType(int square) {
		int p = pieces[square];
		
		if(p == Piece.NO_PIECE) return 0;
		
		return Piece.getTypeOfPiece(p);
	}
	
	private void clearSquare(int index, int side, int type) {
		long key = BitBoard.SINGLE_SQUARE[index];
		
		bitBoards[side].xor(key);
		bitBoards[type].xor(key);
		
		positionKey ^= PositionKey.getRandomNumber(pieces[index] * 64 + index);
		
		pieces[index] = Piece.NO_PIECE;
	}
	
	private void setPiece(int index, int side, int type) {
		long key = BitBoard.SINGLE_SQUARE[index];
		
		bitBoards[side].xor(key);
		bitBoards[type].xor(key);
		
		int p = Piece.getPiece(side, type);
		
		pieces[index] = p;
		
		positionKey ^= PositionKey.getRandomNumber(p * 64 + index);
	}
	
	public void makeMove(Move m) {
		UndoStructure u = history[historyPly];
		
		u.setPositionKey(positionKey);
		
		int opponentSide = Piece.flipSide(side);
		
		int originalPiece = getPieceType(m.getFrom());
		int placedPiece = originalPiece;
		
		if(m.getPromoted() != 0) placedPiece = m.getPromoted();
		
		if(m.getCaptured() != 0) clearSquare(m.getTo(), opponentSide, m.getCaptured());
		setPiece(m.getTo(), side, placedPiece);
		clearSquare(m.getFrom(), side, originalPiece);
		
		if(m.getFlag() == MoveFlag.EN_PASSANT) {
			int target = enPassant;
			
			if(side == Piece.WHITE) target += 8;
			else target -= 8;
			
			clearSquare(target, opponentSide, Piece.PAWN);
		}
		
		u.setFiftyMoveCounter(fiftyMoveCounter);
		u.setCastlePerms(castlePerms);
		u.setEnPassant(enPassant);
		
		historyPly++;
		
		if(originalPiece == Piece.PAWN || m.getCaptured() != 0) fiftyMoveCounter = 0;
		else fiftyMoveCounter++;
		
		if(enPassant != BoardSquare.NONE) {
			positionKey ^= PositionKey.getRandomNumber(PositionKey.EN_PASSANT_OFFSET + enPassant % 8);
		}
		
		if(m.getFlag() == MoveFlag.DOUBLE_PAWN_ADVANCE) {
			enPassant = (m.getFrom() + m.getTo()) / 2;
			
			positionKey ^= PositionKey.getRandomNumber(PositionKey.EN_PASSANT_OFFSET + enPassant % 8);
		} else {
			enPassant = BoardSquare.NONE;
		}
		
		if(m.getFlag() == MoveFlag.CASTLING_QUEEN_SIDE || m.getFlag() == MoveFlag.CASTLING_KING_SIDE) {
			
			removeCastlePerms(side);
			
			int y = m.getFrom() / 8;
			
			int rookFrom = y * 8;
			int rookTo = m.getTo();
			
			if(m.getFlag() == MoveFlag.CASTLING_KING_SIDE) {
				rookFrom += 7;
				
				rookTo -= 1;
			} else {
				rookTo += 1;
			}
			
			setPiece(rookTo, side, Piece.ROOK);
			clearSquare(rookFrom, side, Piece.ROOK);
			
		} else if(castlePerms != Castling.BOTH) {
			
			if(m.getFrom() == BoardConstants.KING_START_POSITION[side]) {
				removeCastlePerms(side);
			}
			
			updateCastlePerms(Piece.WHITE, m.getFrom(), m.getTo());
			updateCastlePerms(Piece.BLACK, m.getFrom(), m.getTo());
		}
		
		countedPieces = false;
		
		calculatedAttacks = false;
		
		side = opponentSide;
		positionKey ^= PositionKey.getRandomNumber(PositionKey.SIDE_OFFSET);
	}
	
	public void undoMove(Move m) {
		int opponentSide = side;
		
		side = Piece.flipSide(side);
		
		int originalPiece = getPieceType(m.getTo());
		int placedPiece = originalPiece;
		
		if(m.getPromoted() != 0) placedPiece = Piece.PAWN;
		
		setPiece(m.getFrom(), side, placedPiece);
		clearSquare(m.getTo(), side, originalPiece);
		
		int captured = m.getCaptured();
		if(captured != 0) setPiece(m.getTo(), opponentSide, captured);
		
		historyPly--;
		
		UndoStructure u = history[historyPly];
		
		fiftyMoveCounter = u.getFiftyMoveCounter();
		
		castlePerms = u.getCastlePerms();
		
		enPassant = u.getEnPassant();
		
		if(m.getFlag() == MoveFlag.EN_PASSANT) {
			int target = enPassant;
			
			if(side == Piece.WHITE) target += 8;
			else target -= 8;
			
			setPiece(target, opponentSide, Piece.PAWN);
		}
		
		if(m.getFlag() == MoveFlag.CASTLING_QUEEN_SIDE || m.getFlag() == MoveFlag.CASTLING_KING_SIDE) {
			
			int y = m.getFrom() / 8;
			
			int rookFrom = y * 8;
			int rookTo = m.getTo();
			
			if(m.getFlag() == MoveFlag.CASTLING_KING_SIDE) {
				rookFrom += 7;
				
				rookTo -= 1;
			} else {
				rookTo += 1;
			}
			
			setPiece(rookFrom, side, Piece.ROOK);
			clearSquare(rookTo, side, Piece.ROOK);
		}
		
		countedPieces = false;
		
		calculatedAttacks = false;
		
		positionKey = u.getPositionKey();
	}
	
	public void makeNullMove() {
		UndoStructure u = history[historyPly];
		
		u.setPositionKey(positionKey);
		
		int opponentSide = Piece.flipSide(side);
		
		u.setFiftyMoveCounter(fiftyMoveCounter);
		u.setCastlePerms(castlePerms);
		u.setEnPassant(enPassant);
		
		historyPly++;
		
		if(enPassant != BoardSquare.NONE) {
			positionKey ^= PositionKey.getRandomNumber(PositionKey.EN_PASSANT_OFFSET + enPassant % 8);
		}
		
		enPassant = BoardSquare.NONE;
		
		side = opponentSide;
		positionKey ^= PositionKey.getRandomNumber(PositionKey.SIDE_OFFSET);
	}
	
	public void undoNullMove() {
		side = Piece.flipSide(side);
		
		historyPly--;
		
		UndoStructure u = history[historyPly];
		
		fiftyMoveCounter = u.getFiftyMoveCounter();
		
		castlePerms = u.getCastlePerms();
		
		enPassant = u.getEnPassant();
		
		positionKey = u.getPositionKey();
	}
	
	public boolean isSideInCheck() {
		return isInCheck(side);
	}
	
	public boolean isOpponentInCheck() {
		return isInCheck(Piece.flipSide(side));
	}
	
	private boolean isInCheck(int side) {
		long kingSquares = getBitBoard(side).andReturn(getBitBoard(Piece.KING));
		
		int square = BitOperations.bitScanForward(kingSquares);
		
		return isUnderAttack(square, side);
	}
	
	public boolean isUnderAttack(int square, int defenderSide) {
		long mask = BitBoard.SINGLE_SQUARE[square];
		
		return (attackedBy(Piece.flipSide(defenderSide), Piece.ALL_PIECES) & mask) != 0;
	}
	
	private void countPieces() {
		if(countedPieces) return;
		
		for(int i=0; i<pieceCounters.length; i++) {
			pieceCounters[i] = 0;
		}
		
		for(int i=0; i<64; i++) {
			int p = pieces[i];
			
			if(p != Piece.NO_PIECE) {
				int l = pieceCounters[p];
				
				pieceSquares[p][l] = i;
				
				pieceCounters[p] = l + 1;
			}
		}
		
		countedPieces = true;
	}
	
	public int getPieceAmount(int side, int type) {
		if(side == Piece.BOTH_SIDES) return getPieceAmount(Piece.WHITE, type) + getPieceAmount(Piece.BLACK, type);
		
		if(type == Piece.BISHOP_PAIR) return getPieceAmount(side, Piece.BISHOP) > 1 ? 1 : 0;
		
		if(type != Piece.ALL_PIECES) return getPieceAmount(Piece.getPiece(side, type));
		
		int count = 0;
		
		for(int i = Piece.PAWN; i <= Piece.KING; i++) {
			count += getPieceAmount(Piece.getPiece(side, i));
		}
		
		return count;
	}
	
	public int getPieceAmount(int p) {
		countPieces();
		
		return pieceCounters[p];
	}
	
	public int getPieceSquare(int p, int i) {
		countPieces();
		
		return pieceSquares[p][i];
	}
	
	private void calculateAttacks() {
		if(calculatedAttacks) return;
		
		long occupiedSquares = getBitBoard(Piece.WHITE).orReturn(getBitBoard(Piece.BLACK));
		
		calculatePawnAttacks(Piece.WHITE, BitOperations.SHIFT_UP);
		calculatePawnAttacks(Piece.BLACK, BitOperations.SHIFT_DOWN);
		
		calculateKnightAttacks(Piece.WHITE);
		calculateKnightAttacks(Piece.BLACK);
		
		calculateBishopAttacks(Piece.WHITE, occupiedSquares);
		calculateBishopAttacks(Piece.BLACK, occupiedSquares);
		
		calculateRookAttacks(Piece.WHITE, occupiedSquares);
		calculateRookAttacks(Piece.BLACK, occupiedSquares);
		
		calculateQueenAttacks(Piece.WHITE, occupiedSquares);
		calculateQueenAttacks(Piece.BLACK, occupiedSquares);
		
		calculateKingAttacks(Piece.WHITE);
		calculateKingAttacks(Piece.BLACK);
		
		calculatedAttacks = true;
	}
	
	private void calculatePawnAttacks(int side, int dir) {
		int p = Piece.getPiece(side, Piece.PAWN);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(Piece.PAWN));
		
		squares = BitOperations.shift(squares, dir);
		
		long attacksLeft = BitOperations.shift(squares, BitOperations.SHIFT_LEFT);
		long attacksRight = BitOperations.shift(squares, BitOperations.SHIFT_RIGHT);
		
		attacksLeft &= BitOperations.inverse(BitBoard.FILE_H);
		attacksRight &= BitOperations.inverse(BitBoard.FILE_A);
		
		attackedSquares[p] = attacksLeft | attacksRight;
	}
	
	private void calculateKnightAttacks(int side) {
		int p = Piece.getPiece(side, Piece.KNIGHT);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(Piece.KNIGHT));
		
		long attacks = 0;
		
		while(squares != 0) {
			int square = BitOperations.bitScanForward(squares);
			
			long moves = LookupTable.KNIGHT_MOVES[square];
			
			attacks |= moves;
			
			squares ^= BitBoard.SINGLE_SQUARE[square];
		}
		
		attackedSquares[p] = attacks;
	}
	
	private void calculateBishopAttacks(int side, long occupiedSquares) {
		int p = Piece.getPiece(side, Piece.BISHOP);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(Piece.BISHOP));
		
		long attacks = 0;
		
		while(squares != 0) {
			int square = BitOperations.bitScanForward(squares);
			
			long moves = LookupTable.getSliderMoves(square, occupiedSquares, Piece.BISHOP);
			
			attacks |= moves;
			
			squares ^= BitBoard.SINGLE_SQUARE[square];
		}
		
		attackedSquares[p] = attacks;
	}
	
	private void calculateRookAttacks(int side, long occupiedSquares) {
		int p = Piece.getPiece(side, Piece.ROOK);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(Piece.ROOK));
		
		long attacks = 0;
		
		while(squares != 0) {
			int square = BitOperations.bitScanForward(squares);
			
			long moves = LookupTable.getSliderMoves(square, occupiedSquares, Piece.ROOK);
			
			attacks |= moves;
			
			squares ^= BitBoard.SINGLE_SQUARE[square];
		}
		
		attackedSquares[p] = attacks;
	}
	
	private void calculateQueenAttacks(int side, long occupiedSquares) {
		int p = Piece.getPiece(side, Piece.QUEEN);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(Piece.QUEEN));
		
		long attacks = 0;
		
		while(squares != 0) {
			int square = BitOperations.bitScanForward(squares);
			
			long moves = LookupTable.getSliderMoves(square, occupiedSquares, Piece.ROOK);
			
			moves |= LookupTable.getSliderMoves(square, occupiedSquares, Piece.BISHOP);
			
			attacks |= moves;
			
			squares ^= BitBoard.SINGLE_SQUARE[square];
		}
		
		attackedSquares[p] = attacks;
	}
	
	private void calculateKingAttacks(int side) {
		int p = Piece.getPiece(side, Piece.KING);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(Piece.KING));
		
		int square = BitOperations.bitScanForward(squares);
		
		long moves = LookupTable.KING_MOVES[square];
		
		attackedSquares[p] = moves;
	}
	
	public long attackedBy(int side, int type) {
		if(side == Piece.BOTH_SIDES) return attackedBy(Piece.WHITE, type) | attackedBy(Piece.BLACK, type);
		
		if(type != Piece.ALL_PIECES) return attackedBy(Piece.getPiece(side, type));
		
		long attacks = 0;
		
		for(int i = Piece.PAWN; i <= Piece.KING; i++) {
			attacks |= attackedBy(Piece.getPiece(side, i));
		}
		
		return attacks;
	}
	
	public long attackedBy(int p) {
		calculateAttacks();
		
		return attackedSquares[p];
	}
	
	public int getEndgameWeight() {
		int knightPhase = 1;
		int bishopPhase = 1;
		int rookPhase = 2;
		int queenPhase = 4;
		
		int totalPhase = knightPhase * 4 + bishopPhase * 4 + rookPhase * 4 + queenPhase * 2;
		
		int phase = totalPhase;
		
		phase -= getPieceAmount(Piece.getPiece(Piece.WHITE, Piece.KNIGHT)) * knightPhase;
		phase -= getPieceAmount(Piece.getPiece(Piece.WHITE, Piece.BISHOP)) * bishopPhase;
		phase -= getPieceAmount(Piece.getPiece(Piece.WHITE, Piece.ROOK)) * rookPhase;
		phase -= getPieceAmount(Piece.getPiece(Piece.WHITE, Piece.QUEEN)) * queenPhase;
		
		phase -= getPieceAmount(Piece.getPiece(Piece.BLACK, Piece.KNIGHT)) * knightPhase;
		phase -= getPieceAmount(Piece.getPiece(Piece.BLACK, Piece.BISHOP)) * bishopPhase;
		phase -= getPieceAmount(Piece.getPiece(Piece.BLACK, Piece.ROOK)) * rookPhase;
		phase -= getPieceAmount(Piece.getPiece(Piece.BLACK, Piece.QUEEN)) * queenPhase;
		
		return (phase * 256 + (totalPhase / 2)) / totalPhase;
	}
	
	private void removeCastlePerms(int side) {
		if(side == Piece.WHITE) {
			if((castlePerms & Castling.WHITE_KING_SIDE) == 0) positionKey ^= PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET);
			if((castlePerms & Castling.WHITE_QUEEN_SIDE) == 0) positionKey ^= PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET + 1);
			
			castlePerms |= Castling.WHITE;
		} else {
			if((castlePerms & Castling.BLACK_KING_SIDE) == 0) positionKey ^= PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET + 2);
			if((castlePerms & Castling.BLACK_QUEEN_SIDE) == 0) positionKey ^= PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET + 3);
			
			castlePerms |= Castling.BLACK;
		}
	}
	
	private void updateCastlePerms(int side, int from, int to) {
		updateCastlePerms(side, from, to, 0);
		updateCastlePerms(side, from, to, 1);
	}
	
	private void updateCastlePerms(int side, int from, int to, int rookIndex) {
		int square;
		
		if(rookIndex == 0) square = BoardConstants.LEFT_ROOK_START_POSITION[side];
		else square = BoardConstants.RIGHT_ROOK_START_POSITION[side];
		
		if(from == square || to == square) {
			int mask;
			long key;
			
			if(side == Piece.WHITE) {
				if(rookIndex == 0) {
					mask = Castling.WHITE_QUEEN_SIDE;
					key = PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET + 1);
				} else {
					mask = Castling.WHITE_KING_SIDE;
					key = PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET);
				}
			} else {
				if(rookIndex == 0) {
					mask = Castling.BLACK_QUEEN_SIDE;
					key = PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET + 3);
				} else {
					mask = Castling.BLACK_KING_SIDE;
					key = PositionKey.getRandomNumber(PositionKey.CASTLING_OFFSET + 2);
				}
			}
			
			if((castlePerms & mask) == 0) {
				positionKey ^= key;
				
				castlePerms |= mask;
			}
		}
	}
	
	public boolean hasThreefoldRepetition() {
		int start = historyPly - 2;
		
		int count = 0;
		
		for(int i = start; i >= 0; i -= 2) {
			UndoStructure previous = history[i];
			
			if(previous.getPositionKey() == positionKey) {
				count++;
				
				if(count == 2) return true;
			} else if(previous.getCastlePerms() != castlePerms || previous.getFiftyMoveCounter() >= fiftyMoveCounter) {
				break;
			}
		}
		
		return false;
	}
	
	public int findWinner(boolean hasLegalMoves) {
		return findWinner(hasLegalMoves, isSideInCheck());
	}
	
	public int findWinner(boolean hasLegalMoves, boolean inCheck) {
		if(hasLegalMoves) {
			if(fiftyMoveCounter == 100 || hasThreefoldRepetition()) return Winner.DRAW;
			
			return Winner.NONE;
		}
		
		if(!inCheck) {
			return Winner.DRAW;
		}
		
		return Piece.flipSide(side);
	}
	
	public int getSide() {
		return side;
	}
	
	public int getHistoryPly() {
		return historyPly;
	}
	
	public int getFiftyMoveCounter() {
		return fiftyMoveCounter;
	}
	
	public int getCastlePerms() {
		return castlePerms;
	}
	
	public int getEnPassant() {
		return enPassant;
	}
	
	public long getPositionKey() {
		return positionKey;
	}
	
	public BitBoard getBitBoard(int i) {
		return bitBoards[i];
	}
	
	public int[] getPieces() {
		return pieces;
	}
	
}
