package compiler.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import compiler.analysis.SemanticImpl;
import compiler.core.Expression;
import compiler.core.Variable;
import compiler.util.*;
import jflex.gui.GeneratorThread;

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
	public void currentValue(Object n){
		this.currentValue = n;
	}
	
	private void iterateAddress(){
		setAdress(getAddress()+ 8);
	}
	
	public void declareVariable(Variable variable) throws Exception{
		SemanticImpl.getInstance()
		.getVariable(variable.getIdentifier())
		.setRegister(currentRegister);

		currentRegister = currentRegister.next();
	}
	
	public void assignment(Expression expression, String var) throws Exception {
		Variable var2 = SemanticImpl.getInstance().getVariable(var);
		switch (expression.getTarget()) {
		case VARIABLE:
			writeLine(LD, String.valueOf(var2.getRegister()), ((Variable) currentValue).getIdentifier());
			writeLine(ST, var, String.valueOf(var2.getRegister()));
			break;
		case BOOLEAN:
			break;
		case STRING:
			break;
		case NUMBER:
			writeLine(LD, String.valueOf(var2.getRegister()), "#" + currentValue);
			writeLine(ST, var, String.valueOf(var2.getRegister()));
			break;
		default:
			break;
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
	
	
	private void writeLine(String op, String field1, String field2) {
		code += String.format(Locale.getDefault(), "%s:\t%s %s, %s\n",
				getAddress(), op, field1, field2);
		iterateAddress();
	}

	
}
