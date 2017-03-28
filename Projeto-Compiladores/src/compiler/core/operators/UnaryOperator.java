package compiler.core.operators;

import compiler.core.Expression;

public abstract class UnaryOperator {
	private Expression rExpression;

	public void setExpression(Expression rExpression) {
		this.rExpression = rExpression;
	}

	public Expression getRExpression() {
		return rExpression;
	}
	
	public abstract String getMnemonic(); 
}
