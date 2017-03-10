package main;

import compiler.generated.Parser;
import compiler.generated.Scanner;
import java_cup.runtime.Symbol;
import compiler.analysis.SemanticImpl;
import compiler.generator.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainTest {
	
	public static void main(String[] args) throws IOException {
		String filePath1 = "test/TesteAnaliseSintatica1";
		String filePath2 = "test/TesteAnaliseSintatica2";
		String filePath3 = "test/TesteAnaliseSintatica3";
		String filePath4 = "test/TesteAnaliseSintatica4";
		String filePath5 = "test/Teste5";

		Scanner scanner1 = null,
				scanner2 = null,
				scanner3 = null,
				scanner4 = null,
				scanner5 = null;
		try {
			scanner1 = new Scanner(new BufferedReader(new FileReader(filePath1)));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			scanner2 = new Scanner(new BufferedReader(new FileReader(filePath2)));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			scanner3 = new Scanner(new BufferedReader(new FileReader(filePath3)));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			scanner4 = new Scanner(new BufferedReader(new FileReader(filePath4)));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			scanner5 = new Scanner(new BufferedReader(new FileReader(filePath5)));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		Parser parser1 = new Parser(scanner1);
		Parser parser2 = new Parser(scanner2);
		Parser parser3 = new Parser(scanner3);
		Parser parser4 = new Parser(scanner4);
		Parser parser5 = new Parser(scanner5);
		Symbol s = null;
		try {
			s = parser1.parse();
			SemanticImpl.getInstance().destroy();
			s = parser2.parse();
			SemanticImpl.getInstance().destroy();
			s = parser3.parse();
			SemanticImpl.getInstance().destroy();
			s = parser4.parse();
			SemanticImpl.getInstance().destroy();
			s = parser5.parse();
			SemanticImpl.getInstance().destroy();
			System.out.println("The compilation process was successfully finished!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
}