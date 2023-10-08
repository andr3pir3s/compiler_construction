package main;

import exception.ParserException;
import exception.ScannerException;
import lexical.Scanner;
import syntax.Parser;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner("source_code.mc");
		Parser parser = new Parser(sc);
		try {
			parser.program();
			System.out.println("Compilation Successful!");
		} catch (ScannerException e) {
			System.out.println("Lexical Error: " + e.getMessage());
		} catch (ParserException e) {
			System.out.println("Syntax Error: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Generic Error: " + e.getMessage());
		}
	}
}
