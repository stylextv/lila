package de.lila.option;

import java.util.function.Consumer;

public class StringField extends UCIDataField<String> {
	
	public StringField(String defaultValue) {
		this(defaultValue, null);
	}
	
	public StringField(String defaultValue, Consumer<String> onValueChange) {
		super("string", defaultValue, onValueChange);
	}
	
}
