package compiler.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
	private static final String SP = "SP";

	private int address;
	private static CodeGenerator instance;
	private Register currentRegister;
	private Map<String, Variable> vars;
	private HashMap<Register, Variable> registers;
	private Object currentValue;
	
	private Stack<Object> expressionStack = new Stack<>();
	private List<Instruction> code = new LinkedList<>();
	
	private Instruction currentIf;
	private List<Instruction> ifInstructions = new LinkedList<>();
	private List<Instruction> statementInstructions = new LinkedList<>();
	private boolean isStatement = false;
	
	private CodeGenerator() {
		setAdress(100);
		initCode();
		currentRegister = Register.R1;
		registers = new HashMap<Register, Variable>();
		vars = new HashMap<>();
	}
	
	private void initCode(){
		writeLine(LD, SP, "#8000");
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

	public void finishStatement() {
		outOfIf();
		isStatement = false;

		for (Instruction instruction : statementInstructions) {
			
			if (instruction.code.startsWith("BR_TMP")) {
				code.add(new Instruction(instruction.address, "BR " + getAddress()));
			} else {
				code.add(instruction);
			}
			
		}
		statementInstructions.clear();
	}

	public void declareIfStatement(Expression exp) {
		isStatement = true;
		System.out.println("====> declareIfStatement - START IF");
		System.out.println("====> declareIfStatement - exp: " + exp.getRegister());

		currentIf = new Instruction(getAddress(), "BEQZ " + String.valueOf(exp.getRegister()) + ", ");
		iterateAddress();
	}
	
	public void outOfIf() {
		System.out.println("====> declareIfStatement - END IF3");
		if (!isStatement) {
			return;
		}
		ifInstructions.add(0, new Instruction(currentIf.address, currentIf.code + (getAddress() + 8)));
		Collections.sort(ifInstructions, new Comparator<Instruction>() {

			@Override
			public int compare(Instruction o1, Instruction o2) {
				return o1.address - o2.address;
			}
		});

		writeLine("BR_TMP", "");

		for (Instruction instruction : ifInstructions) {
			statementInstructions.add(instruction);
		}
		ifInstructions.clear();
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
				break;
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
	
	public void assignment(Expression expression, String var) {
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
	
	public void declaredConstant(Variable var, Expression expression) {
		try {
			declare(expression);
			assignment(expression, var.getIdentifier());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void generateFinalAssemblyCode() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("assembly.txt")));
		try {
			for (Instruction instruction : code) {
				writer.write(instruction.toString());
				writer.newLine();
			}
		} finally {
			writer.close();
		}
	}
	
	private void writeLine(String op, String field1, String field2, String field3) {
		Instruction instruction = new Instruction(getAddress(),
				String.format(Locale.getDefault(), "%s %s, %s, %s",
				op, field1, field2, field3));
		if (isStatement) {
			ifInstructions.add(instruction);
		} else {
			code.add(instruction);
		}
		iterateAddress();
	}
	
	private void writeLine(String op, String field1, String field2) {
		Instruction instruction = new Instruction(getAddress(),
				String.format(Locale.getDefault(), "%s %s, %s",
				op, field1, field2));
		if (isStatement) {
			ifInstructions.add(instruction);
		} else {
			code.add(instruction);
		}
		iterateAddress();
	}
	
	private void writeLine(String op, String field1) {
		Instruction instruction = new Instruction(getAddress(),
				String.format(Locale.getDefault(), "%s %s",
				op, field1));
		if (isStatement) {
			ifInstructions.add(instruction);
		} else {
			code.add(instruction);
		}
		iterateAddress();
	}
	
	private class Instruction {
		public final int address;
		public final String code;
		
		public Instruction(int address, String code) {
			this.address = address;
			this.code = code;
		}
		
		@Override
		public String toString() {
			return String.format(Locale.getDefault(), "%d:\t%s", address, code);
		}
	}
}
