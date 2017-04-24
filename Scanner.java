package cop5556sp17;

import java.util.ArrayList;

public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		

	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			//TODO IMPLEMENT THIS
			return chars.substring(pos, pos + length);
//			return null;
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			int line = 0, colmn = -1;
			for(int i = 0; i <= pos; i++) {
				if(chars.charAt(i) == '\n') {
					line++;
					colmn = -1;
				} else
					colmn++;
			}
			return(new LinePos(line, colmn));
//			return null;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException, IllegalNumberException{
			//TODO IMPLEMENT THIS
			int temp;
			if (kind == Kind.INT_LIT) {
				try {
					temp = Integer.parseInt(getText());
				} catch (NumberFormatException e){
					throw new IllegalNumberException("Number is out of bounds");
				}
				return temp;
			} else {
				throw new IllegalNumberException("Token is not an integer");
			}
//			return 0;
		}
		
		public boolean isKind(Kind fixed_kind) {
			if (this.kind == fixed_kind)
				return true;
			else return false;
		}
		
		@Override
		  public int hashCode() {
		   final int prime = 31;
		   int result = 1;
		   result = prime * result + getOuterType().hashCode();
		   result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		   result = prime * result + length;
		   result = prime * result + pos;
		   return result;
		  }

		  @Override
		  public boolean equals(Object obj) {
		   if (this == obj) {
		    return true;
		   }
		   if (obj == null) {
		    return false;
		   }
		   if (!(obj instanceof Token)) {
		    return false;
		   }
		   Token other = (Token) obj;
		   if (!getOuterType().equals(other.getOuterType())) {
		    return false;
		   }
		   if (kind != other.kind) {
		    return false;
		   }
		   if (length != other.length) {
		    return false;
		   }
		   if (pos != other.pos) {
		    return false;
		   }
		   return true;
		  }

		  private Scanner getOuterType() {
		   return Scanner.this;
		  }
	}

	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
	}
	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0; 
		//TODO IMPLEMENT THIS!!!!
		int length = chars.length();
		int ch, startpos = 0;
		while (pos <= length) {
			ch = pos < length ? chars.charAt(pos) : -1;
			if (Character.isDigit(ch)) {
				if (ch == '0') {
					tokens.add(new Token(Kind.INT_LIT, pos, 1));
					pos++;
				} else {
					startpos = pos;
					while (Character.isDigit(ch = pos < length ? chars.charAt(pos) : -1)) {
						pos++;
					}
					Token t = new Token(Kind.INT_LIT, startpos, (pos - startpos));
					t.intVal();
					tokens.add(t);
				}
			} else if (Character.isJavaIdentifierStart(ch)) {
				startpos = pos;
				while(Character.isJavaIdentifierPart(ch = pos < length ? chars.charAt(pos) : -1)) {
					pos++;
				}
				String s = "";
				s = chars.substring(startpos, pos);
				if (s.equals("integer")) {
					tokens.add(new Token(Kind.KW_INTEGER, startpos, (pos - startpos)));
				} else if (s.equals("boolean")) {
					tokens.add(new Token(Kind.KW_BOOLEAN, startpos, (pos - startpos)));
				} else if (s.equals("image")) {
					tokens.add(new Token(Kind.KW_IMAGE, startpos, (pos - startpos)));
				} else if (s.equals("url")) {
					tokens.add(new Token(Kind.KW_URL, startpos, (pos - startpos)));
				} else if (s.equals("file")) {
					tokens.add(new Token(Kind.KW_FILE, startpos, (pos - startpos)));
				} else if (s.equals("frame")) {
					tokens.add(new Token(Kind.KW_FRAME, startpos, (pos - startpos)));
				} else if (s.equals("while")) {
					tokens.add(new Token(Kind.KW_WHILE, startpos, (pos - startpos)));
				} else if (s.equals("if")) {
					tokens.add(new Token(Kind.KW_IF, startpos, (pos - startpos)));
				} else if (s.equals("true")) {
					tokens.add(new Token(Kind.KW_TRUE, startpos, (pos - startpos)));
				} else if (s.equals("false")) {
					tokens.add(new Token(Kind.KW_FALSE, startpos, (pos - startpos)));
				} else if (s.equals("blur")) {
					tokens.add(new Token(Kind.OP_BLUR, startpos, (pos - startpos)));
				} else if (s.equals("gray")) {
					tokens.add(new Token(Kind.OP_GRAY, startpos, (pos - startpos)));
				} else if (s.equals("convolve")) {
					tokens.add(new Token(Kind.OP_CONVOLVE, startpos, (pos - startpos)));
				} else if (s.equals("screenheight")) {
					tokens.add(new Token(Kind.KW_SCREENHEIGHT, startpos, (pos - startpos)));
				} else if (s.equals("screenwidth")) {
					tokens.add(new Token(Kind.KW_SCREENWIDTH, startpos, (pos - startpos)));
				} else if (s.equals("width")) {
					tokens.add(new Token(Kind.OP_WIDTH, startpos, (pos - startpos)));
				} else if (s.equals("height")) {
					tokens.add(new Token(Kind.OP_HEIGHT, startpos, (pos - startpos)));
				} else if (s.equals("xloc")) {
					tokens.add(new Token(Kind.KW_XLOC, startpos, (pos - startpos)));
				} else if (s.equals("yloc")) {
					tokens.add(new Token(Kind.KW_YLOC, startpos, (pos - startpos)));
				} else if (s.equals("hide")) {
					tokens.add(new Token(Kind.KW_HIDE, startpos, (pos - startpos)));
				} else if (s.equals("show")) {
					tokens.add(new Token(Kind.KW_SHOW, startpos, (pos - startpos)));
				} else if (s.equals("move")) {
					tokens.add(new Token(Kind.KW_MOVE, startpos, (pos - startpos)));
				} else if (s.equals("sleep")) {
					tokens.add(new Token(Kind.OP_SLEEP, startpos, (pos - startpos)));
				} else if (s.equals("scale")) {
					tokens.add(new Token(Kind.KW_SCALE, startpos, (pos - startpos)));
				} else {
					tokens.add(new Token(Kind.IDENT, startpos, (pos - startpos)));
				}
			} else {
				switch (ch) {
					case ' ': case '\t': case '\n': case '\r': {
						pos++;
						break;
					}
					case -1: {
						tokens.add(new Token(Kind.EOF,pos,0));
						pos++;
						break;
					}
					case '|': {
						pos++;
						ch = pos < length ? chars.charAt(pos) : -1;
						if (ch == '-') {
							pos++;
							ch = pos < length ? chars.charAt(pos) : -1;
							if (ch == '>') {
								tokens.add(new Token(Kind.BARARROW, (pos - 2), 3));
								pos++;
								break;
							}
							/* else {
								pos--;
								throw new Scanner.IllegalCharException("Expected > after |-");
							} */
							pos--;
						} else {
							pos--;
							tokens.add(new Token(Kind.OR, pos, 1));
							pos++;
							break;
						}
					}
					case '&': {
						tokens.add(new Token(Kind.AND, pos, 1));
						pos++;
						break;
					}
					case '<': {
						pos++;
						ch = pos < length ? chars.charAt(pos) : -1;
						if (ch == '=') {
							tokens.add(new Token(Kind.LE, (pos - 1), 2));
							pos++;
							break;
						} else if (ch == '-') {
							tokens.add(new Token(Kind.ASSIGN, (pos - 1), 2));
							pos++;
							break;
						} else {
							pos--;
							tokens.add(new Token(Kind.LT, pos, 1));
							pos++;
							break;
						}
					}
					case '>': {
						pos++;
						ch = pos < length ? chars.charAt(pos) : -1;
						if (ch == '=') {
							tokens.add(new Token(Kind.GE, (pos - 1), 2));
							pos++;
							break;
						} else {
							pos--;
							tokens.add(new Token(Kind.GT, pos, 1));
							pos++;
							break;
						}
					}
					case '+': {
						tokens.add(new Token(Kind.PLUS, pos, 1));
						pos++;
						break;
					}
					case '-': {
						pos++;
						ch = pos < length ? chars.charAt(pos) : -1;
						if (ch == '>') {
							tokens.add(new Token(Kind.ARROW, (pos - 1), 2));
							pos++;
							break;
						} else {
							pos--;
							tokens.add(new Token(Kind.MINUS, pos, 1));
							pos++;
							break;
						}
					}
					case '*': {
						tokens.add(new Token(Kind.TIMES, pos, 1));
						pos++;
						break;
					}
					case '/': {
						pos++;
						ch = pos < length ? chars.charAt(pos) : -1;
						if (ch == '*') {
							pos++;
							while (chars.charAt(pos) != '*' || chars.charAt(pos+1) != '/')
								pos++;
							pos += 2;
							break;
						} else {
							pos--;
							tokens.add(new Token(Kind.DIV, pos, 1));
							pos++;
							break;
						}
					}
					case '%': {
						tokens.add(new Token(Kind.MOD, pos, 1));
						pos++;
						break;
					}
					case '!': {
						pos++;
						ch = pos < length ? chars.charAt(pos) : -1;
						if (ch == '=') {
							tokens.add(new Token(Kind.NOTEQUAL, (pos - 1), 2));
							pos++;
							break;
						} else {
							pos--;
							tokens.add(new Token(Kind.NOT, pos, 1));
							pos++;
							break;
						}
					}
					case '=': {
						pos++;
						ch = pos < length ? chars.charAt(pos) : -1;
						if (ch == '=') {
							tokens.add(new Token(Kind.EQUAL, (pos - 1), 2));
							pos++;
							break;
						}
						else {
							pos--;
							throw new Scanner.IllegalCharException("Expected = after a =");
						}
					}
					case ';': {
						tokens.add(new Token(Kind.SEMI, pos, 1));
						pos++;
						break;
					}
					case ',': {
						tokens.add(new Token(Kind.COMMA, pos, 1));
						pos++;
						break;
					}
					case '(': {
						tokens.add(new Token(Kind.LPAREN, pos, 1));
						pos++;
						break;
					}
					case ')': {
						tokens.add(new Token(Kind.RPAREN, pos, 1));
						pos++;
						break;
					}
					case '{': {
						tokens.add(new Token(Kind.LBRACE, pos, 1));
						pos++;
						break;
					}
					case '}': {
						tokens.add(new Token(Kind.RBRACE, pos, 1));
						pos++;
						break;
					}
					default: {
						throw new Scanner.IllegalCharException("Illegal character:" + ch);
					}
				}
			}
		}
		return this;  
	}



	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}

	 /*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek() {
	    if (tokenNum >= tokens.size())
	        return null;
	    return tokens.get(tokenNum);
	}
	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
		return t.getLinePos();
//		return null;
	}


}
