package compiler.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import compiler.analysis.SemanticImpl;
import compiler.core.Expression;
import compiler.core.Type;
import compiler.core.Variable;
import compiler.core.operators.Operator;
import compiler.core.types.Bool;
import compiler.core.types.CharArray;
import compiler.core.types.VariableType;
import compiler.util.*;

public class CodeGenerator {
	private static final String TAB = "\t";
	
	private static final String LD = "LD";
	private static final String ST = "ST";
	
	private String code;
	private int address;
	private static CodeGenerator instance;
	private Register currentRegister;
	private Map<String, Variable> vars;
	private HashMap<Register, Variable> registers;
	private Object currentValue;
	
	private Stack<Object> expressionStack = new Stack<>();
	
	private CodeGenerator() {
		setAdress(100);
		initCode();
		currentRegister = Register.R1;
		registers = new HashMap<Register, Variable>();
		vars = new HashMap<>();
	}
	
	private void initCode(){
		code = "100:\tLD SP, #8000\n";
		iterateAddress();
	}
	
	private void setAdress(int value){
		address = value;
	}
	
	public static CodeGenerator getInstance() {
		if(instance == null) {
			instance = new CodeGenerator();
		}
		
		return instance;
	}
	
	public int getAddress() {
		return address;
	}
	
	private void assingRegister(Variable v) {
		registers.put(currentRegister, v);
		currentRegister = currentRegister.next();

	}

	public void currentValue(Object n) {
		this.currentValue = n;
	}

	public void addTerm(Object term) {
		expressionStack.add(term);
	}
	
	public Expression generateExpression() throws Exception {
		expressionStack.elements();
		Expression exp1 = null;
		Expression exp2 = null;
		Operator op = null;
		for (Object current : expressionStack) {
			if (current instanceof Expression) {
				exp2 = exp1;
				exp1 = (Expression) current;
			} else if (current instanceof Operator) {
				op = (Operator) current;
			}
			
			if (exp1 != null && exp2 != null && op != null) {
				Expression expression = generateSimpleExpression(op, exp1, exp2);
				exp1 = expression;
				exp2 = null;
				op = null;
			}
		}
		expressionStack.clear();
		return exp1;
	}

	public Expression generateSimpleExpression(Operator op, Expression e1, Expression e2) throws Exception {
		try {
			if (op == null) {
				return SemanticImpl.getInstance().getExpressionCheckingError((Expression)e1, (Expression)e2);
			}
		
			op.setExpressions(e1, e2);
			writeLine(op.getMnemonic(), String.valueOf(e1.getRegister()),
					String.valueOf(e1.getRegister()), String.valueOf(e2.getRegister()));
		
			Expression expression = new Expression(new Type(e1.getType().getName()));
			expression.setRegister(e1.getRegister());

			return expression;
		} catch (Throwable t) {
			t.printStackTrace();
			return SemanticImpl.getInstance().getExpressionCheckingError((Expression)e1, (Expression)e2);
		}
	}

	private void iterateAddress(){
		setAdress(getAddress()+ 8);
	}
	
	public void declare(Expression e) {
		e.setRegister(currentRegister);
		Type type = e.getType();
		currentRegister = currentRegister.next();
		
		if (type instanceof VariableType) {
			writeLine(LD, String.valueOf(e.getRegister()), ((VariableType) type).getValue().getIdentifier());
			return;
		}

		switch (type.getName()) {
			case "integer":
			case "real":
				writeLine(LD, String.valueOf(e.getRegister()), "#" + currentValue);
				break;
			case "array":
				writeLine(LD, String.valueOf(e.getRegister()), String.valueOf(((CharArray) e.getType()).getValue()));
			case "boolean":
				writeLine(LD, String.valueOf(e.getRegister()), String.valueOf(((Bool) e.getType()).getValue()));
				break;
			default:
				throw new RuntimeException("an error while declaring literal value");
		}
	}
	
	public void declareVariable(Variable variable) throws Exception{
		// FIXME REMOVE ME
	}
	
	public void assignment(Expression expression, String var) throws Exception {
		try {
			Type type = expression.getType();
			
			switch (type.getName()) {
				case "integer":
				case "real":
				case "array":
				case "boolean":
					writeLine(ST, var, String.valueOf(expression.getRegister()));
					break;
				default:
					throw new RuntimeException("an error while assingning literal value");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void GenerateConstantDefinition(Variable variable){
		code += getAddress() + ":\tLD " + currentRegister + ", #" + this.currentValue + "\n";
		iterateAddress();
		code += getAddress() + ":\tST " + variable.getIdentifier() + ", " +  currentRegister + "\n";
		assingRegister(variable);
		iterateAddress();
	}

	public void generateFinalAssemblyCode() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("assembly.txt")));
		writer.write(code);
		writer.close();
	}
	
	private void writeLine(String op, String field1, String field2, String field3) {
		code += String.format(Locale.getDefault(), "%s:\t%s %s, %s, %s\n",
				getAddress(), op, field1, field2, field3);
		iterateAddress();
	}
	
	private void writeLine(String op, String field1, String field2) {
		code += String.format(Locale.getDefault(), "%s:\t%s %s, %s\n",
				getAddress(), op, field1, field2);
		iterateAddress();
	}

	
}
