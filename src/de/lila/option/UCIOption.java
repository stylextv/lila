package de.lila.option;

public class UCIOption {
	
	private String name;
	
	private UCIDataField<?> field;
	
	public UCIOption(String name, UCIDataField<?> field) {
		this.name = name;
		this.field = field;
		
		Options.registerOption(this);
	}
	
	public String getName() {
		return name;
	}
	
	public UCIDataField<?> getField() {
		return field;
	}
	
	@Override
	public String toString() {
		return "option name " + name + " " + field.toString();
	}
	
}
