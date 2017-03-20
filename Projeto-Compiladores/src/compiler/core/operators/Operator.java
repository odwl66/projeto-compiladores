package compiler.core.operators;

import compiler.core.Expression;

public abstract class Operator {
	private Expression lExpression;
	private Expression rExpression;

	public void setExpressions(Expression lExpression, Expression rExpression) {
		this.lExpression = lExpression;
		this.rExpression = rExpression;
	}

	public Expression getRExpression() {
		return rExpression;
	}

	public Expression getLExpression() {
		return lExpression;
	}
	
	public abstract String getMnemonic(); 
}
