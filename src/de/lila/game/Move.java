package de.lila.game;

public class Move {
	
	public static final int MEMORY_SIZE = 6 * 4 + 2 + 1;
	
	private int from;
	private int to;
	
	private int captured;
	private int promoted;
	
	private int flag;
	
	private short hash = -1;
	
	private int score;
	
	private boolean picked = false;
	
	public Move(int from, int to, int captured, int promoted, int flag) {
		this.from = from;
		this.to = to;
		
		this.captured = captured;
		this.promoted = promoted;
		
		this.flag = flag;
	}
	
	public short getHash() {
		if(hash == -1) hash = (short) (from | (to << 6) | (promoted << 12));
		
		return hash;
	}
	
	public String getAlgebraicNotation() {
		String s = BoardSquare.getNotation(from) + BoardSquare.getNotation(to);
		
		if(promoted != 0) {
			s = s + Piece.getFenNotation(promoted - Piece.PAWN + Piece.TYPE_AMOUNT);
		}
		
		return s;
	}
	
	public boolean isTactical() {
		return captured != 0 || promoted != 0;
	}
	
	public int getFrom() {
		return from;
	}
	
	public int getTo() {
		return to;
	}
	
	public int getCaptured() {
		return captured;
	}
	
	public int getPromoted() {
		return promoted;
	}
	
	public int getFlag() {
		return flag;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public boolean wasPicked() {
		return picked;
	}
	
	public void setPicked(boolean picked) {
		this.picked = picked;
	}
	
	@Override
	public String toString() {
		return "Move[to = "+to+", from = "+from+", captured = "+captured+", promoted = "+promoted+", flag = "+flag+", hash = "+hash+", score = "+score+"]";
	}
	
}