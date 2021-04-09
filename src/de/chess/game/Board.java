package de.chess.game;

import de.chess.ai.OpeningBook;
import de.chess.ai.OpeningPosition;
import de.chess.ai.Search;
import de.chess.ui.WidgetUI;

public class Board {
	
	// sebastian pawn endgame: 8/k7/3p4/p2P1p2/P2P1P2/8/8/K7 w - - 0 1
	// crazy karpov move: r1bqk2r/pp3pp1/2pbpn1p/8/3P3Q/3B1N2/PPP2PPP/R1B1K2R b KQkq - 0 1
	
	private static final String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	
	private BitBoard[] bitBoards = new BitBoard[PieceCode.LAST + 1];
	
	private int[] pieces = new int[BoardConstants.BOARD_SIZE_SQ];
	
	private int[][] pieceIndices = new int[12][11];
	
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
		for(int i=0; i<bitBoards.length; i++) {
			bitBoards[i] = new BitBoard();
		}
		
		for(int i=0; i<history.length; i++) {
			history[i] = new UndoStructure();
		}
		
		reset();
	}
	
	public void reset() {
		parseFen(STARTING_POSITION);
		
//		LookupTable.initTables();
		
//		System.out.println("---");
//		System.out.println(Evaluator.evalMobility(this, PieceCode.WHITE, Evaluator.MOBILITY_BONUS_MG));
//		System.out.println("---");
//		System.out.println(Evaluator.evalMobility(this, PieceCode.BLACK, Evaluator.MOBILITY_BONUS_MG));
		
//		System.out.println(Evaluator.evalTotalImbalance(this, false));
	}
	
	public void parseFen(String fen) {
		String[] split = fen.split(" ");
		
		for(int i=0; i<bitBoards.length; i++) {
			bitBoards[i].clear();
		}
		
		int square = 0;
		
		char[] piecePosition = split[0].replace("/", "").toCharArray();
		
		for(char ch : piecePosition) {
			int code = PieceCode.getCodeFromFenNotation(ch);
			
			if(code == -1) {
				int a = Integer.parseInt("" + ch);
				
				for(int i=0; i<a; i++) {
					pieces[square] = -1;
					
					square++;
				}
			} else {
				int color = PieceCode.getColorFromSpriteCode(code);
				int type = PieceCode.getTypeFromSpriteCode(code);
				
				long key = BoardConstants.BIT_SET[square];
				
				bitBoards[color].xor(key);
				bitBoards[type].xor(key);
				
				pieces[square] = code;
				
				square++;
			}
		}
		
		countedPieces = false;
		
		calculatedAttacks = false;
		
		if(split[1].equals("w")) side = PieceCode.WHITE;
		else side = PieceCode.BLACK;
		
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
			enPassant = BoardSquare.getIndexFromNotation(enPassantSquare);
		}
		
		fiftyMoveCounter = Integer.parseInt(split[4]);
		
		int ply = Integer.parseInt(split[5]);
		
		ply = (ply - 1) * 2;
		
		if(side == PieceCode.BLACK) ply++;
		
		historyPly = ply;
		
		positionKey = PositionKey.generatePositionKeySlow(this);
	}
	
	public String getFen() {
		String fen = "";
		
		for(int y=0; y<8; y++) {
			int emptySquares = 0;
			
			for(int x=0; x<8; x++) {
				int code = pieces[y * 8 +x];
				
				if(code == -1) {
					emptySquares++;
				} else {
					if(emptySquares != 0) {
						fen += emptySquares;
						
						emptySquares = 0;
					}
					
					fen += PieceCode.getFenNotation(code);
				}
			}
			
			if(emptySquares != 0) {
				fen += emptySquares;
				
				emptySquares = 0;
			}
			
			if(y != 7) fen = fen + "/";
		}
		
		if(side == PieceCode.WHITE) fen += " w";
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
			fen += " " + BoardSquare.getSquareNotation(enPassant);
		}
		
		fen += " " + fiftyMoveCounter;
		fen += " " + (1 + historyPly / 2);
		
		return fen;
	}
	
	public int getPiece(int index) {
		return pieces[index];
	}
	
	public int getPieceType(int index) {
		int i = pieces[index];
		
		if(i == -1) return 0;
		
		return PieceCode.getTypeFromSpriteCode(i);
	}
	
	private void clearSquare(int index, int side, int type) {
		long key = BoardConstants.BIT_SET[index];
		
		bitBoards[side].xor(key);
		bitBoards[type].xor(key);
		
		positionKey ^= PositionKey.getRandomNumber(pieces[index] * 64 + index);
		positionKey ^= PositionKey.getRandomNumber(PositionKey.NOTHING_OFFSET + index);
		
		pieces[index] = -1;
	}
	
	private void setPiece(int index, int side, int type) {
		long key = BoardConstants.BIT_SET[index];
		
		bitBoards[side].xor(key);
		bitBoards[type].xor(key);
		
		int code = PieceCode.getSpriteCode(side, type);
		
		pieces[index] = code;
		
		positionKey ^= PositionKey.getRandomNumber(PositionKey.NOTHING_OFFSET + index);
		positionKey ^= PositionKey.getRandomNumber(code * 64 + index);
	}
	
	public void makeMove(Move m) {
		UndoStructure u = history[historyPly];
		
		u.setPositionKey(positionKey);
		
		int opponentSide = (side + 1) % 2;
		
		int originalPiece = getPieceType(m.getFrom());
		int placedPiece = originalPiece;
		
		if(m.getPromoted() != 0) placedPiece = m.getPromoted();
		
		if(m.getCaptured() != 0) clearSquare(m.getTo(), opponentSide, m.getCaptured());
		setPiece(m.getTo(), side, placedPiece);
		clearSquare(m.getFrom(), side, originalPiece);
		
		if(m.getFlag() == MoveFlag.EN_PASSANT) {
			int target = enPassant;
			
			if(side == PieceCode.WHITE) target += 8;
			else target -= 8;
			
			clearSquare(target, opponentSide, PieceCode.PAWN);
		}
		
		u.setFiftyMoveCounter(fiftyMoveCounter);
		u.setCastlePerms(castlePerms);
		u.setEnPassant(enPassant);
		
		historyPly++;
		
		if(originalPiece == PieceCode.PAWN || m.getCaptured() != 0) fiftyMoveCounter = 0;
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
			
			setPiece(rookTo, side, PieceCode.ROOK);
			clearSquare(rookFrom, side, PieceCode.ROOK);
			
		} else if(castlePerms != Castling.BOTH) {
			
			if(m.getFrom() == MoveGenerator.KING_START_POSITION[side]) {
				removeCastlePerms(side);
			}
			
			updateCastlePerms(PieceCode.WHITE, m.getFrom(), m.getTo());
			updateCastlePerms(PieceCode.BLACK, m.getFrom(), m.getTo());
		}
		
		countedPieces = false;
		
		calculatedAttacks = false;
		
		side = opponentSide;
		positionKey ^= PositionKey.getRandomNumber(PositionKey.SIDE_OFFSET);
	}
	
	public void undoMove(Move m) {
		int opponentSide = side;
		
		side = (side + 1) % 2;
		
		int originalPiece = getPieceType(m.getTo());
		int placedPiece = originalPiece;
		
		if(m.getPromoted() != 0) placedPiece = PieceCode.PAWN;
		
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
			
			if(side == PieceCode.WHITE) target += 8;
			else target -= 8;
			
			setPiece(target, opponentSide, PieceCode.PAWN);
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
			
			setPiece(rookFrom, side, PieceCode.ROOK);
			clearSquare(rookTo, side, PieceCode.ROOK);
		}
		
		countedPieces = false;
		
		calculatedAttacks = false;
		
		positionKey = u.getPositionKey();
	}
	
	public void makeNullMove() {
		UndoStructure u = history[historyPly];
		
		u.setPositionKey(positionKey);
		
		int opponentSide = (side + 1) % 2;
		
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
		side = (side + 1) % 2;
		
		historyPly--;
		
		UndoStructure u = history[historyPly];
		
		fiftyMoveCounter = u.getFiftyMoveCounter();
		
		castlePerms = u.getCastlePerms();
		
		enPassant = u.getEnPassant();
		
		positionKey = u.getPositionKey();
	}
	
	public Move makeAIMove() {
		OpeningPosition p = OpeningBook.getOpeningPosition(positionKey);
		
		if(p != null) {
			
			int hash = p.getRandomMove();
			
			Move m = null;
			
			MoveList list = new MoveList();
			
			MoveGenerator.generateAllMoves(this, list);
			
			for(int i=0; i<list.getCount(); i++) {
				Move move = list.getMove(i);
				
				if(move.getHash() == hash) {
					m = move;
					break;
				}
			}
			
			System.out.println("------");
			System.out.println("played move from book: "+m);
			
			WidgetUI.setPrediction(0);
			WidgetUI.addToEvalHistory(0);
			
			makeMove(m);
			
			return m;
		}
		
		Move m = Search.findNextMove(this);
		
		makeMove(m);
		
		return m;
	}
	
	public boolean isSideInCheck() {
		return isInCheck(side);
	}
	
	public boolean isOpponentInCheck() {
		return isInCheck((side + 1) % 2);
	}
	
	private boolean isInCheck(int side) {
		long kingSquares = getBitBoard(side).andReturn(getBitBoard(PieceCode.KING));
		
		int square = BitOperations.bitScanForward(kingSquares);
		
		return isUnderAttack(square, side);
	}
	
	public boolean isUnderAttack(int square, int defenderSide) {
		long mask = BoardConstants.BIT_SET[square];
		
		return (attackedBy((defenderSide + 1) % 2, PieceCode.ALL_PIECES) & mask) != 0;
	}
	
	private void countPieces() {
		if(countedPieces) return;
		
		for(int i=0; i<pieceCounters.length; i++) {
			pieceCounters[i] = 0;
		}
		
		for(int i=0; i<64; i++) {
			int code = pieces[i];
			
			if(code != -1) {
				int l = pieceCounters[code];
				
				pieceIndices[code][l] = i;
				
				pieceCounters[code] = l + 1;
			}
		}
		
		countedPieces = true;
	}
	
	public int getPieceAmount(int side, int type) {
		if(side == PieceCode.BOTH_SIDES) return getPieceAmount(PieceCode.WHITE, type) + getPieceAmount(PieceCode.BLACK, type);
		
		if(type == PieceCode.BISHOP_PAIR) return getPieceAmount(side, PieceCode.BISHOP) > 1 ? 1 : 0;
		
		if(type != PieceCode.ALL_PIECES) return getPieceAmount(PieceCode.getSpriteCode(side, type));
		
		int count = 0;
		
		for(int i = PieceCode.PAWN; i <= PieceCode.KING; i++) {
			count += getPieceAmount(PieceCode.getSpriteCode(side, i));
		}
		
		return count;
	}
	
	public int getPieceAmount(int code) {
		countPieces();
		
		return pieceCounters[code];
	}
	
	public int getPieceIndex(int code, int i) {
		countPieces();
		
		return pieceIndices[code][i];
	}
	
	private void calculateAttacks() {
		if(calculatedAttacks) return;
		
		long occupiedSquares = getBitBoard(PieceCode.WHITE).orReturn(getBitBoard(PieceCode.BLACK));
		
		calculatePawnAttacks(PieceCode.WHITE, BitOperations.SHIFT_UP);
		calculatePawnAttacks(PieceCode.BLACK, BitOperations.SHIFT_DOWN);
		
		calculateKnightAttacks(PieceCode.WHITE);
		calculateKnightAttacks(PieceCode.BLACK);
		
		calculateBishopAttacks(PieceCode.WHITE, occupiedSquares);
		calculateBishopAttacks(PieceCode.BLACK, occupiedSquares);
		
		calculateRookAttacks(PieceCode.WHITE, occupiedSquares);
		calculateRookAttacks(PieceCode.BLACK, occupiedSquares);
		
		calculateQueenAttacks(PieceCode.WHITE, occupiedSquares);
		calculateQueenAttacks(PieceCode.BLACK, occupiedSquares);
		
		calculateKingAttacks(PieceCode.WHITE);
		calculateKingAttacks(PieceCode.BLACK);
		
		calculatedAttacks = true;
	}
	
	private void calculatePawnAttacks(int side, int dir) {
		int code = PieceCode.getSpriteCode(side, PieceCode.PAWN);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(PieceCode.PAWN));
		
		squares = BitOperations.shift(squares, dir);
		
		long attacksLeft = BitOperations.shift(squares, BitOperations.SHIFT_LEFT);
		long attacksRight = BitOperations.shift(squares, BitOperations.SHIFT_RIGHT);
		
		attacksLeft &= BitOperations.inverse(BitBoard.FILE_H);
		attacksRight &= BitOperations.inverse(BitBoard.FILE_A);
		
		attackedSquares[code] = attacksLeft | attacksRight;
	}
	
	private void calculateKnightAttacks(int side) {
		int code = PieceCode.getSpriteCode(side, PieceCode.KNIGHT);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(PieceCode.KNIGHT));
		
		long attacks = 0;
		
		while(squares != 0) {
			int square = BitOperations.bitScanForward(squares);
			
			long moves = LookupTable.KNIGHT_MOVES[square];
			
			attacks |= moves;
			
			squares ^= BoardConstants.BIT_SET[square];
		}
		
		attackedSquares[code] = attacks;
	}
	
	private void calculateBishopAttacks(int side, long occupiedSquares) {
		int code = PieceCode.getSpriteCode(side, PieceCode.BISHOP);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(PieceCode.BISHOP));
		
		long attacks = 0;
		
		while(squares != 0) {
			int square = BitOperations.bitScanForward(squares);
			
			long moves = MoveGenerator.getSliderMoves(square, occupiedSquares, LookupTable.RELEVANT_BISHOP_MOVES, LookupTable.BISHOP_MAGIC_VALUES, LookupTable.BISHOP_MAGIC_INDEX_BITS, LookupTable.BISHOP_MOVES);
			
			attacks |= moves;
			
			squares ^= BoardConstants.BIT_SET[square];
		}
		
		attackedSquares[code] = attacks;
	}
	
	private void calculateRookAttacks(int side, long occupiedSquares) {
		int code = PieceCode.getSpriteCode(side, PieceCode.ROOK);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(PieceCode.ROOK));
		
		long attacks = 0;
		
		while(squares != 0) {
			int square = BitOperations.bitScanForward(squares);
			
			long moves = MoveGenerator.getSliderMoves(square, occupiedSquares, LookupTable.RELEVANT_ROOK_MOVES, LookupTable.ROOK_MAGIC_VALUES, LookupTable.ROOK_MAGIC_INDEX_BITS, LookupTable.ROOK_MOVES);
			
			attacks |= moves;
			
			squares ^= BoardConstants.BIT_SET[square];
		}
		
		attackedSquares[code] = attacks;
	}
	
	private void calculateQueenAttacks(int side, long occupiedSquares) {
		int code = PieceCode.getSpriteCode(side, PieceCode.QUEEN);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(PieceCode.QUEEN));
		
		long attacks = 0;
		
		while(squares != 0) {
			int square = BitOperations.bitScanForward(squares);
			
			long moves = MoveGenerator.getSliderMoves(square, occupiedSquares, LookupTable.RELEVANT_ROOK_MOVES, LookupTable.ROOK_MAGIC_VALUES, LookupTable.ROOK_MAGIC_INDEX_BITS, LookupTable.ROOK_MOVES);
			
			moves |= MoveGenerator.getSliderMoves(square, occupiedSquares, LookupTable.RELEVANT_BISHOP_MOVES, LookupTable.BISHOP_MAGIC_VALUES, LookupTable.BISHOP_MAGIC_INDEX_BITS, LookupTable.BISHOP_MOVES);
			
			attacks |= moves;
			
			squares ^= BoardConstants.BIT_SET[square];
		}
		
		attackedSquares[code] = attacks;
	}
	
	private void calculateKingAttacks(int side) {
		int code = PieceCode.getSpriteCode(side, PieceCode.KING);
		
		long squares = getBitBoard(side).andReturn(getBitBoard(PieceCode.KING));
		
		int square = BitOperations.bitScanForward(squares);
		
		long moves = LookupTable.KING_MOVES[square];
		
		attackedSquares[code] = moves;
	}
	
	public long attackedBy(int side, int type) {
		if(side == PieceCode.BOTH_SIDES) return attackedBy(PieceCode.WHITE, type) | attackedBy(PieceCode.BLACK, type);
		
		if(type != PieceCode.ALL_PIECES) return attackedBy(PieceCode.getSpriteCode(side, type));
		
		long attacks = 0;
		
		for(int i = PieceCode.PAWN; i <= PieceCode.KING; i++) {
			attacks |= attackedBy(PieceCode.getSpriteCode(side, i));
		}
		
		return attacks;
	}
	
	public long attackedBy(int code) {
		calculateAttacks();
		
		return attackedSquares[code];
	}
	
	public int getEndgameWeight() {
		int knightPhase = 1;
		int bishopPhase = 1;
		int rookPhase = 2;
		int queenPhase = 4;
		
		int totalPhase = knightPhase*4 + bishopPhase*4 + rookPhase*4 + queenPhase*2;
		
		int phase = totalPhase;
		
		phase -= getPieceAmount(PieceCode.getSpriteCode(PieceCode.WHITE, PieceCode.KNIGHT)) * knightPhase;
		phase -= getPieceAmount(PieceCode.getSpriteCode(PieceCode.WHITE, PieceCode.BISHOP)) * bishopPhase;
		phase -= getPieceAmount(PieceCode.getSpriteCode(PieceCode.WHITE, PieceCode.ROOK)) * rookPhase;
		phase -= getPieceAmount(PieceCode.getSpriteCode(PieceCode.WHITE, PieceCode.QUEEN)) * queenPhase;
		
		phase -= getPieceAmount(PieceCode.getSpriteCode(PieceCode.BLACK, PieceCode.KNIGHT)) * knightPhase;
		phase -= getPieceAmount(PieceCode.getSpriteCode(PieceCode.BLACK, PieceCode.BISHOP)) * bishopPhase;
		phase -= getPieceAmount(PieceCode.getSpriteCode(PieceCode.BLACK, PieceCode.ROOK)) * rookPhase;
		phase -= getPieceAmount(PieceCode.getSpriteCode(PieceCode.BLACK, PieceCode.QUEEN)) * queenPhase;
		
		return (phase * 256 + (totalPhase / 2)) / totalPhase;
	}
	
	private void removeCastlePerms(int side) {
		if(side == PieceCode.WHITE) {
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
		
		if(rookIndex == 0) square = MoveGenerator.ROOK1_START_POSITION[side];
		else square = MoveGenerator.ROOK2_START_POSITION[side];
		
		if(from == square || to == square) {
			int mask;
			long key;
			
			if(side == PieceCode.WHITE) {
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
	
	public boolean isLegalMove(Move m) {
		boolean legal = true;
		
		makeMove(m);
		
		if(isOpponentInCheck()) legal = false;
		
		undoMove(m);
		
		return legal;
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
	
	public int findWinner() {
		MoveList list = new MoveList();
		
		MoveGenerator.generateAllMoves(this, list);
		
		boolean hasLegalMoves = false;
		
		for(int i=0; i<list.getCount(); i++) {
			Move m = list.getMove(i);
			
			if(isLegalMove(m)) {
				hasLegalMoves = true;
				
				break;
			}
		}
		
		return findWinner(hasLegalMoves);
	}
	
	public int findWinner(boolean hasLegalMoves) {
		if(hasLegalMoves) {
			if(fiftyMoveCounter == 100 || hasThreefoldRepetition()) return Winner.DRAW;
			
			return Winner.NONE;
		}
		
		if(!isSideInCheck()) {
			return Winner.DRAW;
		}
		
		if(side == PieceCode.WHITE) return Winner.BLACK;
		return Winner.WHITE;
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
	
	public BitBoard getBitBoard(int code) {
		return bitBoards[code];
	}
	
	public int[] getPieces() {
		return pieces;
	}
	
}
