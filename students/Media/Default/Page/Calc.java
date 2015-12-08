package edu.spbgu;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.ParseException;
public class Calc {
	
	 Lexer lexer = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Simple calculator ver 0.1. Use \"quit\" command for exit");
		String line = "";
		Calc calc = new Calc();
		try {
			LineNumberReader is = new LineNumberReader(new InputStreamReader(System.in));
			for(;;) {
				line = is.readLine();
				// process internal commands
				if (line.toLowerCase().equals("quit")) {
					System.out.println("Quiting");
					return;
				}
				calc.setLexer(new Lexer(line));
				// Calculate
				double result;
				try {
					result = calc.calculate();
				    System.out.println(result);
				} catch (ParseException e) {
					e.printStackTrace();
				}
		}
		} catch (IOException e) {
			System.out.println("Unexpected errror, Exiting");
			e.printStackTrace();
		}
	}
	private double calculate() throws ParseException {
		lexer.nextToken();
		return E();
	}
	private double E() throws ParseException {
		Token t = lexer.getToken();
		if (t == Token.EOI) {
			return 0;
		}
		// E->TE1
		if(t.isNumber() || t == Token.LEFT) {
			double attr = T();
			return E1(attr);
		}
		throw new ParseException(" ( or number is expeced", lexer.getPosition());

	}
	private double E1(double inh_attr) throws ParseException {
		Token t = lexer.getToken();
		// E1->+TE1
		if(t == Token.PLUS) {
			lexer.nextToken();
			double attr = inh_attr + T();
			return E1(attr);
		}		
		// E1->+TE1
		if(t == Token.MINUS) {
			lexer.nextToken();
			double attr = inh_attr - T();
			return E1(attr);
		}
		// E1->e
		if (t == Token.EOI || t ==Token.RIGHT) {
			return inh_attr;
		}
		throw new ParseException(" + or - expected", lexer.getPosition());
	}
	
	private double T() throws ParseException {
		Token t = lexer.getToken();
		// T->FT1
		if(t.isNumber() || t == Token.LEFT) {
			double attr = F();
			return T1(attr);
		}
		throw new ParseException(" ( or number is expeced", lexer.getPosition());

	}
	private double T1(double inh_attr) throws ParseException {
		Token t = lexer.getToken();
		// T1->*FT1
		if(t == Token.MUL) {
			lexer.nextToken();
			double attr = inh_attr * F();
			return T1(attr);
		}		
		// T1->/FT1
		if(t == Token.DIV) {
			lexer.nextToken();
			double attr = inh_attr / F();
			return T1(attr);
		}
		// T1->e
		if (t == Token.EOI || t ==Token.RIGHT || t == Token.PLUS || t== Token.MINUS) {
			return inh_attr;
		}
		throw new ParseException(" * or / expected", lexer.getPosition());
	}	
	private double F() throws ParseException {
		Token t = lexer.getToken();
		// F->num
		if(t.isNumber()) {
			lexer.nextToken();
			double attr = t.getValue();
			return attr;
		}
		// F->(E)
		if(t == Token.LEFT) {
			lexer.nextToken();
			double attr = E();
			if (lexer.getToken() == Token.RIGHT) {
				lexer.nextToken();
				return attr;
			}
		}
		throw new ParseException(" ) expected", lexer.getPosition());
	}

	private void setLexer(Lexer lexer) {
		this.lexer=lexer;
	}

}

class Lexer {
	private String source; 
	private int position;
	Token current;
	Lexer (String source) {
		this.source=source;
		position = 0;
	}
	
	public int getPosition() {
		return position;
	}

	public Token nextToken() throws ParseException {
		if (position >= source.length()) {
			current = Token.EOI;
			return Token.EOI;
		}
		char c = source.charAt(position);
		if(Character.isWhitespace(c)) {
			skipWhitespaces();
			return nextToken();
		}
		if ((c >='0' && c <='9') || c=='.') {
			current = getNumber();
		} else if (c == '(') {
			position++;
			current =  Token.LEFT;
		} else if (c == ')') {
			position++;
			current =  Token.RIGHT;
		}else  current = getOperation();
		return current;
	}
	public Token getToken() {
		return current;
	}
	
	public Token getOperation() throws ParseException {
		char c = source.charAt(position++);
		switch (c) {
			case '+': return Token.PLUS;
			case '-': return Token.MINUS;
			case '*': return Token.MUL;
			case '/': return Token.DIV;
		}
		throw new ParseException("Unknown operation: " + c, position-1);
	}
	
	public Token getNumber() throws ParseException {
	    int start=position;
		for (; position < source.length()  ; position++) {
			char c = source.charAt(position);
			if (!(Character.isDigit(c) || c =='.')) break; 
		}
		try {
			double value = Double.parseDouble(source.substring(start,position));
			return new Token (value);
		} catch (NumberFormatException e) {
			throw new ParseException("NumberFormatException", start);
		}
	}
	public void skipWhitespaces() {
		for (; position < source.length()  ; position++) {
			char c = source.charAt(position);
			if (!Character.isWhitespace(c)) break; 
		}
	}
}


class Token {
	public final static int TYPE_NUMBER=1;
	public final static int TYPE_OPERATION=2;
	public final static int TYPE_EOI=3;
	private static final int TYPE_LEFT = 4;
	private static final int TYPE_RIGHT = 5;
	
	public static final Token RIGHT=new Token(TYPE_LEFT);
	public static final Token LEFT=new Token(TYPE_RIGHT);
	public final static Token PLUS=new Token(TYPE_OPERATION);
	public final static Token MINUS=new Token(TYPE_OPERATION);
	public final static Token MUL=new Token(TYPE_OPERATION);
	public final static Token DIV=new Token(TYPE_OPERATION);
	public final static Token EOI=new Token(TYPE_EOI);
	
    private double value;
    private int type;
    Token(int type) {
    	this.type = type;
    	value =0;
    }
    


	Token (double value) {
    	type= TYPE_NUMBER;
    	this.value=value;
    }
    
    boolean isNumber() {
    	return type == TYPE_NUMBER;
    }
    
    boolean isOperation() {
    	return type == TYPE_OPERATION;
    }
    
    double getValue() {
    	return value;
    }
}
	
	