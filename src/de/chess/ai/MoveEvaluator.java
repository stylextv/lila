package de.chess.ai;

import de.chess.game.Board;
import de.chess.game.BoardConstants;
import de.chess.game.Move;
import de.chess.game.MoveList;
import de.chess.game.PieceCode;

public class MoveEvaluator {
	
	public static final int PV_MOVE_SCORE = 1000000;
	
	public static final int HASH_MOVE_SCORE = 200000;
	
	public static final int TACTICAL_MOVE_SCORE = 100000;
	
	public static final int KILLER_MOVE_SCORE = TACTICAL_MOVE_SCORE;
	
	private static final int[][] MVV_LVA = new int[8][8];
	
	private static final int[] VICTIM_SCORES = new int[] {
			0, 0,
			100, 200, 300, 400, 500, 600
	};
	
	private static final int ATTACKED_BY_PAWN_PENALTY = 300;
	
	static {
		for(int attacker = PieceCode.PAWN; attacker <= PieceCode.KING; attacker++) {
			for(int victim = PieceCode.PAWN; victim <= PieceCode.KING; victim++) {
				
				int attackerValue = VICTIM_SCORES[attacker];
				int victimValue = VICTIM_SCORES[victim];
				
				int score = 0;
				
				if(victimValue > attackerValue) score += 1000;
				else if(attackerValue > victimValue) score -= 1000;
				
				score += victimValue - (attackerValue / 100);
				
				MVV_LVA[victim][attacker] = score;
			}
		}
	}
	
	public static void eval(MoveList list, Board b) {
		int opponentPawnCode = PieceCode.getSpriteCode((b.getSide() + 1) % 2, PieceCode.PAWN);
		
		for(int i=0; i<list.getCount(); i++) {
			Move m = list.getMove(i);
			
			int pieceType = b.getPieceType(m.getFrom());
			
			int score = 0;
			
			if(m.getCaptured() != 0 || m.getPromoted() != 0) {
				
				score = TACTICAL_MOVE_SCORE;
				
				if(m.getCaptured() != 0) score += MVV_LVA[m.getCaptured()][pieceType];
				if(m.getPromoted() != 0) score += VICTIM_SCORES[m.getPromoted()] + 1000;
			}
			
			if(pieceType != PieceCode.PAWN) {
				
				boolean attackedByPawns = (b.attackedBy(opponentPawnCode) & BoardConstants.BIT_SET[m.getTo()]) != 0;
				
				if(attackedByPawns) score -= ATTACKED_BY_PAWN_PENALTY;
			}
			
			m.setScore(score);
		}
	}
	
}
