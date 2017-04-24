package cop5556sp17;



import java.util.HashMap;
import java.util.Stack;

import cop5556sp17.AST.Dec;


public class SymbolTable {

	//TODO  add fields
	private class SymbolAttr {
		private int scope;
		private Dec dec;

		SymbolAttr(int scope, Dec dec) {
			this.scope = scope;
			this.dec = dec;
		}

		public int getScope() {
			return scope;
		}

		public Dec getDec() {
			return dec;
		}
	}

	private HashMap <String, Stack <SymbolAttr>> symboltable;
	private Stack <Integer> scopestack;
	private int scopecount;
	/**
	 * to be called when block entered
	 */
	public void enterScope(){
		//TODO:  IMPLEMENT THIS
		scopestack.push(++scopecount);
	}

	/**
	 * leaves scope
	 */
	public void leaveScope(){
		//TODO:  IMPLEMENT THIS
		scopestack.pop();
	}

	public boolean insert(String ident, Dec dec){
		//TODO:  IMPLEMENT THIS
		Stack<SymbolAttr> attr;
		attr = symboltable.get(ident);
		if (attr == null) {
			attr = new Stack<SymbolAttr>();
			attr.push(new SymbolAttr(scopestack.peek(), dec));
			symboltable.put(ident, attr);
		} else if (attr.peek().getScope() == scopestack.peek()){
			return false;
		} else {
			attr.push(new SymbolAttr(scopestack.peek(), dec));
		}
		return true;
	}

	public Dec lookup(String ident){
		//TODO:  IMPLEMENT THIS
		Stack<SymbolAttr> symattr;
		symattr = symboltable.get(ident);
		if (symattr != null) {
			for (int i = symattr.size() - 1; i >= 0; i--) {
				if (scopestack.contains(symattr.get(i).getScope())) {
					return symattr.get(i).getDec();
				}
			}
		}
		return null;
	}

	public SymbolTable() {
		//TODO:  IMPLEMENT THIS
		scopecount = 0;
		scopestack = new Stack <Integer>();
		scopestack.push(scopecount);
		symboltable = new HashMap <String, Stack <SymbolAttr>>();
	}

	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return "";
	}

}
