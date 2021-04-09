package de.chess.ai;

import de.chess.game.Board;
import de.chess.game.Move;
import de.chess.game.MoveGenerator;
import de.chess.game.MoveList;
import de.chess.game.Winner;
import de.chess.ui.WidgetUI;
import de.chess.util.MathUtil;

public class Search {
	
	private static final int INFINITY = 1000000;
	
	private static final int MATE_SCORE = 100000;
	
	private static final int ALLOCATED_TIME = 3000;
	
	private static final int MIN_SEARCH_DEPTH = 4;
	
	private static final int WINDOW_SIZE = Evaluator.GENERIC_PAWN_VALUE;
	
	private static final int NULL_MOVE_REDUCTION = 2;
	
	private static Move responseMove;
	
	private static long visitedNormalNodes;
	private static long visitedQuiesceNodes;
	private static long transpositionUses;
	
	public static Move findNextMove(Board b) {
		System.out.println("------");
		
		long before = System.currentTimeMillis();
		
		visitedNormalNodes = 0;
		visitedQuiesceNodes = 0;
		transpositionUses = 0;
		
		int score = 0;
		
		int depth = 1;
		
		while(depth <= MIN_SEARCH_DEPTH || System.currentTimeMillis() - before < ALLOCATED_TIME) {
			score = startSearch(b, depth, score);
			
			System.out.println("depth "+depth+" search complete");
			
			depth++;
		}
		
		float time = (System.currentTimeMillis() - before) / 1000f;
		
		long visitedNodes = visitedNormalNodes + visitedQuiesceNodes;
		
		System.out.println("---");
		System.out.println("time: "+MathUtil.DECIMAL_FORMAT.format(time)+"s");
		System.out.println("eval: "+MathUtil.DECIMAL_FORMAT.format(score));
		System.out.println("nodes: "+MathUtil.DECIMAL_FORMAT.format(visitedNodes));
		System.out.println("knodes/s: "+MathUtil.DECIMAL_FORMAT.format(visitedNodes / 1000f / time));
		System.out.println("normal_nodes: "+MathUtil.DECIMAL_FORMAT.format(visitedNormalNodes));
		System.out.println("quiesce_nodes: "+MathUtil.DECIMAL_FORMAT.format(visitedQuiesceNodes));
		System.out.println("transposition_uses: "+MathUtil.DECIMAL_FORMAT.format(transpositionUses));
		
		WidgetUI.setPrediction(score);
		WidgetUI.addToEvalHistory(score);
		
		return responseMove;
	}
	
	private static int startSearch(Board b, int depth, int lastScore) {
		// Aspiration windows
		
		if(depth != 1) {
			int alpha = lastScore - WINDOW_SIZE;
			int beta = lastScore + WINDOW_SIZE;
			
			int score = runAlphaBeta(b, alpha, beta, depth);
			
			if(score > alpha && score < beta) {
				return score;
			} else {
				System.out.println("Aspiration window failed");
			}
		}
		
		int score = runAlphaBeta(b, -INFINITY, INFINITY, depth);
		
		return score;
	}
	
	private static int runAlphaBeta(Board b, int alpha, int beta, int depth) {
		visitedNormalNodes++;
		
		int type = TranspositionEntry.TYPE_UPPER_BOUND;
		
		MoveList list = new MoveList();
		
		MoveGenerator.generateAllMoves(b, list);
		
		MoveEvaluator.eval(list, b);
		
		TranspositionEntry entry = TranspositionTable.getEntry(b.getPositionKey());
		
		if(entry != null && entry.getMove() != null) {
			list.applyMoveScore(entry.getMove(), MoveEvaluator.HASH_MOVE_SCORE);
		}
		
		applyKillerMoves(list, b.getHistoryPly());
		
		Move bestMove = null;
		
		while(list.hasMovesLeft()) {
			Move m = list.next();
			
			b.makeMove(m);
			
			if(!b.isOpponentInCheck()) {
				int score = -alphaBeta(b, 1, -beta, -alpha, depth - 1, true);
				
				if(score > alpha) {
					bestMove = m;
					alpha = score;
					
					type = TranspositionEntry.TYPE_EXACT;
				}
			}
			
			b.undoMove(m);
		}
		
		if(alpha >= beta) type = TranspositionEntry.TYPE_LOWER_BOUND;
		
		TranspositionTable.putEntry(b.getPositionKey(), depth, 0, bestMove, type, alpha, b.getHistoryPly());
		
		responseMove = bestMove;
		
		return alpha;
	}
	
	private static int alphaBeta(Board b, int plyFromRoot, int alpha, int beta, int depth, boolean allowNullMove) {
		if(depth <= 0) {
			return quiesce(b, plyFromRoot, alpha, beta);
		}
		
		visitedNormalNodes++;
		
		if(b.getFiftyMoveCounter() == 100 || b.hasThreefoldRepetition()) return 0;
		
		// TT lookup
		
		TranspositionEntry entry = TranspositionTable.getEntry(b.getPositionKey());
		
		if(entry != null && entry.getDepth() >= depth) {
			transpositionUses++;
			
			int entryScore = entry.getScore(plyFromRoot);
			
			if(entry.getType() == TranspositionEntry.TYPE_EXACT) return entryScore;
			else if(entry.getType() == TranspositionEntry.TYPE_LOWER_BOUND) alpha = Math.max(alpha, entryScore);
			else beta = Math.min(beta, entryScore);
			
			if(alpha >= beta) return entryScore;
		}
		
		int type = TranspositionEntry.TYPE_UPPER_BOUND;
		
		int newDepth = depth - 1;
		
		// Null move
		
		boolean inCheck = b.isSideInCheck();
		
		if(allowNullMove && !inCheck) {
			b.makeNullMove();
			
			int score = -alphaBeta(b, plyFromRoot + 1, -beta, -beta + 1, newDepth - NULL_MOVE_REDUCTION, false);
			
			b.undoNullMove();
			
			if(score >= beta) {
				return score;
			}
		}
		
		// Ordinary search
		
		MoveList list = new MoveList();
		
		MoveGenerator.generateAllMoves(b, list);
		
		MoveEvaluator.eval(list, b);
		
		if(entry != null && entry.getMove() != null) {
			list.applyMoveScore(entry.getMove(), MoveEvaluator.HASH_MOVE_SCORE);
		}
		
		applyKillerMoves(list, b.getHistoryPly());
		
		boolean hasLegalMove = false;
		
		Move bestMove = null;
		int bestScore = Integer.MIN_VALUE;
		
		int moveCount = 0;
		
		while(list.hasMovesLeft()) {
			Move m = list.next();
			
			b.makeMove(m);
			
			if(!b.isOpponentInCheck()) {
				hasLegalMove = true;
				
				moveCount++;
				
				int score = 0;
				
				boolean doFullDepthSearch = true;
				
				boolean captureOrPromotion = m.getCaptured() != 0 || m.getPromoted() != 0;
				
				// Late move reduction
				
				if(moveCount > 1) {
					
					if(depth > 2 && !inCheck && !captureOrPromotion && !b.isSideInCheck()) {
						int r = 1;
						
						int d = newDepth - r;
						
						if(d < 1) d = 1;
						else if(d > newDepth) d = newDepth;
						
						score = -alphaBeta(b, plyFromRoot + 1, -beta, -alpha, d, true);
						
						doFullDepthSearch = score > alpha && d < newDepth;
					}
				}
				
				if(doFullDepthSearch) {
					score = -alphaBeta(b, plyFromRoot + 1, -beta, -alpha, newDepth, true);
				}
				
				if(score > bestScore) {
					bestMove = m;
					bestScore = score;
				}
				
				if(score > alpha) {
					alpha = score;
					
					type = TranspositionEntry.TYPE_EXACT;
				}
			}
			
			b.undoMove(m);
			
			if(alpha >= beta) {
				KillerTable.storeMove(m, b.getHistoryPly());
				
				TranspositionTable.putEntry(b.getPositionKey(), depth, plyFromRoot, bestMove, TranspositionEntry.TYPE_LOWER_BOUND, beta, b.getHistoryPly());
				
				return alpha;
			}
		}
		
		if(!hasLegalMove) {
			int winner = b.findWinner(false);
			
			int score;
			
			if(winner == Winner.DRAW) {
				score = 0;
			} else {
				int i = MATE_SCORE - plyFromRoot;
				
				score = b.getSide() == winner ? i : -i;
			}
			
			TranspositionTable.putEntry(b.getPositionKey(), depth, plyFromRoot, null, TranspositionEntry.TYPE_EXACT, score, b.getHistoryPly());
			
			return score;
		}
		
		TranspositionTable.putEntry(b.getPositionKey(), depth, plyFromRoot, bestMove, type, alpha, b.getHistoryPly());
		
		return alpha;
	}
	
	private static int quiesce(Board b, int plyFromRoot, int alpha, int beta) {
		visitedQuiesceNodes++;
		
		if(b.getFiftyMoveCounter() == 100 || b.hasThreefoldRepetition()) return 0;
		
		boolean inCheck = b.isSideInCheck();
		
		if(!inCheck) {
			int evalScore = Evaluator.eval(b, b.getSide());
			
			if(evalScore >= beta) return beta;
			
			if(evalScore > alpha) alpha = evalScore;
		}
		
		MoveList list = new MoveList();
		
		MoveGenerator.generateAllMoves(b, list);
		
		MoveEvaluator.eval(list, b);
		
		applyKillerMoves(list, b.getHistoryPly());
		
		boolean hasLegalMove = false;
		
		while(list.hasMovesLeft()) {
			Move m = list.next();
			
			b.makeMove(m);
			
			int score = 0;
			boolean hasDoneMove = false;
			
			if(!b.isOpponentInCheck()) {
				hasLegalMove = true;
				
				if(inCheck || m.getCaptured() != 0 || m.getPromoted() != 0) {
					hasDoneMove = true;
					
					score = -quiesce(b, plyFromRoot + 1, -beta, -alpha);
					
					if(score > alpha) {
						alpha = score;
					}
				}
			}
			
			b.undoMove(m);
			
			if(hasDoneMove && score >= beta) {
				KillerTable.storeMove(m, b.getHistoryPly());
				
				return beta;
			}
		}
		
		if(!hasLegalMove) {
			int winner = b.findWinner(false);
			
			int score;
			
			if(winner == Winner.DRAW) {
				score = 0;
			} else {
				int i = MATE_SCORE - plyFromRoot;
				
				score = b.getSide() == winner ? i : -i;
			}
			
			return score;
		}
		
		return alpha;
	}
	
	private static void applyKillerMoves(MoveList list, int ply) {
		for(int i=0; i<KillerTable.SIZE; i++) {
			Move killer = KillerTable.getMove(ply, i);
			
			if(killer != null) list.applyMoveScore(killer, MoveEvaluator.KILLER_MOVE_SCORE);
		}
	}
	
	public static boolean isMateScore(int score) {
		int maxDepth = 1000;
		
		return Math.abs(score) > MATE_SCORE - maxDepth;
	}
	
}
