package compiler.core;

import compiler.util.Register;

public class Variable implements Parameter {
	
	Type type;
	String identifier;
	Expression value;
	Register register;
	boolean isConstant, isInitialized;

	public Variable(String identifier, Type type, boolean isConstant) {
		this.type = type;
		this.identifier = identifier;
		this.isConstant = isConstant;
		this.isInitialized = isConstant;
	}
	
	public Variable(String identifier, Type type){
		this(identifier, type, false);
	}
	
	public Variable(String identifier, Type type, Expression value){
		this.type = type;
		this.identifier = identifier;
		this.value = value;
		this.isConstant = false;
		this.isInitialized = false;
	}

	public Type getType() {
		return this.type;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Expression getValue() {
		return value;
	}

	public void setValue(Expression value) {
		this.value = value;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public void setRegister(Register register) {
		this.register = register;
//		getValue().setRegister(register);
	}
	
	public Register getRegister() {
		return register;
	}
	
	public boolean isConstant() {
		return isConstant;
	}

	public void setConstant(boolean isConstant) {
		this.isConstant = isConstant;
	}
	
	@Override
	public String toString(){
		return this.identifier + " of type: " + getType().getName();
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}
}