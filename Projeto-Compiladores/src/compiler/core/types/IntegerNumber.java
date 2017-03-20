package compiler.core.types;

import java.util.Locale;

import compiler.core.Type;

public class IntegerNumber extends Type {
	private final Integer value;

	public IntegerNumber(Integer value) {
		super("integer");
		
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.getDefault(),
				"%s [type: %s, value: %s]",
				this.getClass(), super.getName(), getValue());
	}
}
