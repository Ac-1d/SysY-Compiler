package Symbol;

import token.Token;

public class Symbol {
    private int id;
    private static int autoIncreId;
    private SymbolTable symbolTable;
    private Token identToken;
    protected String word;
    private boolean isGlobal;
    private int reg;

    public Symbol(Token identToken) {
        this.id = autoIncreId++;
        this.identToken = identToken;
        this.word = identToken.getWord();
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        isGlobal = symbolTable.getFatherSymbolTable() == null ? true : false;
    }

    public void print() {
        System.out.println(word);
    }

    public String getWord() {
        return word;
    }

    public int getLineNum() {
        return identToken.getLineNum();
    }

    public Token getIdentToken() {
        return identToken;
    }

    public void setReg(int reg) {
        this.reg = reg;
    }

    public int getReg() {
        return reg;
    }

    public boolean isGlobal() {
        return isGlobal;
    }
    
}
