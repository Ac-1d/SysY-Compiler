package Symbol;

import frontend.SymbolHandler;

public class Symbol {
    private int id;
    private static int autoIncreId;
    private SymbolTable symbolTable;
    protected String word;

    public Symbol(String word) {
        SymbolHandler instance = SymbolHandler.getInstance();
        this.id = autoIncreId++;
        this.word = word;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void print() {
        System.out.println(word);
    }
    
}
