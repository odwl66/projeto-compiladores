package compiler.core.operators;

public class Not extends UnaryOperator {
	private static final String MNEMONIC = "NOT";

	@Override
	public String getMnemonic() {
		return MNEMONIC;
	}
}
