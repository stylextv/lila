package de.lila.option;

import java.util.function.Consumer;

public class ComboField extends UCIDataField<Integer> {
	
	private String[] choices;
	
	public ComboField(int defaultValue, String[] choices) {
		this(defaultValue, choices, null);
	}
	
	public ComboField(int defaultValue, String[] choices, Consumer<Integer> onValueChange) {
		super("combo", defaultValue, onValueChange);
		
		this.choices = choices;
	}
	
	public int getChoiceIndex(String s) {
		for(int i = 0; i < choices.length; i++) {
			if(choices[i].equalsIgnoreCase(s)) return i;
		}
		
		return getDefaultValue();
	}
	
	@Override
	public String toString() {
		String s = "type " + getType() + " default " + choices[getDefaultValue()];
		
		for(String choice : choices) {
			s = s + " var " + choice;
		}
		
		return s;
	}
	
	@Override
	public void setValue(String s) {
		setValue(getChoiceIndex(s));
	}
	
}
