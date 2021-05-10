package de.lila.ai;

import de.lila.game.BitBoard;
import de.lila.game.BitOperations;
import de.lila.game.Board;
import de.lila.game.LookupTable;
import de.lila.game.Piece;

public class Evaluator {
	
	private static final int PAWN_VALUE_MG = 126;
	private static final int KNIGHT_VALUE_MG = 781;
	private static final int BISHOP_VALUE_MG = 825;
	private static final int ROOK_VALUE_MG = 1276;
	private static final int QUEEN_VALUE_MG = 2538;
	
	private static final int PAWN_VALUE_EG = 208;
	private static final int KNIGHT_VALUE_EG = 854;
	private static final int BISHOP_VALUE_EG = 915;
	private static final int ROOK_VALUE_EG = 1380;
	private static final int QUEEN_VALUE_EG = 2682;
	
	public static final int GENERIC_PAWN_VALUE = (PAWN_VALUE_MG + PAWN_VALUE_EG) / 2;
	
	private static final int TEMPO_BONUS = 28;
	
	private static final int[] PAWN_TABLE_MG = new int[] {
			   0,   0,   0,   0,   0,   0,   0,   0,
			   2,   4,  11,  18,  16,  21,   9,  -3,
			  -9, -15,  11,  15,  31,  23,   6, -20,
			  -3, -20,   8,  19,  39,  17,   2,  -5,
			  11,  -4, -11,   2,  11,   0, -12,   5,
			   3, -11,  -6,  22,  -8,  -5, -14, -11,
			  -7,   6,  -2, -11,   4, -14,  10,  -9,
			   0,   0,   0,   0,   0,   0,   0,   0
	};
	
	private static final int[] PAWN_TABLE_EG = new int[] {
			   0,   0,   0,   0,   0,   0,   0,   0,
			  -8,  -6,   9,   5,  16,   6,  -6, -18,
			  -9,  -7, -10,   5,   2,   3,  -8,  -5,
			   7,   1,  -8,  -2, -14, -13, -11,  -6,
			  12,   6,   2,  -6,  -5,  -4,  14,   9,
			  27,  18,  19,  29,  30,   9,   8,  14,
			  -1, -14,  13,  22,  24,  17,   7,   7,
			   0,   0,   0,   0,   0,   0,   0,   0
	};
	
	private static final int[] KNIGHT_TABLE_MG = new int[] {
			-175, -92, -74, -73,
			 -77, -41, -27, -15,
			 -61, -17,   6,  12,
			 -35,   8,  40,  49,
			 -34,  13,  44,  51,
			  -9,  22,  58,  53,
			 -67, -27,   4,  37,
			-201, -83, -56, -26
	};
	
	private static final int[] KNIGHT_TABLE_EG = new int[] {
			 -96, -65, -49, -21,
			 -67, -54, -18,   8,
			 -40, -27,  -8,  29,
			 -35,  -2,  13,  28,
			 -45, -16,   9,  39,
			 -51, -44, -16,  17,
			 -69, -50, -51,  12,
			-100, -88, -56, -17
	};
	
	private static final int[] BISHOP_TABLE_MG = new int[] {
			 -37,  -4,  -6, -16,
			 -11,   6,  13,   3,
			  -5,  15,  -4,  12,
			  -4,   8,  18,  27,
			  -8,  20,  15,  22,
			 -11,   4,   1,   8,
			 -12, -10,   4,   0,
			 -34,   1, -10, -16
	};
	
	private static final int[] BISHOP_TABLE_EG = new int[] {
			 -40, -21, -26,  -8,
			 -26,  -9, -12,   1,
			 -11,  -1,  -1,   7,
			 -14,  -4,   0,  12,
			 -12,  -1, -10,  11,
			 -21,   4,   3,   4,
			 -22, -14,  -1,   1,
			 -32, -29, -26, -17
	};
	
	private static final int[] ROOK_TABLE_MG = new int[] {
			 -31, -20, -14,  -5,
			 -21, -13,  -8,   6,
			 -25, -11,  -1,   3,
			 -13,  -5,  -4,  -6,
			 -27, -15,  -4,   3,
			 -22,  -2,   6,  12,
			  -2,  12,  16,  18,
			 -17, -19,  -1,   9
	};
	
	private static final int[] ROOK_TABLE_EG = new int[] {
			  -9, -13, -10,  -9,
			 -12,  -9,  -1,  -2,
			   6,  -8,  -2,  -6,
			  -6,   1,  -9,   7,
			  -5,   8,   7,  -6,
			   6,   1,  -7,  10,
			   4,   5,  20,  -5,
			  18,   0,  19,  13
	};
	
	private static final int[] QUEEN_TABLE_MG = new int[] {
			   3,  -5,  -5,   4,
			  -3,   5,   8,  12,
			  -3,   6,  13,   7,
			   4,   5,   9,   8,
			   0,  14,  12,   5,
			  -4,  10,   6,   8,
			  -5,   6,  10,   8,
			  -2,  -2,   1,  -2
	};
	
	private static final int[] QUEEN_TABLE_EG = new int[] {
			 -69, -57, -47, -26,
			 -54, -31, -22,  -4,
			 -39, -18,  -9,   3,
			 -23,  -3,  13,  24,
			 -29,  -6,   9,  21,
			 -38, -18, -11,   1,
			 -50, -27, -24,  -8,
			 -74, -52, -43, -34
	};
	
	private static final int[] KING_TABLE_MG = new int[] {
			 271, 327, 271, 198,
			 278, 303, 234, 179,
			 195, 258, 169, 120,
			 164, 190, 138,  98,
			 154, 179, 105,  70,
			 123, 145,  81,  31,
			  88, 120,  65,  33,
			  59,  89,  45,  -1
	};
	
	private static final int[] KING_TABLE_EG = new int[] {
			   1,  45,  85,  76,
			  53, 100, 133, 135,
			  88, 130, 169, 175,
			 103, 156, 172, 172,
			  96, 166, 199, 199,
			  92, 172, 184, 191,
			  47, 121, 116, 131,
			  11,  59,  73,  78
	};
	
	private static final int[][] TABLES_MG = new int[][] {
			null,
			null,
			PAWN_TABLE_MG,
			KNIGHT_TABLE_MG,
			BISHOP_TABLE_MG,
			ROOK_TABLE_MG,
			QUEEN_TABLE_MG,
			KING_TABLE_MG,
			KING_TABLE_MG
	};
	
	private static final int[][] TABLES_EG = new int[][] {
			null,
			null,
			PAWN_TABLE_EG,
			KNIGHT_TABLE_EG,
			BISHOP_TABLE_EG,
			ROOK_TABLE_EG,
			QUEEN_TABLE_EG,
			KING_TABLE_EG,
			KING_TABLE_EG
	};
	
	private static final int[] PIECE_VALUES_MG = new int[] {
			0,
			0,
			PAWN_VALUE_MG,
			KNIGHT_VALUE_MG,
			BISHOP_VALUE_MG,
			ROOK_VALUE_MG,
			QUEEN_VALUE_MG
	};
	
	private static final int[] PIECE_VALUES_EG = new int[] {
			0,
			0,
			PAWN_VALUE_EG,
			KNIGHT_VALUE_EG,
			BISHOP_VALUE_EG,
			ROOK_VALUE_EG,
			QUEEN_VALUE_EG
	};
	
	private static final int[] MIRROR_TABLE = new int[] {
			56,  57,  58,  59,  60,	 61,  62,  63,
			48,	 49,  50,  51,  52,	 53,  54,  55,
			40,	 41,  42,  43,  44,	 45,  46,  47,
			32,	 33,  34,  35,  36,	 37,  38,  39,
			24,	 25,  26,  27,  28,	 29,  30,  31,
			16,  17,  18,  19,  20,	 21,  22,  23,
			 8,   9,  10,  11,  12,  13,  14,  15,
			 0,   1,   2,   3,   4,   5,   6,	7
	};
	
	private static final int[][] QUADRATIC_IMBALANCE_MG = new int[][] {
		{ 1419},
		{  101,   37},
		{   57,  249,  -49},
		{    0,  118,   10,    0},
		{  -63,   -5,  100,  132, -246},
		{ -210,   37,  147,  161, -158,   -9}
	};
	
	private static final int[][] QUADRATIC_IMBALANCE_OPPONENT_MG = new int[][] {
		{},
		{   33},
		{   46,  106},
		{   75,   59,   60},
		{   26,    6,   38,  -12},
		{   97,  100,  -58,  112,  276}
	};
	
	private static final int[][] QUADRATIC_IMBALANCE_EG = new int[][] {
		{ 1455},
		{   28,   39},
		{   64,  187,  -62},
		{    0,  137,   27,    0},
		{  -68,    3,   81,  118, -244},
		{ -211,   14,  141,  105, -174,  -31}
	};
	
	private static final int[][] QUADRATIC_IMBALANCE_OPPONENT_EG = new int[][] {
		{},
		{   30},
		{   18,   84},
		{   35,   44,   15},
		{   35,   22,   39,   -2},
		{   93,  163,  -91,  192,  225}
	};
	
	private static final int[][] MOBILITY_BONUS_MG = new int[][] {
		{-62, -53, -12,  -4,   3,  13,  22,  28,  33},
	    {-48, -20,  16,  26,  38,  51,  55,  63,  63,  68,  81,  81,  91,  98},
	    {-60, -20,   2,   3,   3,  11,  22,  31,  40,  40,  41,  48,  57,  57,  62},
	    {-30, -12,  -8,  -9,  20,  23,  23,  35,  38,  53,  64,  65,  65,  66,  67,  67,  72,  72,  77,  79,  93, 108, 108, 108, 110, 114, 114, 116}
	};
	
	private static final int[][] MOBILITY_BONUS_EG = new int[][] {
		{-81, -56, -31, -16,   5,  11,  17,  20,  25},
	    {-59, -23,  -3,  13,  24,  42,  54,  57,  65,  73,  78,  86,  88,  97},
	    {-78, -17,  23,  39,  70,  99, 103, 121, 134, 139, 158, 164, 168, 169, 172},
	    {-48, -30,  -7,  19,  40,  55,  59,  75,  78,  96,  96, 100, 121, 127, 131, 133, 136, 141, 147, 150, 151, 168, 168, 171, 182, 182, 192, 219}
	};
	
	private static final long SPACE_MASK_WHITE = 16954726998343680l;
	private static final long SPACE_MASK_BLACK = 1010580480;
	
	private static final int[] CONNECTED_PAWNS_BONUS = new int[] {
			7, 8, 12, 29, 48, 86
	};
	
	private static final int[] BLOCKED_PAWNS_PENALTY_MG = new int[] {
			-11, -3
	};
	
	private static final int[] BLOCKED_PAWNS_PENALTY_EG = new int[] {
			-4, 4
	};
	
	public static int eval(Board b, int side) {
		int score = eval(b);
		
		if(side == Piece.WHITE) return score;
		return -score;
	}
	
	private static int eval(Board b) {
		int endgameWeight = b.getEndgameWeight();
		int openingWeight = 256 - endgameWeight;
		
		int scoreMiddle = 0;
		int scoreEnd = 0;
		
		if(openingWeight != 0) scoreMiddle = evalMiddleGame(b);
		if(endgameWeight != 0) scoreEnd = evalEndGame(b);
		
		int score = ((scoreMiddle * openingWeight) + (scoreEnd * endgameWeight)) / 256;
		
		score += b.getSide() == Piece.WHITE ? TEMPO_BONUS : -TEMPO_BONUS;
		
		return score;
	}
	
	private static int evalMiddleGame(Board b) {
		int score = evalMaterial(b, Piece.WHITE, PIECE_VALUES_MG) - evalMaterial(b, Piece.BLACK, PIECE_VALUES_MG);
		
		score += evalPiecePositions(b, Piece.WHITE, TABLES_MG) - evalPiecePositions(b, Piece.BLACK, TABLES_MG);
		
		score += evalTotalImbalance(b, false);
		
		score += evalMobility(b, Piece.WHITE, MOBILITY_BONUS_MG) - evalMobility(b, Piece.BLACK, MOBILITY_BONUS_MG);
		
		score += evalPawnStructure(b, Piece.WHITE, false) - evalPawnStructure(b, Piece.BLACK, false);
		
		score += evalPassedPawns(b, Piece.WHITE, false) - evalPassedPawns(b, Piece.BLACK, false);
		
		score += evalTacticalPieces(b, Piece.WHITE, false) - evalTacticalPieces(b, Piece.BLACK, false);
		
		score += evalKingPosition(b, Piece.WHITE, false) - evalKingPosition(b, Piece.BLACK, false);
		
		score += evalThreats(b, Piece.WHITE, false) - evalThreats(b, Piece.BLACK, false);
		
		score += evalSpace(b, Piece.WHITE) - evalSpace(b, Piece.BLACK);
		
		return score;
	}
	
	private static int evalEndGame(Board b) {
		int score = evalMaterial(b, Piece.WHITE, PIECE_VALUES_EG) - evalMaterial(b, Piece.BLACK, PIECE_VALUES_EG);
		
		score += evalPiecePositions(b, Piece.WHITE, TABLES_EG) - evalPiecePositions(b, Piece.BLACK, TABLES_EG);
		
		score += evalTotalImbalance(b, true);
		
		score += evalMobility(b, Piece.WHITE, MOBILITY_BONUS_EG) - evalMobility(b, Piece.BLACK, MOBILITY_BONUS_EG);
		
		score += evalPawnStructure(b, Piece.WHITE, true) - evalPawnStructure(b, Piece.BLACK, true);
		
		score += evalPassedPawns(b, Piece.WHITE, true) - evalPassedPawns(b, Piece.BLACK, true);
		
		score += evalTacticalPieces(b, Piece.WHITE, true) - evalTacticalPieces(b, Piece.BLACK, true);
		
		score += evalKingPosition(b, Piece.WHITE, true) - evalKingPosition(b, Piece.BLACK, true);
		
		score += evalThreats(b, Piece.WHITE, true) - evalThreats(b, Piece.BLACK, true);
		
		return score;
	}
	
	private static int evalMaterial(Board b, int side, int[] table) {
		int score = evalMaterial(b, side, table, Piece.PAWN);
		
		score += evalMaterial(b, side, table, Piece.KNIGHT);
		score += evalMaterial(b, side, table, Piece.BISHOP);
		score += evalMaterial(b, side, table, Piece.ROOK);
		score += evalMaterial(b, side, table, Piece.QUEEN);
		
		return score;
	}
	
	private static int evalNonPawnMaterial(Board b, int side, int[] table) {
		int score = evalMaterial(b, side, table, Piece.KNIGHT);
		
		score += evalMaterial(b, side, table, Piece.BISHOP);
		score += evalMaterial(b, side, table, Piece.ROOK);
		score += evalMaterial(b, side, table, Piece.QUEEN);
		
		return score;
	}
	
	private static int evalMaterial(Board b, int side, int[] table, int type) {
		return b.getPieceAmount(side, type) * table[type];
	}
	
	private static int evalPiecePositions(Board b, int side, int[][] tables) {
		int score = evalPiecePositions(b, side, tables, Piece.PAWN);
		
		score += evalPiecePositions(b, side, tables, Piece.KNIGHT);
		score += evalPiecePositions(b, side, tables, Piece.BISHOP);
		score += evalPiecePositions(b, side, tables, Piece.ROOK);
		score += evalPiecePositions(b, side, tables, Piece.QUEEN);
		score += evalPiecePositions(b, side, tables, Piece.KING);
		
		return score;
	}
	
	private static int evalPiecePositions(Board b, int side, int[][] tables, int type) {
		int score = 0;
		
		int[] table = tables[type];
		
		boolean mirrorTable = table.length == 32;
		
		int w = 8;
		
		if(mirrorTable) w = 4;
		
		boolean flip = side == Piece.WHITE;
		
		int p = Piece.getPiece(side, type);
		
		int l = b.getPieceAmount(p);
		
		for(int i=0; i<l; i++) {
			int square = b.getPieceSquare(p, i);
			
			if(flip) square = MIRROR_TABLE[square];
			
			int x = square % 8;
			int y = square / 8;
			
			if(mirrorTable && x >= w) x = 2 * w - 1 - x;
			
			score += table[y * w + x];
		}
		
		return score;
	}
	
	private static int evalTotalImbalance(Board b, boolean endgame) {
		int[][] table1 = endgame ? QUADRATIC_IMBALANCE_EG : QUADRATIC_IMBALANCE_MG;
		int[][] table2 = endgame ? QUADRATIC_IMBALANCE_OPPONENT_EG : QUADRATIC_IMBALANCE_OPPONENT_MG;
		
		int score = evalImbalance(b, Piece.WHITE, table1, table2) - evalImbalance(b, Piece.BLACK, table1, table2);
		
		return score / 16;
	}
	
	private static int evalImbalance(Board b, int side, int[][] table1, int[][] table2) {
		int opponentSide = Piece.flipSide(side);
		
		int bonus = 0;
		
		for(int type1 = Piece.BISHOP_PAIR; type1 <= Piece.QUEEN; type1++) {
			if(type1 > Piece.BISHOP_PAIR && type1 < Piece.PAWN) continue;
			
			int count = b.getPieceAmount(side, type1);
			
			if(count == 0) continue;
			
			int i1 = 0;
			
			if(type1 != Piece.BISHOP_PAIR) i1 = type1 - Piece.PAWN + 1;
			
			int v = table1[i1][i1] * count;
			
			for(int type2 = Piece.BISHOP_PAIR; type2 < type1; type2++) {
				if(type2 > Piece.BISHOP_PAIR && type2 < Piece.PAWN) continue;
				
				int i2 = 0;
				
				if(type2 != Piece.BISHOP_PAIR) i2 = type2 - Piece.PAWN + 1;
				
				v += table1[i1][i2] * b.getPieceAmount(side, type2) + table2[i1][i2] * b.getPieceAmount(opponentSide, type2);
			}
			
			bonus += count * v;
		}
		
		return bonus;
	}
	
	private static int evalMobility(Board b, int side, int[][] tables) {
		int opponentSide = Piece.flipSide(side);
		
		long mobilityArea = BitBoard.ALL_SQUARES;
		
		mobilityArea &= BitOperations.inverse(b.getBitBoard(side).andReturn(b.getBitBoard(Piece.KING)));
		mobilityArea &= BitOperations.inverse(b.getBitBoard(side).andReturn(b.getBitBoard(Piece.QUEEN)));
		
		mobilityArea &= BitOperations.inverse(b.attackedBy(opponentSide, Piece.PAWN));
		
		int down = side == Piece.WHITE ? BitOperations.SHIFT_DOWN : BitOperations.SHIFT_UP;
		
		long friendlyPawns = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.PAWN));
		
		long blockedSquares = b.getBitBoard(side).orReturn(b.getBitBoard(opponentSide));
		
		blockedSquares = BitOperations.shift(blockedSquares, down);
		
		long excludedPawns = friendlyPawns & (blockedSquares | BitBoard.getLowerRanks(side == Piece.WHITE ? 4 * 8 : 3 * 8, down));
		
		mobilityArea &= BitOperations.inverse(excludedPawns);
		
		long blockersForKing = getKingBlockers(b, side);
		
		mobilityArea &= BitOperations.inverse(blockersForKing);
		
		int score = evalMobility(b, side, tables, Piece.KNIGHT, mobilityArea);
		
		score += evalMobility(b, side, tables, Piece.BISHOP, mobilityArea);
		score += evalMobility(b, side, tables, Piece.ROOK, mobilityArea);
		score += evalMobility(b, side, tables, Piece.QUEEN, mobilityArea);
		
		return score;
	}
	
	private static long getKingBlockers(Board b, int side) {
		int opponentSide = Piece.flipSide(side);
		
		int kingSquare = b.getPieceSquare(Piece.getPiece(side, Piece.KING), 0);
		
		long opponentBishopSliders = b.getBitBoard(opponentSide).andReturn(b.getBitBoard(Piece.BISHOP).orReturn(b.getBitBoard(Piece.QUEEN)));
		long opponentRookSliders = b.getBitBoard(opponentSide).andReturn(b.getBitBoard(Piece.ROOK).orReturn(b.getBitBoard(Piece.QUEEN)));
		
		long bishopXRayMoves = LookupTable.getSliderMoves(kingSquare, opponentBishopSliders, Piece.BISHOP);
		long rookXRayMoves = LookupTable.getSliderMoves(kingSquare, opponentRookSliders, Piece.ROOK);
		
		long occupiedSquares = b.getBitBoard(Piece.WHITE).orReturn(b.getBitBoard(Piece.BLACK));
		
		long bishopMoves = LookupTable.getSliderMoves(kingSquare, occupiedSquares, Piece.BISHOP);
		long rookMoves = LookupTable.getSliderMoves(kingSquare, occupiedSquares, Piece.ROOK);
		
		long attackingBishopSliders = opponentBishopSliders & bishopXRayMoves;
		long attackingRookSliders = opponentRookSliders & rookXRayMoves;
		
		long blockers = 0;
		
		while(attackingBishopSliders != 0) {
			int attackingFrom = BitOperations.bitScanForward(attackingBishopSliders);
			
			long moves = LookupTable.getSliderMoves(attackingFrom, occupiedSquares, Piece.BISHOP);
			
			long pinnedPieces = moves & bishopMoves;
			
			blockers |= pinnedPieces;
			
			attackingBishopSliders ^= BitBoard.SINGLE_SQUARE[attackingFrom];
		}
		
		while(attackingRookSliders != 0) {
			int attackingFrom = BitOperations.bitScanForward(attackingRookSliders);
			
			long moves = LookupTable.getSliderMoves(attackingFrom, occupiedSquares, Piece.ROOK);
			
			long pinnedPieces = moves & rookMoves;
			
			blockers |= pinnedPieces;
			
			attackingRookSliders ^= BitBoard.SINGLE_SQUARE[attackingFrom];
		}
		
		return blockers;
	}
	
	private static int evalMobility(Board b, int side, int[][] tables, int type, long mobilityArea) {
		int[] table = tables[type - Piece.KNIGHT];
		
		int p = Piece.getPiece(side, type);
		
		int l = b.getPieceAmount(p);
		
		int score = 0;
		
		for(int i=0; i<l; i++) {
			int square = b.getPieceSquare(p, i);
			
			int n = countMobility(b, side, type, square, mobilityArea);
			
			score += table[n];
		}
		
		return score;
	}
	
	private static int countMobility(Board b, int side, int type, int square, long mobilityArea) {
		long friendlyQueens = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.QUEEN));
		
		if(type == Piece.KNIGHT) {
			long moves = LookupTable.KNIGHT_MOVES[square];
			
			moves &= mobilityArea;
			moves &= BitOperations.inverse(friendlyQueens);
			
			return BitOperations.countBits(moves);
		}
		
		long occupiedSquares = b.getBitBoard(Piece.WHITE).orReturn(b.getBitBoard(Piece.BLACK));
		
		long bishopXRaySquares = occupiedSquares & BitOperations.inverse(b.getBitBoard(Piece.QUEEN).getValue());
		
		long friendlyRooks = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.ROOK));
		
		long rookXRaySquares = bishopXRaySquares & BitOperations.inverse(friendlyRooks);
		
		if(type == Piece.BISHOP) {
			long moves = LookupTable.getSliderMoves(square, bishopXRaySquares, Piece.BISHOP);
			
			moves &= mobilityArea;
			moves &= BitOperations.inverse(friendlyQueens);
			
			return BitOperations.countBits(moves);
		}
		
		if(type == Piece.ROOK) {
			long moves = LookupTable.getSliderMoves(square, rookXRaySquares, Piece.ROOK);
			
			moves &= mobilityArea;
			
			return BitOperations.countBits(moves);
		}
		
		if(type == Piece.QUEEN) {
			long moves = LookupTable.getSliderMoves(square, occupiedSquares, Piece.ROOK);
			
			moves |= LookupTable.getSliderMoves(square, occupiedSquares, Piece.BISHOP);
			
			moves &= mobilityArea;
			
			return BitOperations.countBits(moves);
		}
		
		return 0;
	}
	
	private static int evalSpace(Board b, int side) {
		int opponentSide = Piece.flipSide(side);
		
		if(evalNonPawnMaterial(b, side, PIECE_VALUES_MG) + evalNonPawnMaterial(b, opponentSide, PIECE_VALUES_MG) < 11551) return 0;
		
		int down = side == Piece.WHITE ? BitOperations.SHIFT_DOWN : BitOperations.SHIFT_UP;
		
		long mask = side == Piece.WHITE ? SPACE_MASK_WHITE : SPACE_MASK_BLACK;
		
		long friendlyPawns = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.PAWN));
		long opponentPawns = b.getBitBoard(opponentSide).andReturn(b.getBitBoard(Piece.PAWN));
		
		long safe = mask & BitOperations.inverse(friendlyPawns) & BitOperations.inverse(b.attackedBy(opponentSide, Piece.PAWN));
		
		long behind = friendlyPawns;
		
		behind |= BitOperations.shift(behind, down);
		behind |= BitOperations.shift(behind, down);
		
		int bonus = BitOperations.countBits(safe) + BitOperations.countBits(behind & safe & BitOperations.inverse(b.attackedBy(opponentSide, Piece.ALL_PIECES)));
		
		long doublePawnAttacks = BitBoard.getDoublePawnAttacks(opponentSide, opponentPawns);
		
		int blockedCount = BitOperations.countBits(BitOperations.shift(friendlyPawns, -down) & (opponentPawns | doublePawnAttacks));
		
		int weight = b.getPieceAmount(side, Piece.ALL_PIECES) - 3 + Math.min(blockedCount, 9);
		
		return bonus * weight * weight / 16;
	}
	
	private static int evalPawnStructure(Board b, int side, boolean endgame) {
		int score = 0;
		
		int opponentSide = Piece.flipSide(side);
		
		int friendlyPawn = Piece.getPiece(side, Piece.PAWN);
		int opponentPawn = Piece.getPiece(opponentSide, Piece.PAWN);
		
		int up = side == Piece.WHITE ? BitOperations.SHIFT_UP : BitOperations.SHIFT_DOWN;
		
		long friendlyPawns = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.PAWN));
		long opponentPawns = b.getBitBoard(opponentSide).andReturn(b.getBitBoard(Piece.PAWN));
		
		long leftSquares = BitOperations.shift(friendlyPawns, BitOperations.SHIFT_LEFT) & BitOperations.inverse(BitBoard.FILE_H);
		long rightSquares = BitOperations.shift(friendlyPawns, BitOperations.SHIFT_RIGHT) & BitOperations.inverse(BitBoard.FILE_A);
		
		long phalanxPawns = friendlyPawns & (leftSquares | rightSquares);
		
		long doubledPawns = BitOperations.shift(friendlyPawns, BitOperations.SHIFT_UP) & friendlyPawns;
		
		score -= BitOperations.countBits(doubledPawns) * (endgame ? 56 : 11);
		
		for(int i=0; i<b.getPieceAmount(friendlyPawn); i++) {
			int square = b.getPieceSquare(friendlyPawn, i);
			
			long file = BitBoard.getFile(square);
			
			long adjacentFiles = BitBoard.getAdjacentFiles(square);
			
			long neighbours = friendlyPawns & adjacentFiles;
			
			long supportedBy = neighbours & BitBoard.getRank(square - up);
			
			boolean isPhalanx = (BitBoard.SINGLE_SQUARE[square] & phalanxPawns) != 0;
			
			long upperRanks = BitBoard.getLowerRanks(square, up);
			
			boolean isOpposed = (opponentPawns & file & upperRanks) != 0;
			
			boolean isConnected = isPhalanx || supportedBy != 0;
			
			boolean isIsolated = (friendlyPawns & adjacentFiles) == 0;
			
			long advancingMask = BitBoard.SINGLE_SQUARE[square + up];
			
			boolean blocked = (opponentPawns & advancingMask) != 0;
			
			boolean isAdvancingUnsafe = blocked || (b.attackedBy(opponentPawn) & advancingMask) != 0;
			
			boolean isBackward = (BitBoard.getLowerRanks(square + up, -up) & adjacentFiles & friendlyPawns) == 0 && isAdvancingUnsafe;
			
			boolean isWeak = isIsolated || isBackward;
			
			boolean isDoubled = (friendlyPawns & file & BitBoard.getLowerRanks(square, -up)) != 0;
			
			boolean isDoubledIsolated = isIsolated && isDoubled && isOpposed && (upperRanks & adjacentFiles & opponentPawns) == 0;
			
			if(isDoubledIsolated) score -= endgame ? 56 : 11;
			else if(isIsolated) score -= endgame ? 15 : 5;
			else if(isBackward) score -= endgame ? 24 : 9;
			
			int rank = square / 8 + 1;
			
			if(side == Piece.WHITE) rank = 9 - rank;
			
			if(isConnected) {
				
				int supporterCount = BitOperations.countBits(supportedBy);
				
				int bonus = CONNECTED_PAWNS_BONUS[rank - 2] * (2 + (isPhalanx ? 1 : 0) - (isOpposed ? 1 : 0)) + 21 * supporterCount;
				
				if(endgame) {
					bonus = bonus * (rank - 3) / 4;
				}
				
				score += bonus;
			}
			
			if(isWeak && !isOpposed) {
				score -= endgame ? 27 : 13;
			}
			
			if(endgame && supportedBy == 0) {
				int attackerCount = BitOperations.countBits(opponentPawns & adjacentFiles & BitBoard.getRank(square + up));
				
				if(attackerCount == 2) score -= 56;
			}
			
			if((rank == 5 || rank == 6) && blocked) {
				int blockedAt = rank - 5;
				
				score += endgame ? BLOCKED_PAWNS_PENALTY_EG[blockedAt] : BLOCKED_PAWNS_PENALTY_MG[blockedAt];
			}
		}
		
		return score;
	}
	
	private static int evalPassedPawns(Board b, int side, boolean endgame) {
		return 0;
	}
	
	private static int evalTacticalPieces(Board b, int side, boolean endgame) {
		long knights = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.KNIGHT));
		long bishops = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.BISHOP));
		long rooks = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.ROOK));
		long queens = b.getBitBoard(side).andReturn(b.getBitBoard(Piece.QUEEN));
		
		long minorPieces = knights | bishops;
		
		long allPawns = b.getBitBoard(Piece.PAWN).getValue();
		
		int down = side == Piece.WHITE ? BitOperations.SHIFT_DOWN : BitOperations.SHIFT_UP;
		
		int score = 0;
		
		score += BitOperations.countBits(BitOperations.shift(allPawns, down) & minorPieces) * (endgame ? 3 : 18);
		
		
		
		return score;
	}
	
	private static int evalKingPosition(Board b, int side, boolean endgame) {
		int opponentSide = Piece.flipSide(side);
		
		int score = isOnPawnlessFlank(b, b.getPieceSquare(Piece.getPiece(opponentSide, Piece.KING), 0)) ? (endgame ? 95 : 17) : 0;
		
		
		
		return score;
	}
	
	private static boolean isOnPawnlessFlank(Board b, int x) {
		long pawns = b.getBitBoard(Piece.PAWN).getValue();
		
		if(x % 7 != 0) {
			x = (x - 1) / 2 * 2 + 1;
		}
		
		int fromX = x == 7 ? 5 : Math.max(x - 1, 0);
		int toX = Math.min(x + 2, 7);
		
		long mask = BitBoard.getFiles(fromX, toX);
		
		return (pawns & mask) == 0;
	}
	
	private static int evalThreats(Board b, int side, boolean endgame) {
		return 0;
	}
	
}
