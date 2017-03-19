package compiler.core.types;

import java.util.Locale;

import compiler.core.Type;

public class RealNumber extends Type {
	private final Float value;

	public RealNumber(Float value) {
		super("real");
		
		this.value = value;
	}
	
	public Float getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.getDefault(),
				"%s [type: %s, value: %s]",
				this.getClass(), super.getName(), getValue());
	}
}
