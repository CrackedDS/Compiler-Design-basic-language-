package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.ArrayList;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Chain chain;
		Token arrow;
		ChainElem chainelem;

		chain = binaryChain.getE0();
		arrow = binaryChain.getArrow();
		chainelem = binaryChain.getE1();
		chain.visit(this, arg);
		chainelem.visit(this, arg);

		switch (arrow.kind) {
		case ARROW:
			switch (chain.getChainType()) {
			case URL:
			case FILE:
				if (chainelem.getChainType().equals(IMAGE)) {
					binaryChain.setChainType(IMAGE);
				} else throw new TypeCheckException("Wrong ChainElemType");
				break;
			case FRAME:
				if (chainelem instanceof FrameOpChain) {
					if (chainelem.getFirstToken().kind.equals(KW_XLOC) || chainelem.getFirstToken().kind.equals(KW_YLOC)) {
						binaryChain.setChainType(INTEGER);
					} else if (chainelem.getFirstToken().kind.equals(KW_SHOW) || chainelem.getFirstToken().kind.equals(KW_HIDE) || chainelem.getFirstToken().kind.equals(KW_MOVE)) {
						binaryChain.setChainType(FRAME);
					} else throw new TypeCheckException("Incorrect token \"" + chainelem.getFirstToken().getText() + "\" at " + chainelem.getFirstToken().getLinePos() + ".");
				} else throw new TypeCheckException("ChainElem not an instance of FrameOpChain");
				break;
			case IMAGE:
				if (chainelem.getChainType().equals(FRAME)) {
					binaryChain.setChainType(FRAME);
				} else if (chainelem.getChainType().equals(FILE)) {
					binaryChain.setChainType(NONE);
				} else if (chainelem instanceof ImageOpChain) {
					Kind firsttoken = chainelem.getFirstToken().kind;
					if (firsttoken.equals(KW_SCALE)) {
						binaryChain.setChainType(IMAGE);
					} else if (firsttoken.equals(OP_WIDTH) || firsttoken.equals(OP_HEIGHT)) {
						binaryChain.setChainType(INTEGER);
					} else throw new TypeCheckException("Wrong first token for ChainElem2");
				} else if (chainelem instanceof IdentChain && chainelem.getChainType().equals(IMAGE)) {
					binaryChain.setChainType(IMAGE);
				} else if (chainelem instanceof FilterOpChain) {
					Kind firsttoken = chainelem.getFirstToken().kind;
					if (firsttoken.equals(OP_BLUR) || firsttoken.equals(OP_GRAY) || firsttoken.equals(OP_CONVOLVE)) {
						binaryChain.setChainType(IMAGE);
					} else throw new TypeCheckException("Wrong first token for ChainElem3");
				} else throw new TypeCheckException("ChainElem not a part of any instance or type");
				break;
			case INTEGER:
				if (chainelem instanceof IdentChain && chainelem.getChainType().equals(INTEGER))
					binaryChain.setChainType(INTEGER);
				break;
			default:
				throw new TypeCheckException("Wrong Chain type");
			}
			break;
		case BARARROW:
			if (chain.getChainType().equals(IMAGE)) {
				if (chainelem instanceof FilterOpChain) {
					Kind firsttoken = chainelem.getFirstToken().kind;
					if (firsttoken.equals(OP_BLUR) || firsttoken.equals(OP_GRAY) || firsttoken.equals(OP_CONVOLVE)) {
						binaryChain.setChainType(IMAGE);
					} else throw new TypeCheckException("Wrong first token for ChainElem5");
				} else throw new TypeCheckException("ChainElem not an instance of FilterOpChain");
			} else throw new TypeCheckException("Wrong Chain type");
			break;
		default:
			throw new TypeCheckException("Operator not an arrow or bararrow");
		}
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e0;
		Token op;
		Expression e1;
		e0 = binaryExpression.getE0();
		op = binaryExpression.getOp();
		e1 = binaryExpression.getE1();
		e0.visit(this, arg);
		e1.visit(this, arg);

		switch (op.kind) {
		case PLUS:
		case MINUS:
			switch (e0.getExpressionType()) {
			case IMAGE:
				if (e1.getExpressionType().equals(IMAGE)) {
					binaryExpression.setExpressionType(IMAGE);
				} else throw new TypeCheckException("Cant perform operation, E1 not correct type IMAGE");
				break;
			case INTEGER:
				if (e1.getExpressionType().equals(INTEGER)) {
					binaryExpression.setExpressionType(INTEGER);
				} else throw new TypeCheckException("Can't perform operation, E1 not correct type INTEGER");
				break;
			default:
				throw new TypeCheckException("Wrong type for E0");
			}
			break;
		case DIV:
			if (e0.getExpressionType().equals(INTEGER)) {
				if (e1.getExpressionType().equals(INTEGER)) {
					binaryExpression.setExpressionType(INTEGER);
				}
			} else if (e0.getExpressionType().equals(IMAGE)) {
				if (e1.getExpressionType().equals(INTEGER)) {
					binaryExpression.setExpressionType(IMAGE);
				}
			} else throw new TypeCheckException("Can't perform operation. Type not INTEGER");
			break;
		case TIMES:
			if (e0.getExpressionType().equals(INTEGER)) {
				if (e1.getExpressionType().equals(INTEGER)) {
					binaryExpression.setExpressionType(INTEGER);
				} else if (e1.getExpressionType().equals(IMAGE)) {
					binaryExpression.setExpressionType(IMAGE);
				}
			} else if (e0.getExpressionType().equals(IMAGE)) {
				if (e1.getExpressionType().equals(INTEGER)) {
					binaryExpression.setExpressionType(IMAGE);
				}
			} else throw new TypeCheckException("Can't perform operation. Wrong type");
			break;
		case LT:
		case GT:
		case LE:
		case GE:
			if (e0.getExpressionType().equals(INTEGER)) {
				if (e1.getExpressionType().equals(INTEGER)) {
					binaryExpression.setExpressionType(BOOLEAN);
				}
			} else if (e0.getExpressionType().equals(BOOLEAN)) {
				if (e1.getExpressionType().equals(BOOLEAN)) {
					binaryExpression.setExpressionType(BOOLEAN);
				}
			} else throw new TypeCheckException("Expressions have to be either BOOLEAN or INTEGER");
			break;
		case EQUAL:
		case NOTEQUAL:
			if (e0.getExpressionType() == e1.getExpressionType()) {
				binaryExpression.setExpressionType(BOOLEAN);
			} else throw new TypeCheckException("Expression1 type not same as Expression2 type");
			break;
		case MOD:
            if (e0.getExpressionType() == INTEGER && e1.getExpressionType() == INTEGER) {
                  binaryExpression.setExpressionType(INTEGER);
            } else if (e0.getExpressionType() == IMAGE && e1.getExpressionType() == INTEGER) {
            	binaryExpression.setExpressionType(IMAGE);
            } else throw new TypeCheckException("Error in MOD");
            break;
		case AND:
		case OR:
			if (e0.getExpressionType() == BOOLEAN && e1.getExpressionType() == BOOLEAN) {
                binaryExpression.setExpressionType(BOOLEAN);
			} else throw new TypeCheckException ("Error in AND/OR");
			break;
		default:
			throw new TypeCheckException("Wrong Operator type");
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		symtab.enterScope();
		for (Dec dec : block.getDecs()) {
			dec.visit(this, arg);
		}
		for (Statement statement : block.getStatements()) {
			statement.visit(this, arg);
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		booleanLitExpression.setExpressionType(BOOLEAN);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple tuple;
		tuple = filterOpChain.getArg();
		tuple.visit(this, arg);
		if (tuple.getExprList().size() != 0) {
			throw new TypeCheckException("Condition not satisfied for FilterOpChain");
		}
		filterOpChain.setChainType(IMAGE);
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple tuple;
		tuple = frameOpChain.getArg();
		tuple.visit(this, arg);

		switch (frameOpChain.getFirstToken().kind) {
		case KW_SHOW:
		case KW_HIDE:
			if(tuple.getExprList().size() != 0) {
				throw new TypeCheckException("Condition not satisfied for FrameOpChain");
			}
			frameOpChain.setChainType(NONE);
			break;
		case KW_XLOC:
		case KW_YLOC:
			if(tuple.getExprList().size() != 0) {
				throw new TypeCheckException("Condition not satisfied for FrameOpChain");
			}
			frameOpChain.setChainType(INTEGER);
			break;
		case KW_MOVE:
			if(tuple.getExprList().size() != 2) {
				throw new TypeCheckException("Condition not satisfied for FrameOpChain");
			}
			frameOpChain.setChainType(NONE);
			break;
		default:
			throw new TypeCheckException ("Incorrect type. Compile time error");
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token ident;
		Dec dec;
		ident = identChain.getFirstToken();
		dec = symtab.lookup(ident.getText());
		if(dec == null) {
			throw new TypeCheckException ("Undeclared Identifier");
		}
		identChain.setChainType(Type.getTypeName(dec.getType()));
		identChain.setDec(dec);
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token ident;
		Dec dec;
		ident = identExpression.getFirstToken();
		dec = symtab.lookup(ident.getText());
		if(dec == null) {
			throw new TypeCheckException ("Undeclared Identifier");
		}
		identExpression.setExpressionType(Type.getTypeName(dec.getType()));
		identExpression.setDec(dec);
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e;
		Block b;
		e = ifStatement.getE();
		b = ifStatement.getB();
		e.visit(this, arg);
		b.visit(this, arg);
		if (e.getExpressionType() != BOOLEAN) {
			throw new TypeCheckException("Wrong Expression type");
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		intLitExpression.setExpressionType(INTEGER);
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e;
		e = sleepStatement.getE();
		e.visit(this, arg);
		if (e.getExpressionType() != INTEGER) {
			throw new TypeCheckException("Wrong Expression type");
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e;
		Block b;
		e = whileStatement.getE();
		b = whileStatement.getB();
		e.visit(this, arg);
		b.visit(this, arg);
		if (e.getExpressionType() != BOOLEAN) {
			throw new TypeCheckException("Wrong Expression type");
		}
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token ident;
		ident = declaration.getIdent();
		declaration.setDecType(Type.getTypeName(declaration.getType()));
		boolean isvalid = symtab.insert(ident.getText(), declaration);
		if(isvalid == false)
			throw new TypeCheckException ("Identifier already declared");
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO Auto-generated method stub
		for (ParamDec paramdec : program.getParams())
			paramdec.visit(this, arg);
		program.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		IdentLValue identlvalue;
		Expression e;
		identlvalue = assignStatement.getVar();
		e = assignStatement.getE();
		identlvalue.visit(this, arg);
		e.visit(this, arg);
		if (identlvalue.getIdentLValueType() != e.getExpressionType())
			throw new TypeCheckException ("Incorrect Assignment");
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token ident;
		Dec dec;
		ident = identX.getFirstToken();
		dec = symtab.lookup(ident.getText());
		if (dec == null) {
			throw new TypeCheckException ("Undeclared Identifier");
		}
		identX.setIdentLValueType(Type.getTypeName(dec.getType()));
		identX.setDec(dec);
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token ident;
		ident = paramDec.getIdent();
		paramDec.setDecType(Type.getTypeName(paramDec.getType()));
		//System.err.println(paramDec.getType());
		boolean isvalid = symtab.insert(ident.getText(), paramDec);
		if (isvalid == false)
			throw new TypeCheckException ("Identifier already declared");
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// TODO Auto-generated method stub
		constantExpression.setExpressionType(INTEGER);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple tuple;
		tuple = imageOpChain.getArg();
		tuple.visit(this, arg);

		switch (imageOpChain.getFirstToken().kind) {
		case OP_WIDTH:
		case OP_HEIGHT:
			if(tuple.getExprList().size() != 0) {
				throw new TypeCheckException("Condition not satisfied for ImageOpChain");
			}
			imageOpChain.setChainType(INTEGER);
			break;
		case KW_SCALE:
			if(tuple.getExprList().size() != 1) {
				throw new TypeCheckException("Condition not satisfied for ImageOpChain");
			}
			imageOpChain.setChainType(IMAGE);
			break;
		default:
			throw new TypeCheckException ("Incorrect type. Compile time error");
		}
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// TODO Auto-generated method stub
		for (Expression e : tuple.getExprList()) {
			e.visit(this, arg);
			if (e.getExpressionType() != INTEGER)
				throw new TypeCheckException("Expression in Tuple is not Integer");
		}
		return null;
	}


}
