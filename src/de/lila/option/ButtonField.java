package de.lila.option;

import java.util.function.Consumer;

public class ButtonField extends UCIDataField<Boolean> {
	
	public ButtonField(boolean defaultValue) {
		this(defaultValue, null);
	}
	
	public ButtonField(boolean defaultValue, Consumer<Boolean> onValueChange) {
		super("button", defaultValue, onValueChange);
	}
	
	@Override
	public void setValue(String s) {
		setValue(false);
	}
	
}
