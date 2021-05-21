package de.lila.option;

import java.util.function.Consumer;

public abstract class UCIDataField<T> {
	
	private String type;
	
	private T defaultValue;
	
	private T value;
	
	private Consumer<T> onValueChange;
	
	public UCIDataField(String type, T defaultValue, Consumer<T> onValueChange) {
		this.type = type;
		this.defaultValue = defaultValue;
		
		this.onValueChange = onValueChange;
		
		setValue(defaultValue);
	}
	
	public abstract void setValue(String s);
	
	public String getType() {
		return type;
	}
	
	public T getDefaultValue() {
		return defaultValue;
	}
	
	public T getValue() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
		
		if(onValueChange != null) onValueChange.accept(value);
	}
	
	@Override
	public String toString() {
		return "type " + type + " default " + defaultValue;
	}
	
}
