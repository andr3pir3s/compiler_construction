package main;

import lexical.Scanner;
import lexical.Token;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner("source_code.mc");
		Token tk;
		try {
			do {
				tk = sc.nextToken();
				System.out.println(tk);
			} while (tk != null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
