package de.lila.game;

public class BitBoard {
	
	private long value;
	
	public BitBoard() {
		this(BitBoards.NO_SQUARES);
	}
	
	public BitBoard(long value) {
		this.value = value;
	}
	
	public void clear() {
		value = BitBoards.NO_SQUARES;
	}
	
	public void or(long l) {
		value |= l;
	}
	
	public long orReturn(BitBoard b) {
		return value | b.getValue();
	}
	
	public void and(long l) {
		value &= l;
	}
	
	public long andReturn(long l) {
		return value & l;
	}
	
	public long andReturn(BitBoard b) {
		return value & b.getValue();
	}
	
	public void xor(long l) {
		value ^= l;
	}
	
	public long getValue() {
		return value;
	}
	
}
