package Symbol;

import token.Token;

public class Symbol {
    private int id;
    private static int autoIncreId;
    private SymbolTable symbolTable;
    private Token identToken;
    protected String word;

    public Symbol(Token identToken) {
        this.id = autoIncreId++;
        this.identToken = identToken;
        this.word = identToken.getWord();
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
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
    
}
