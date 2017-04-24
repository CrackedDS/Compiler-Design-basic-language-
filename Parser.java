package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

import java.awt.List;
import java.util.ArrayList;

import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 * @throws IllegalNumberException 
	 * @throws NumberFormatException 
	 */
	Program parse() throws SyntaxException, NumberFormatException, IllegalNumberException {
		Program p = null;
		p = program();
		matchEOF();
		return p;
	}

	//expression ∷= term ( relOp term)*
	//relOp ∷=  LT | LE | GT | GE | EQUAL | NOTEQUAL 
	Expression expression() throws SyntaxException, NumberFormatException, IllegalNumberException {
		//TODO
		Expression e0 = null;
		Expression e1 = null;
		Token tk = t;
		e0 = term();
		while (t.isKind(LT) || t.isKind(LE) || t.isKind(GT) || t.isKind(GE) || t.isKind(EQUAL) || t.isKind(NOTEQUAL)) {
			Token op = t;
			consume();
			e1 = term();
			e0 = new BinaryExpression(tk, e0, op, e1);
		}
		return e0;
		//throw new UnimplementedFeatureException();
	}

	//term ∷= elem ( weakOp  elem)*
	//weakOp  ∷= PLUS | MINUS | OR
	Expression term() throws SyntaxException, NumberFormatException, IllegalNumberException {
		//TODO
		Expression e0 = null;
		Expression e1 = null;
		Token tk = t;
		e0 = elem();
		while (t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR)) {
			Token op = t;
			consume();
			e1 = elem();
			e0 = new BinaryExpression(tk, e0, op, e1);
		}
		return e0;
		//throw new UnimplementedFeatureException();
	}

	//elem ∷= factor ( strongOp factor)*
	//strongOp ∷= TIMES | DIV | AND | MOD
	Expression elem() throws SyntaxException, NumberFormatException, IllegalNumberException {
		//TODO
		Expression e0 = null;
		Expression e1 = null;
		Token tk = t;
		e0 = factor();
		while (t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND) || t.isKind(MOD)) {
			Token op = t;
			consume();
			e1 = factor();
			e0 = new BinaryExpression(tk, e0, op, e1);
		}
		return e0;
		//throw new UnimplementedFeatureException();
	}

	Expression factor() throws SyntaxException, NumberFormatException, IllegalNumberException {
		Expression e = null;
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			e = new IdentExpression(t);
			consume();
		}
			break;
		case INT_LIT: {
			e = new IntLitExpression(t);
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			e = new BooleanLitExpression(t);
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			e = new ConstantExpression(t);
			consume();
		}
			break;
		case LPAREN: {
			consume();
			e = expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor token: " + t.kind);
		}
		return e;
	}

	//block ::= { ( dec | statement) * }
	//dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT
	//filterOp ::= OP_BLUR |OP_GRAY | OP_CONVOLVE
	//frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC |KW_YLOC
	//imageOp ::= OP_WIDTH |OP_HEIGHT | KW_SCALE
	Block block() throws SyntaxException, NumberFormatException, IllegalNumberException {
		//TODO
		Block b = null;
		ArrayList<Dec> d = new ArrayList<Dec>();
		ArrayList<Statement> s = new ArrayList<Statement>();
		Token tk = t;
		match(LBRACE);
		while(t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) || t.isKind(KW_IMAGE) || t.isKind(KW_FRAME) || 
				t.isKind(OP_SLEEP) || t.isKind(KW_WHILE) || t.isKind(KW_IF) || t.isKind(IDENT) || t.isKind(OP_BLUR) ||
				t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE) || t.isKind(KW_SHOW) || t.isKind(KW_HIDE) || t.isKind(KW_MOVE) ||
				t.isKind(KW_XLOC) || t.isKind(KW_YLOC) || t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE)) {
			Kind k = t.kind;
			switch (k) {
				case KW_INTEGER:
				case KW_BOOLEAN:
				case KW_IMAGE:
				case KW_FRAME: {
					d.add(dec());
					break;
				}
				case OP_SLEEP:
				case KW_WHILE:
				case KW_IF:
				case IDENT:
				case OP_BLUR:
				case OP_GRAY:
				case OP_CONVOLVE:
				case KW_SHOW:
				case KW_HIDE:
				case KW_MOVE:
				case KW_XLOC:
				case KW_YLOC:
				case OP_WIDTH:
				case OP_HEIGHT:
				case KW_SCALE: {
					s.add(statement());
					break;
				}
			}
		}
		b = new Block(tk, d, s);
		match(RBRACE);
		return b;
		//throw new UnimplementedFeatureException();
	}

	//program ::=  IDENT block
	//program ::=  IDENT param_dec ( , param_dec )*   block
	Program program() throws SyntaxException, NumberFormatException, IllegalNumberException {
		//TODO
		Program p = null;
		Block b = null;
		ArrayList<ParamDec> pd = new ArrayList<ParamDec>();
		Token programname = t;
		match(IDENT);
		if (t.isKind(LBRACE))
			b = block();
		else {
			pd.add(paramDec());
			while (t.isKind(COMMA)) {
				consume();
				pd.add(paramDec());
			}
			b = block();
		}
		p = new Program(programname, pd, b);
		return p;
		//throw new UnimplementedFeatureException();
	}

	//paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN)   IDENT
	ParamDec paramDec() throws SyntaxException {
		//TODO
		ParamDec pd = null;
		Token ty = t;
		match(KW_URL, KW_FILE, KW_INTEGER, KW_BOOLEAN);
		Token id = t;
		match(IDENT);
		pd = new ParamDec(ty, id);
		return pd;
		//throw new UnimplementedFeatureException();
	}

	//dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT
	Dec dec() throws SyntaxException {
		//TODO
		Dec d = null;
		Token ty = t;
		match(KW_INTEGER, KW_BOOLEAN, KW_IMAGE, KW_FRAME);
		Token id = t;
		match(IDENT);
		d = new Dec(ty, id);
		return d;
		//throw new UnimplementedFeatureException();
	}

	//statement ::=   OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
	Statement statement() throws SyntaxException, NumberFormatException, IllegalNumberException {
		//TODO
		Statement s = null;
		Token tk = t;
		Kind k = t.kind;
		switch (k) {
			case OP_SLEEP: {
				Expression e = null;
				consume();
				e = expression();
				match(SEMI);
				s = new SleepStatement(tk, e);
				break;
			}
			//whileStatement ::= KW_WHILE ( expression ) block
			case KW_WHILE:{
				Expression e = null;
				Block b = null;
				consume();
				match(LPAREN);
				e = expression();
				match(RPAREN);
				b = block();
				s = new WhileStatement(tk, e, b);
				break;
			}
			case KW_IF: {
				Expression e = null;
				Block b = null;
				consume();
				match(LPAREN);
				e = expression();
				match(RPAREN);
				b = block();
				s = new IfStatement(tk, e, b);
				break;
			}
			//filterOp ::= OP_BLUR |OP_GRAY | OP_CONVOLVE
			//frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC |KW_YLOC
			//imageOp ::= OP_WIDTH |OP_HEIGHT | KW_SCALE
			case OP_BLUR:
			case OP_GRAY:
			case OP_CONVOLVE:
			case KW_SHOW:
			case KW_HIDE:
			case KW_MOVE:
			case KW_XLOC:
			case KW_YLOC:
			case OP_WIDTH:
			case OP_HEIGHT:
			case KW_SCALE: {
				s = chain();
				match(SEMI);
				break;
			}
			case IDENT: {
				if(scanner.peek().isKind(ASSIGN)) {
					IdentLValue v = new IdentLValue(tk);
					Expression e = null;
					consume();
					match(ASSIGN);
					e = expression();
					match(SEMI);
					s = new AssignmentStatement(tk, v, e);
					break;
				} else if(scanner.peek().isKind(ARROW) || scanner.peek().isKind(BARARROW)) {
					s = chain();
					match(SEMI);
					break;
				}
			}
		}
		return s;
		//throw new UnimplementedFeatureException();
	}

	//chain ::=  chainElem arrowOp chainElem ( arrowOp  chainElem)*
	Chain chain() throws SyntaxException, NumberFormatException, IllegalNumberException {
		//TODO
		Chain c = null;
		ChainElem ce = null;
		Token tk = t;
		c = chainElem();
		Token arrow = t;
		match(ARROW, BARARROW);
		ce = chainElem();
		c = new BinaryChain(tk, c, arrow, ce);
		while(t.isKind(ARROW) || t.isKind(BARARROW)) {
			Token arr = t;
			consume();
			ce = chainElem();
			c = new BinaryChain(tk, c, arr, ce);
		}
		return c;
		//throw new UnimplementedFeatureException();
	}

	//chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
	ChainElem chainElem() throws SyntaxException, NumberFormatException, IllegalNumberException {
		//TODO
		ChainElem ce = null;
		Token tk = t;
		Kind k = t.kind;
		switch (k) {
			case OP_BLUR:
			case OP_GRAY:
			case OP_CONVOLVE: {
				Tuple tu = null;
				consume();
				tu = arg();
				ce = new FilterOpChain(tk, tu);
				break;
			}
			case KW_SHOW:
			case KW_HIDE:
			case KW_MOVE:
			case KW_XLOC:
			case KW_YLOC: {
				Tuple tu = null;
				consume();
				tu = arg();
				ce = new FrameOpChain(tk, tu);
				break;
			}
			case OP_WIDTH:
			case OP_HEIGHT:
			case KW_SCALE: {
				Tuple tu = null;
				consume();
				tu = arg();
				ce = new ImageOpChain(tk, tu);
				break;
			}
			case IDENT: {
				consume();
				ce = new IdentChain(tk);
				break;
			}
		}
		return ce;
		//throw new UnimplementedFeatureException();
	}

	//arg ::= ε | ( expression (   ,expression)* )
	Tuple arg() throws SyntaxException, NumberFormatException, IllegalNumberException {
		//TODO
		Tuple tu = null;
		Token tk = null;
		ArrayList<Expression> e = new ArrayList<Expression>();
		if(t.isKind(LPAREN)) {
			tk = t;
			consume();
			e.add(expression());
			while(t.isKind(COMMA)) {
				consume();
				e.add(expression());
			}
			match(RPAREN);
		}
		tu = new Tuple(tk, e);
		return tu;
		//throw new UnimplementedFeatureException();
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		for(Kind kin : kinds){
			if (t.isKind(kin)) {
				return consume();
			}
	    }
		throw new SyntaxException("saw " + t.kind + "expected " + kinds);
		//return null; //replace this statement
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}
