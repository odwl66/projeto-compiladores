package compiler.core.types;

import java.util.Locale;

import compiler.core.Type;

public class Bool extends Type {
	private final Boolean value;

	public Bool(Boolean value) {
		super("boolean");
		
		this.value = value;
	}
	
	public Boolean getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.getDefault(),
				"%s [type: %s, value: %s]",
				this.getClass(), super.getName(), getValue());
	}
}
