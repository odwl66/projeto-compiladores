package compiler.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import compiler.core.Expression;
import compiler.core.Variable;
import compiler.util.*;
import jflex.gui.GeneratorThread;

public class CodeGenerator {
	
	private String code;
	private int address;
	private static CodeGenerator instance;
	private Register currentRegister;
	private HashMap<Register, Variable> registers;
	private Object currentValue;
	
	private CodeGenerator() {
		setAdress(100);
		initCode();
		currentRegister = Register.R1;
		registers = new HashMap<Register, Variable>();
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
	
	public void declareVariable(Variable variable){
		code += getAddress() + ":\tLD " + currentRegister + ", " + variable.getIdentifier() + "\n";
		assingRegister(variable);
		iterateAddress();
	}
	
	public void GenerateConstantDefinition(Variable variable){
		code += getAddress() + ":\tLD " + currentRegister + ", " + variable.getIdentifier() + "\n";
		assingRegister(variable);
		iterateAddress();
		code += getAddress() + ":\tST " + variable.getIdentifier() + ", " +  this.currentValue + "\n";
		iterateAddress();
	}
	
	
	
	
	public void generateFinalAssemblyCode() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("assembly.txt")));
		writer.write(code);
		writer.close();
	}

	
}
