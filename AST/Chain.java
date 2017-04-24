package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;


public abstract class Chain extends Statement {
	
	private TypeName type;
	
	public TypeName getChainType() {
		return type;
	} 
	
	public void setChainType(TypeName type) {
		this.type = type;
	}
	
	public Chain(Token firstToken) {
		super(firstToken);
	}

}
