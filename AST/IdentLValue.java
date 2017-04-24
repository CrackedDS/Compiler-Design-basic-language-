package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public class IdentLValue extends ASTNode {
	
	private TypeName type;
	private Dec dec;
	
	public TypeName getIdentLValueType() {
		return type;
	} 
	
	public void setIdentLValueType(TypeName type) {
		this.type = type;
	}
	
	public void setDec(Dec dec) {
		this.dec = dec;
	}
	
	public Dec getDec() {
		return dec;
	}
	
	public IdentLValue(Token firstToken) {
		super(firstToken);
	}
	
	@Override
	public String toString() {
		return "IdentLValue [firstToken=" + firstToken + "]";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentLValue(this,arg);
	}

	public String getText() {
		return firstToken.getText();
	}

}
