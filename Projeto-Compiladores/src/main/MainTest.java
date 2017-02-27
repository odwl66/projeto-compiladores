package main;

import compiler.generated.Parser;
import compiler.generated.Scanner;
import java_cup.runtime.Symbol;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainTest {
	
	public static void main(String[] args) throws IOException {
		String filePath = "test/TesteAnaliseSintatica1";
		Scanner scanner = null;
		try {
			scanner = new Scanner(new BufferedReader(new FileReader(filePath)));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		Parser parser = new Parser(scanner);
		Symbol s = null;
		try {
			s = parser.parse();
			System.out.println("The compilation process was successfully finished!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
}