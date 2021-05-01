package de.lila.option;

import java.util.function.Consumer;

public class SpinField extends UCIDataField<Integer> {
	
	private int min;
	private int max;
	
	public SpinField(int defaultValue, int min, int max) {
		this(defaultValue, min, max, null);
	}
	
	public SpinField(int defaultValue, int min, int max, Consumer<Integer> onValueChange) {
		super("spin", defaultValue, onValueChange);
		
		this.min = min;
		this.max = max;
	}
	
	@Override
	public String toString() {
		return super.toString() + " min " + min + " max " + max;
	}
	
	@Override
	public void setValue(String s) {
		int i = Integer.parseInt(s);
		
		if(i > max) i = max;
		else if(i < min) i = min;
		
		setValue(i);
	}
	
}
