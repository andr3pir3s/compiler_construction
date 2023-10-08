package exception;

import lexical.Scanner.*;
public class ParserException extends RuntimeException{

	public ParserException(String msg) {
		super(msg);
	}

}