package de.lila.option;

import java.util.function.Consumer;

public class CheckField extends UCIDataField<Boolean> {
	
	public CheckField(boolean defaultValue) {
		this(defaultValue, null);
	}
	
	public CheckField(boolean defaultValue, Consumer<Boolean> onValueChange) {
		super("check", defaultValue, onValueChange);
	}
	
	@Override
	public void setValue(String s) {
		setValue(Boolean.parseBoolean(s));
	}
	
}
