package compiler.core.types;

import java.util.Locale;

import compiler.core.Type;
import compiler.core.Variable;

public class VariableType extends Type {
	private final Variable value;

	public VariableType(Variable value) {
		super(value.getType().getName());
		
		this.value = value;
	}
	
	public Variable getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.getDefault(),
				"%s [type: %s, value: %s]",
				this.getClass(), super.getName(), getValue());
	}
}
