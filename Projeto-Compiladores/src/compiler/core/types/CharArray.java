package compiler.core.types;

import java.util.Locale;

import compiler.core.Type;

public class CharArray extends Type {
	private final String value;

	public CharArray(String value) {
		super("array");
		
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.getDefault(),
				"%s [type: %s, value: %s]",
				this.getClass(), super.getName(), getValue());
	}
}
